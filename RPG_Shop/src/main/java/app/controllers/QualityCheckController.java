package app.controllers;

import app.dao.QualityCheckDAO;
import app.entities.QualityCheck;
import app.entities.enums.QualityStatus;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class QualityCheckController {

    private static final QualityCheckDAO qualityCheckDAO = new QualityCheckDAO();

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

        List<QualityCheck> checks = qualityCheckDAO.getAll();
        ctx.status(200).json(checks);
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<QualityCheck> check = qualityCheckDAO.getById(id);

        if (check.isPresent()) {
            ctx.status(200).json(check.get());
        } else {
            ctx.status(404).result("Quality check not found");
        }
    }

    public static void getAllByItemId(Context ctx) {

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));
        ctx.status(200).json(qualityCheckDAO.getAllByItemId(itemId));
    }

    public static void getAllByStatus(Context ctx) {

        try {

            QualityStatus status = QualityStatus.valueOf(ctx.pathParam("status").toUpperCase());
            ctx.status(200).json(qualityCheckDAO.getAllByStatus(status));
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid quality status");
        }
    }

    public static void getAllByApprovedById(Context ctx) {

        int userId = Integer.parseInt(ctx.pathParam("userId"));
        ctx.status(200).json(qualityCheckDAO.getAllByApprovedById(userId));
    }

    public static void create(Context ctx) {

        try {

            QualityCheck check = ctx.bodyAsClass(QualityCheck.class);
            QualityCheck created = qualityCheckDAO.create(check);

            ctx.status(201).json(created);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create quality check");
        }
    }

    public static void update(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            QualityCheck check = ctx.bodyAsClass(QualityCheck.class);
            check.setId(id);

            QualityCheck updated = qualityCheckDAO.update(check);

            ctx.status(200).json(updated);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not update quality check");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            qualityCheckDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete quality check");
        }
    }
}