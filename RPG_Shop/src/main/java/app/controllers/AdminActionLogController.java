package app.controllers;

import app.dto.adminActionLog.CreateAdminActionLogDTO;
import app.exceptions.ApiException;
import app.service.impl.AdminActionLogServiceImpl;
import app.service.security.AuthMiddleware;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminActionLogController {

    private static final AdminActionLogServiceImpl adminActionLogService = new AdminActionLogServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/admin-logs", AdminActionLogController::getAll);
        app.get("/admin-logs/{id}", AdminActionLogController::getById);
        app.get("/admin-logs/admin/{adminId}", AdminActionLogController::getAllByAdminId);

        app.post("/admin-logs", AdminActionLogController::create);

        // No update/delete routes for logs
    }

    public static void getAll(Context ctx) {

        AuthMiddleware.requireAdmin(ctx);

        ctx.future(() ->
                adminActionLogService.getAll().thenAccept(ctx::json)
        );
    }

    public static void getById(Context ctx) {

        AuthMiddleware.requireAdmin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                adminActionLogService.getById(id).thenAccept(log ->
                        ctx.json(log.orElseThrow(() ->
                                new ApiException(404, "Log not found")))
                )
        );
    }

    public static void getAllByAdminId(Context ctx) {

        AuthMiddleware.requireAdmin(ctx);

        int adminId = Integer.parseInt(ctx.pathParam("adminId"));

        ctx.future(() ->
                adminActionLogService.getAllByAdminId(adminId).thenAccept(ctx::json)
        );
    }

    public static void create(Context ctx) {

        AuthMiddleware.requireAdmin(ctx);

        Integer adminId = ctx.attribute("userId");

        if (adminId == null) {
            throw new ApiException(401, "Unauthorized");
        }

        CreateAdminActionLogDTO dto = ctx.bodyAsClass(CreateAdminActionLogDTO.class);

        ctx.future(() ->
                adminActionLogService.createForAdmin(adminId, dto)
                        .thenAccept(log -> {
                            ctx.status(201);
                            ctx.json(log);
                        })
        );
    }
}