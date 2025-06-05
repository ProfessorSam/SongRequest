package de.professorsam.songrequest;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class AdminGetHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) {
        context.render("admin.jte", Collections.singletonMap("ctx", new AdminCtx(SongRequest.getInstance().getCourses())));
    }
}
