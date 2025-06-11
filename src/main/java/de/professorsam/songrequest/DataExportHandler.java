package de.professorsam.songrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DataExportHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String json = mapper.writeValueAsString(SongRequest.getInstance().getCourses());
            ctx.json(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert courses to JSON", e);
        }

    }
}
