package app.controllers;

import app.dto.adminActionLog.CreateAdminActionLogDTO;
import app.dto.adminActionLog.UpdateAdminActionLogDTO;
import app.exceptions.ApiException;
import app.service.impl.AdminActionLogServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminActionLogController {

    private static final AdminActionLogServiceImpl adminActionLogService = new AdminActionLogServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/admin-logs", AdminActionLogController::getAll);
        app.get("/admin-logs/{id}", AdminActionLogController::getById);
        app.get("/admin-logs/admin/{adminId}", AdminActionLogController::getAllByAdminId);

        app.post("/admin-logs", AdminActionLogController::create);
        app.put("/admin-logs/{id}", AdminActionLogController::update);
        app.delete("/admin-logs/{id}", AdminActionLogController::delete);
    }

    public static void getAll(Context ctx) {

        ctx.future(() ->
                adminActionLogService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                adminActionLogService.getById(id).thenAccept(log ->
                        ctx.json(log.orElseThrow(() ->
                                new ApiException(404, "Log not found"))))
        );
    }

    public static void getAllByAdminId(Context ctx) {

        int adminId = Integer.parseInt(ctx.pathParam("adminId"));

        ctx.future(() ->
                adminActionLogService.getAllByAdminId(adminId).thenAccept(ctx::json));
    }

    public static void create(Context ctx) {

        CreateAdminActionLogDTO dto = ctx.bodyAsClass(CreateAdminActionLogDTO.class);

        ctx.future(() ->
                adminActionLogService.create(dto).thenAccept(log -> {

                    ctx.status(201);
                    ctx.json(log);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateAdminActionLogDTO dto = ctx.bodyAsClass(UpdateAdminActionLogDTO.class);

        ctx.future(() ->
                adminActionLogService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                adminActionLogService.delete(id).thenRun(() ->
                        ctx.status(204)));
    }
}