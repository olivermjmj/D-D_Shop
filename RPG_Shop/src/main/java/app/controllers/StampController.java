package app.controllers;

import app.dto.stamp.CreateStampDTO;
import app.dto.stamp.UpdateStampDTO;
import app.exceptions.ApiException;
import app.service.impl.StampServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class StampController {

    private static final StampServiceImpl stampService = new StampServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/stamps", StampController::getAll);
        app.get("/stamps/{id}", StampController::getById);

        app.post("/stamps", StampController::create);
        app.put("/stamps/{id}", StampController::update);
        app.delete("/stamps/{id}", StampController::delete);
    }

    public static void getAll(Context ctx) {

        ctx.future(() ->
                stampService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                stampService.getById(id).thenAccept(stamp ->
                        ctx.json(stamp.orElseThrow(() ->
                                new ApiException(404, "Stamp not found")))));
    }

    public static void create(Context ctx) {

        CreateStampDTO dto = ctx.bodyAsClass(CreateStampDTO.class);

        ctx.future(() ->
                stampService.create(dto).thenAccept(stamp -> {

                    ctx.status(201);
                    ctx.json(stamp);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateStampDTO dto = ctx.bodyAsClass(UpdateStampDTO.class);

        ctx.future(() ->
                stampService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                stampService.delete(id).thenRun(() -> ctx.status(204)));
    }
}