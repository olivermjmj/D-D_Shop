package app.controllers;

import app.dto.qualityCheck.CreateQualityCheckDTO;
import app.dto.qualityCheck.UpdateQualityCheckDTO;
import app.entities.enums.QualityStatus;
import app.exceptions.ApiException;
import app.service.impl.QualityCheckServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class QualityCheckController {

    private static final QualityCheckServiceImpl qualityCheckService = new QualityCheckServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/quality-checks", QualityCheckController::getAll);
        app.get("/quality-checks/{id}", QualityCheckController::getById);

        app.get("/quality-checks/item/{itemId}", QualityCheckController::getAllByItemId);
        app.get("/quality-checks/status/{status}", QualityCheckController::getAllByStatus);
        app.get("/quality-checks/approved-by/{userId}", QualityCheckController::getAllByApprovedById);

        app.post("/quality-checks", QualityCheckController::create);
        app.put("/quality-checks/{id}", QualityCheckController::update);
        app.delete("/quality-checks/{id}", QualityCheckController::delete);
    }

    public static void getAll(Context ctx) {

        ctx.future(() ->
                qualityCheckService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                qualityCheckService.getById(id).thenAccept(check ->
                        ctx.json(check.orElseThrow(() ->
                                new ApiException(404, "Quality check not found")))));
    }

    public static void getAllByItemId(Context ctx) {

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));

        ctx.future(() ->
                qualityCheckService.getAllByItemId(itemId).thenAccept(ctx::json));
    }

    public static void getAllByStatus(Context ctx) {

        try {

            QualityStatus status = QualityStatus.valueOf(ctx.pathParam("status").toUpperCase());

            ctx.future(() ->
                    qualityCheckService.getAllByStatus(status).thenAccept(ctx::json));
        } catch (IllegalArgumentException e) {
            throw new ApiException(400, "Invalid quality status");
        }
    }

    public static void getAllByApprovedById(Context ctx) {

        int userId = Integer.parseInt(ctx.pathParam("userId"));

        ctx.future(() ->
                qualityCheckService.getAllByApprovedById(userId).thenAccept(ctx::json)
        );
    }

    public static void create(Context ctx) {

        CreateQualityCheckDTO dto = ctx.bodyAsClass(CreateQualityCheckDTO.class);

        ctx.future(() ->
                qualityCheckService.create(dto).thenAccept(check -> {

                    ctx.status(201);
                    ctx.json(check);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateQualityCheckDTO dto = ctx.bodyAsClass(UpdateQualityCheckDTO.class);

        ctx.future(() ->
                qualityCheckService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                qualityCheckService.delete(id).thenRun(() ->
                        ctx.status(204)));
    }
}