package app.controllers;

import app.dao.ItemCategoryDAO;
import app.entities.ItemCategory;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class ItemCategoryController {

    private static final ItemCategoryDAO itemCategoryDAO = new ItemCategoryDAO();

    public static void addRoutes(Javalin app) {

        app.get("/categories", ItemCategoryController::getAll);
        app.get("/categories/{id}", ItemCategoryController::getById);

        app.post("/categories", ItemCategoryController::create);
        app.put("/categories/{id}", ItemCategoryController::update);
        app.delete("/categories/{id}", ItemCategoryController::delete);
    }

    public static void getAll(Context ctx) {

        List<ItemCategory> categories = itemCategoryDAO.getAll();
        ctx.status(200).json(categories);
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<ItemCategory> category = itemCategoryDAO.getById(id);

        if (category.isPresent()) {

            ctx.status(200).json(category.get());
        } else {
            ctx.status(404).result("Category not found");
        }
    }

    public static void create(Context ctx) {

        try {

            ItemCategory category = ctx.bodyAsClass(ItemCategory.class);
            ItemCategory created = itemCategoryDAO.create(category);

            ctx.status(201).json(created);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create category");
        }
    }

    public static void update(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            ItemCategory category = ctx.bodyAsClass(ItemCategory.class);
            category.setId(id);

            ItemCategory updated = itemCategoryDAO.update(category);

            ctx.status(200).json(updated);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not update category");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            itemCategoryDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete category");
        }
    }
}