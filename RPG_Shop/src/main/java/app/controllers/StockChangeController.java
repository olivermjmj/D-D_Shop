package app.controllers;

import app.dto.stockChange.CreateStockChangeDTO;
import app.dto.stockChange.UpdateStockChangeDTO;
import app.exceptions.ApiException;
import app.service.impl.StockChangeServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class StockChangeController {

    private static final StockChangeServiceImpl stockChangeService = new StockChangeServiceImpl();

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

        ctx.future(() -> stockChangeService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                stockChangeService.getById(id).thenAccept(change ->
                        ctx.json(change.orElseThrow(() ->
                                new ApiException(404, "Stock change not found"))))
        );
    }

    public static void getAllByItemId(Context ctx) {

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));

        ctx.future(() ->
                stockChangeService.getAllByItemId(itemId).thenAccept(ctx::json));
    }

    public static void getAllByAdminId(Context ctx) {

        int adminId = Integer.parseInt(ctx.pathParam("adminId"));

        ctx.future(() ->
                stockChangeService.getAllByAdminId(adminId).thenAccept(ctx::json));
    }

    public static void create(Context ctx) {

        CreateStockChangeDTO dto = ctx.bodyAsClass(CreateStockChangeDTO.class);

        ctx.future(() ->
                stockChangeService.create(dto).thenAccept(change -> {

                    ctx.status(201);
                    ctx.json(change);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateStockChangeDTO dto = ctx.bodyAsClass(UpdateStockChangeDTO.class);

        ctx.future(() ->
                stockChangeService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                stockChangeService.delete(id).thenRun(() ->
                        ctx.status(204)));
    }
}