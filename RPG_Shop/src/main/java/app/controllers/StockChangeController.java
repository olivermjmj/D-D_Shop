package app.controllers;

import app.dao.StockChangeDAO;
import app.entities.StockChange;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class StockChangeController {

    private static final StockChangeDAO stockChangeDAO = new StockChangeDAO();

    public static void addRoutes(Javalin app) {

        app.get("/stock-changes", StockChangeController::getAll);
        app.get("/stock-changes/{id}", StockChangeController::getById);

        app.get("/stock-changes/item/{itemId}", StockChangeController::getAllByItemId);
        app.get("/stock-changes/admin/{adminId}", StockChangeController::getAllByAdminId);

        app.post("/stock-changes", StockChangeController::create);
        app.put("/stock-changes/{id}", StockChangeController::update);
        app.delete("/stock-changes/{id}", StockChangeController::delete);
    }

    public static void getAll(Context ctx) {

        List<StockChange> changes = stockChangeDAO.getAll();
        ctx.status(200).json(changes);
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<StockChange> change = stockChangeDAO.getById(id);

        if (change.isPresent()) {

            ctx.status(200).json(change.get());
        } else {
            ctx.status(404).result("Stock change not found");
        }
    }

    public static void getAllByItemId(Context ctx) {

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));
        ctx.status(200).json(stockChangeDAO.getAllByItemId(itemId));
    }

    public static void getAllByAdminId(Context ctx) {

        int adminId = Integer.parseInt(ctx.pathParam("adminId"));
        ctx.status(200).json(stockChangeDAO.getAllByAdminId(adminId));
    }

    public static void create(Context ctx) {

        try {

            StockChange change = ctx.bodyAsClass(StockChange.class);
            StockChange created = stockChangeDAO.create(change);

            ctx.status(201).json(created);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create stock change");
        }
    }

    public static void update(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            StockChange change = ctx.bodyAsClass(StockChange.class);
            change.setId(id);

            StockChange updated = stockChangeDAO.update(change);

            ctx.status(200).json(updated);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not update stock change");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            stockChangeDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete stock change");
        }
    }
}