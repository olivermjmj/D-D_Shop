package app.controllers;

import app.dao.AdminActionLogDAO;
import app.entities.AdminActionLog;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class AdminActionLogController {

    private static final AdminActionLogDAO logDAO = new AdminActionLogDAO();

    public static void addRoutes(Javalin app) {

        app.get("/admin-logs", AdminActionLogController::getAll);
        app.get("/admin-logs/{id}", AdminActionLogController::getById);
        app.get("/admin-logs/admin/{adminId}", AdminActionLogController::getAllByAdminId);

        app.post("/admin-logs", AdminActionLogController::create);
        app.delete("/admin-logs/{id}", AdminActionLogController::delete);
    }

    public static void getAll(Context ctx) {

        List<AdminActionLog> logs = logDAO.getAll();
        ctx.status(200).json(logs);
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<AdminActionLog> log = logDAO.getById(id);

        if (log.isPresent()) {

            ctx.status(200).json(log.get());
        } else {
            ctx.status(404).result("Log not found");
        }
    }

    public static void getAllByAdminId(Context ctx) {

        int adminId = Integer.parseInt(ctx.pathParam("adminId"));
        ctx.status(200).json(logDAO.getAllByAdminId(adminId));
    }

    public static void create(Context ctx) {
        try {

            AdminActionLog log = ctx.bodyAsClass(AdminActionLog.class);
            AdminActionLog created = logDAO.create(log);

            ctx.status(201).json(created);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create log");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            logDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete log");
        }
    }
}