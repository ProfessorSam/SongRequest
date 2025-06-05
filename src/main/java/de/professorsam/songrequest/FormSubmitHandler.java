package de.professorsam.songrequest;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class FormSubmitHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) {
        ctx.formParamMap().forEach((key, val) -> {
            System.out.println(key + ": " + val);
        });
        ctx.status(200);
    }
}
