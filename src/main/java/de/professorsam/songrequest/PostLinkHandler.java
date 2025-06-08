package de.professorsam.songrequest;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Collections;

public class PostLinkHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) {
        String link = context.formParam("link");
        if(link == null || link.isBlank()){
            context.result("Link ist ungültig!");
            context.status(400);
            return;
        }
        String videoid = SongRequest.getInstance().getYoutubeHandler().getIDFromLink(link);
        if(videoid == null || videoid.isBlank()){
            context.result("Link ist ungültig!");
            context.status(400);
            return;
        }
        try {
            Duration duration = SongRequest.getInstance().getYoutubeHandler().getYouTubeVideoDuration(videoid);
            if(duration.minus(Duration.ofSeconds(30)).isNegative()){
                context.result("Das Video ist nicht lang genug");
                context.status(400);
                return;
            }
            VideoComponentCtx componentCtx = new VideoComponentCtx(videoid, duration.toSeconds());
            context.render("video-component.jte", Collections.singletonMap("context", componentCtx));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            context.status(500);
            context.result(e.getMessage());
        }
    }
}
