package de.professorsam.songrequest;

import de.professorsam.songrequest.data.Course;
import de.professorsam.songrequest.data.Student;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class IndexGetHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String studentId = ctx.pathParam("student");
        if (studentId == null || studentId.isEmpty()) {
            ctx.status(404);
            return;
        }
        Course course = null;
        Student student = null;
        for(Course c : SongRequest.getInstance().getCourses()){
            for(Student s : c.students()){
                if(s.id().equals(studentId)){
                    student = s;
                    course = c;
                    break;
                }
            }
        }
        if(student == null){
            ctx.status(404);
            return;
        }
        ctx.cookieStore().set("studentid", studentId);
        ctx.render("index.jte", Collections.singletonMap("context", new IndexCtx(student.name(), course.name())));
    }
}