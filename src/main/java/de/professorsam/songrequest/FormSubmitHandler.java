package de.professorsam.songrequest;

import de.professorsam.songrequest.data.Course;
import de.professorsam.songrequest.data.Student;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class FormSubmitHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) {
        String videoid = ctx.formParam("videoId");
        int startTime = Integer.parseInt(ctx.formParam("startTime") == null ? "0" : ctx.formParam("startTime"));
        if(videoid == null || videoid.isEmpty()) {
            ctx.status(400);
            return;
        }
        String studentId = ctx.cookieStore().get("studentid");
        if (studentId == null || studentId.isEmpty()) {
            ctx.status(400);
            ctx.result("You must provide a student ID");
            return;
        }
        Student student = null;
        for(Course c : SongRequest.getInstance().getCourses()){
            for(Student s : c.students()){
                if(s.id().equals(studentId)){
                    student = s;
                    break;
                }
            }
        }
        if(student == null) {
            ctx.status(400);
            return;
        }
        student.videoID(videoid);
        student.startTime(startTime);
        SongRequest.getInstance().getDatabaseHandler().save();
        ctx.redirect("/done");
        ctx.status(200);
        System.out.println("Received Song for Student " + studentId + " (" + student.name() + ")! Song: " + videoid + " - " + startTime);
    }
}
