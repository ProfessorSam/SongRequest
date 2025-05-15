package de.professorsam.songrequest;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Logger;

public class SongRequest {
    private static final Logger logger = Logger.getLogger("SongRequest");

    public static void main(String[] args) {
        new SongRequest().start();
    }

    public void start() {
        Javalin app = Javalin.create(config -> {
            if(isDevelopmentMode()){
                logger.info("Starting Development Mode");
                config.fileRenderer(new JavalinJte(TemplateEngine.create(new DirectoryCodeResolver(Path.of( "src", "main", "jte")), ContentType.Html)));
            } else {
                logger.info("Starting Production Mode");
                config.fileRenderer(new JavalinJte(TemplateEngine.createPrecompiled(ContentType.Html)));
            }
        });
        app.get("/", ctx -> ctx.render("index.jte", Collections.singletonMap("context", new IndexCtx("John", "Maths LK-1"))));
        app.post("/api/postlink/", new PostLinkHandler());
        app.start(8080);
    }

    private boolean isDevelopmentMode()
    {
        return System.getenv("DEV_MODE") != null && System.getenv("DEV_MODE").equalsIgnoreCase("true");
    }
}
