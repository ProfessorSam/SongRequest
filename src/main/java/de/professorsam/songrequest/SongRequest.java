package de.professorsam.songrequest;

import de.professorsam.songrequest.data.Course;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class SongRequest {
    private static final Logger logger = Logger.getLogger("SongRequest");
    private final YoutubeHandler youtubeHandler;
    private final Dotenv env;
    private static SongRequest instance;
    private final List<Course> courses;

    public static void main(String[] args) {
        new SongRequest().start();
    }

    public SongRequest(){
        env = Dotenv.configure().load();
        youtubeHandler = new YoutubeHandler(env.get("YT_API_KEY"));
        instance = this;
        courses = new ArrayList<>();
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
        app.post("/api/done/", new FormSubmitHandler());
        app.get("/admin/", new AdminGetHandler());
        app.post("/api/students/add", new AdminPostHandler());
        app.post("/api/courses/add", new AdminPostHandler());
        app.post("/api/students/delete", new AdminPostHandler());
        app.start(8080);
    }

    private boolean isDevelopmentMode()
    {
        return env.get("DEV_MODE") != null && env.get("DEV_MODE").equalsIgnoreCase("true");
    }

    public YoutubeHandler getYoutubeHandler() {
        return youtubeHandler;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public static SongRequest getInstance(){
        return instance;
    }
}
