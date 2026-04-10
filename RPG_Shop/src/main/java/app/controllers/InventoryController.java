package app.controllers;

import app.dto.inventory.CreateInventoryDTO;
import app.dto.inventory.UpdateInventoryDTO;
import app.exceptions.ApiException;
import app.service.impl.InventoryServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;

public class InventoryController {

    private static final InventoryServiceImpl inventoryService = new InventoryServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/inventories", InventoryController::getAll);
        app.get("/inventories/{id}", InventoryController::getById);
        app.get("/inventories/item/{itemId}", InventoryController::getByItemId);

        app.post("/inventories", InventoryController::create);
        app.put("/inventories/{id}", InventoryController::update);
        app.delete("/inventories/{id}", InventoryController::delete);

        app.post("/inventories/item/{itemId}/add-stock", InventoryController::addStock);
        app.post("/inventories/item/{itemId}/remove-stock", InventoryController::removeStock);
    }

    public static void getAll(Context ctx) {

        ctx.future(() ->
                inventoryService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                inventoryService.getById(id).thenAccept(inventory ->
                        ctx.json(inventory.orElseThrow(() ->
                                new ApiException(404, "Inventory not found"))))
        );
    }

    public static void getByItemId(Context ctx) {

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));

        ctx.future(() ->
                inventoryService.getByItemId(itemId).thenAccept(ctx::json));
    }

    public static void create(Context ctx) {

        CreateInventoryDTO dto = ctx.bodyAsClass(CreateInventoryDTO.class);

        ctx.future(() ->
                inventoryService.create(dto).thenAccept(inventory -> {

                    ctx.status(201);
                    ctx.json(inventory);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateInventoryDTO dto = ctx.bodyAsClass(UpdateInventoryDTO.class);

        ctx.future(() ->
                inventoryService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                inventoryService.delete(id).thenRun(() -> ctx.status(204)));
    }

    public static void addStock(Context ctx) {

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));
        int amount = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("amount")));

        ctx.future(() ->
                inventoryService.addStock(itemId, amount).thenAccept(ctx::json));
    }

    public static void removeStock(Context ctx) {

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));
        int amount = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("amount")));

        ctx.future(() ->
                inventoryService.removeStock(itemId, amount).thenAccept(ctx::json));
    }
}