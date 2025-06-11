package de.professorsam.songrequest;

import de.professorsam.songrequest.data.Course;
import de.professorsam.songrequest.data.DatabaseHandler;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SongRequest {
    private static final Logger logger = Logger.getLogger("SongRequest");
    private final YoutubeHandler youtubeHandler;
    private final Dotenv env;
    private static SongRequest instance;
    private final List<Course> courses;
    private DatabaseHandler databaseHandler;

    public static void main(String[] args) {
        new SongRequest().start();
    }

    public SongRequest(){
        env = Dotenv.configure().ignoreIfMissing().load();
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
        app.get("/s/{student}", new IndexGetHandler());
        app.post("/api/postlink/", new PostLinkHandler());
        app.get("/admin/", new AdminGetHandler());
        app.post("/api/done/", new FormSubmitHandler());
        app.post("/api/students/add", new AdminPostHandler());
        app.post("/api/courses/add", new AdminPostHandler());
        app.post("/api/students/delete", new AdminPostHandler());
        app.get("/done", ctx -> ctx.render("done.jte"));
        app.get("/api/export", new DataExportHandler());
        app.start(8080);
        String host = env.get("DB_HOST");
        int port = Integer.parseInt(env.get("DB_PORT"));
        String database = env.get("DB_DATABASE");
        String username = env.get("DB_USER");
        String password = env.get("DB_PASSWORD");
        databaseHandler = new DatabaseHandler(host, port, database, username, password);
        try {
            databaseHandler.innit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        databaseHandler.load();
    }

    private boolean isDevelopmentMode()
    {
        return env.get("DEV_MODE") != null && env.get("DEV_MODE").equalsIgnoreCase("true");
    }

    public String getAppHost(){
        return env.get("APP_HOST");
    }
    public DatabaseHandler getDatabaseHandler(){
        return databaseHandler;
    }
    public YoutubeHandler getYoutubeHandler() {
        return youtubeHandler;
    }

    public synchronized List<Course> getCourses() {
        return courses;
    }

    public static SongRequest getInstance(){
        return instance;
    }
}
