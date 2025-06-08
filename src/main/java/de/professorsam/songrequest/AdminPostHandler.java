package de.professorsam.songrequest;

import de.professorsam.songrequest.data.Course;
import de.professorsam.songrequest.data.Student;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class AdminPostHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) {
        if(ctx.path().equals("/api/students/add")) {
            String name = ctx.formParam("name");
            String courseId = ctx.formParam("courseId");
            if(courseId == null || courseId.isEmpty() || name == null || name.isEmpty()) {
                ctx.result("ERROR: " + ctx.body());
                return;
            }
            for(Course course : SongRequest.getInstance().getCourses()){
                if(!course.id().equals(courseId)){
                    continue;
                }
                course.students().add(new Student(UUID.randomUUID().toString(), name, "", 0));
            }
            ctx.redirect("/admin");
            SongRequest.getInstance().getDatabaseHandler().save();
            return;
        }
        if(ctx.path().equals("/api/courses/add")) {
            String name = ctx.formParam("name");
            if(name == null) {
                ctx.result("ERROR: " + ctx.body());
                return;
            }
            SongRequest.getInstance().getCourses().add(new Course(UUID.randomUUID().toString(), name, new ArrayList<>()));
            ctx.redirect("/admin");
            SongRequest.getInstance().getDatabaseHandler().save();
            return;
        }
        if(ctx.path().equals("/api/students/delete")) {
            String id = ctx.formParam("id");
            if(id == null || id.isEmpty()) {
                ctx.result("ERROR");
                return;
            }
            for(Course course : SongRequest.getInstance().getCourses()){
                for(Student student : course.students()){
                    if(student.id().equals(id)){
                        course.students().remove(student);
                        SongRequest.getInstance().getDatabaseHandler().removeStudent(student);
                        break;
                    }
                }
            }
        }
    }
}
