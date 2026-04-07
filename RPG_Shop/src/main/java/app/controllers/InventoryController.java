package app.controllers;

import app.dao.InventoryDAO;
import app.entities.Inventory;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class InventoryController {

    private static final InventoryDAO inventoryDAO = new InventoryDAO();

    public static void addRoutes(Javalin app) {

        app.get("/inventories", InventoryController::getAll);
        app.get("/inventories/{id}", InventoryController::getById);
        app.get("/inventories/item/{itemId}", InventoryController::getByItemId);

        app.post("/inventories", InventoryController::create);
        app.put("/inventories/{id}", InventoryController::update);
        app.delete("/inventories/{id}", InventoryController::delete);
    }

    public static void getAll(Context ctx) {

        List<Inventory> inventories = inventoryDAO.getAll();
        ctx.status(200).json(inventories);
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<Inventory> inventory = inventoryDAO.getById(id);

        if (inventory.isPresent()) {

            ctx.status(200).json(inventory.get());
        } else {
            ctx.status(404).result("Inventory not found");
        }
    }

    public static void getByItemId(Context ctx) {

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));
        Optional<Inventory> inventory = inventoryDAO.getByItemId(itemId);

        if (inventory.isPresent()) {

            ctx.status(200).json(inventory.get());
        } else {
            ctx.status(404).result("Inventory not found");
        }
    }

    public static void create(Context ctx) {

        try {

            Inventory inventory = ctx.bodyAsClass(Inventory.class);
            Inventory created = inventoryDAO.create(inventory);

            ctx.status(201).json(created);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create inventory");
        }
    }

    public static void update(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            Inventory inventory = ctx.bodyAsClass(Inventory.class);
            inventory.setId(id);
            Inventory updated = inventoryDAO.update(inventory);

            ctx.status(200).json(updated);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not update inventory");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            inventoryDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete inventory");
        }
    }
}