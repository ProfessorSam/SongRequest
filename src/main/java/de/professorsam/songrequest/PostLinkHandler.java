package de.professorsam.songrequest;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class PostLinkHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) {
        String link = context.formParam("link");
        if(link == null || link.isBlank()){
            context.status(400);
            return;
        }
        VideoComponentCtx componentCtx = new VideoComponentCtx("dQw4w9WgXcQ", 111);
        context.render("video-component.jte", Collections.singletonMap("context", componentCtx));
    }
}
