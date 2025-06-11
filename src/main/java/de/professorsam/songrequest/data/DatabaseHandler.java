package de.professorsam.songrequest.data;

import de.professorsam.songrequest.SongRequest;
import org.mariadb.jdbc.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DatabaseHandler {

    private final String host, database, username, password;
    private final int port;
    private Connection connection;
    private final ConcurrentLinkedQueue<PreparedStatement> tasks = new ConcurrentLinkedQueue<>();
    private Thread thread;

    public DatabaseHandler(String host, int port, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public void innit() throws SQLException {
        connection = connect();
        PreparedStatement statement = connection.prepareStatement("""
    CREATE TABLE IF NOT EXISTS Courses (
        id UUID PRIMARY KEY NOT NULL,
        name Text NOT NULL
    );
    """);
        statement.executeUpdate();
        statement.close();
        statement = connection.prepareStatement("""
        CREATE TABLE IF NOT EXISTS Students (
            id UUID PRIMARY KEY NOT NULL,
            name Text NOT NULL,
            videoid Text,
            startTime Text,
            courseid TEXT
        )
    """);
        statement.executeUpdate();
        statement.close();
    }

    public void save() {
        List<Course> list = new ArrayList<>(SongRequest.getInstance().getCourses());
        for(Course course : list){
            tasks.add(saveCourse(course));
            for(Student student : course.students()){
                tasks.add(saveStudent(student, course.id()));
            }
        }
        startTasks();
    }

    public void removeStudent(Student student) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Students WHERE id = ? LIMIT 1");
            statement.setString(1, student.id());
            tasks.add(statement);
            startTasks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void startTasks(){
        if(thread == null){
            thread = new Thread(() -> {
                while(!tasks.isEmpty()){
                    PreparedStatement statement = tasks.remove();
                    try {
                        statement.execute();
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                thread = null;
            });
            thread.start();
        }
    }

    public void load(){
        long start = System.currentTimeMillis();
        try {
            PreparedStatement statement = connection.prepareStatement("""
            SELECT Courses.id, Courses.name, Students.id, Students.name, Students.videoid, Students.startTime
            FROM Courses
            LEFT JOIN Students ON Courses.id = Students.courseid
            """);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                String courseID = resultSet.getString("Courses.id");
                String courseName = resultSet.getString("Courses.name");
                String studentID = resultSet.getString("Students.id");
                String studentName = resultSet.getString("Students.name");
                String videoID = resultSet.getString("Students.videoid");
                int startTime = resultSet.getInt("Students.startTime");
                Course course = new Course(courseID, courseName, new ArrayList<>());
                Student student = new Student(studentID, studentName, videoID, startTime);
                boolean found = false;
                for(Course c : SongRequest.getInstance().getCourses()){
                    if(c.id().equals(course.id())){
                        found = true;
                        course = c;
                        break;
                    }
                }
                if(!found){
                    if(student.id() != null){
                        course.students().add(student);
                    }
                    SongRequest.getInstance().getCourses().add(course);
                    continue;
                }
                if(student.id() != null){
                    course.students().add(student);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start + "ms to load");
    }

    private PreparedStatement saveCourse(Course course) {
        try {
            PreparedStatement statement = connection.prepareStatement("REPLACE INTO Courses (id, name) VALUES (?,?);");
            statement.setObject(1, course.id());
            statement.setString(2, course.name());
            return statement;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PreparedStatement saveStudent(Student student, String courseID) {
        try {
            PreparedStatement statement = connection.prepareStatement("REPLACE INTO Students (id, name, videoid, startTime, courseid) VALUES (?,?,?,?,?);");
            statement.setObject(1, student.id());
            statement.setString(2, student.name());
            statement.setString(3, student.videoID() == null ? "" : student.videoID());
            statement.setInt(4, student.startTime());
            statement.setString(5, courseID);
            return statement;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Connection connect(){
        return connect(1);
    }

    private Connection connect(int tries)
    {
        try {
            return (Connection) DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database, username, password);
        } catch (SQLException e) {
            if(tries <= 10){
                System.out.println("Failed to connect to " + host + ":" + port + "/" + database +"! Try nr. " + tries);
                tries++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                return connect(tries);
            }
            throw new RuntimeException(e);
        }
    }

}
