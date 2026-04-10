package app.controllers;

import app.dto.itemCategory.CreateItemCategoryDTO;
import app.dto.itemCategory.UpdateItemCategoryDTO;
import app.exceptions.ApiException;
import app.service.impl.ItemCategoryServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ItemCategoryController {

    private static final ItemCategoryServiceImpl itemCategoryService = new ItemCategoryServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/categories", ItemCategoryController::getAll);
        app.get("/categories/{id}", ItemCategoryController::getById);

        app.post("/categories", ItemCategoryController::create);
        app.put("/categories/{id}", ItemCategoryController::update);
        app.delete("/categories/{id}", ItemCategoryController::delete);
    }

    public static void getAll(Context ctx) {

        ctx.future(() -> itemCategoryService.getAll().thenAccept(ctx::json)
        );
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                itemCategoryService.getById(id).thenAccept(category ->
                        ctx.json(category.orElseThrow(() ->
                                new ApiException(404, "Category not found")))));
    }

    public static void create(Context ctx) {

        CreateItemCategoryDTO dto = ctx.bodyAsClass(CreateItemCategoryDTO.class);

        ctx.future(() ->
                itemCategoryService.create(dto).thenAccept(category -> {

                    ctx.status(201);
                    ctx.json(category);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateItemCategoryDTO dto = ctx.bodyAsClass(UpdateItemCategoryDTO.class);

        ctx.future(() ->
                itemCategoryService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                itemCategoryService.delete(id).thenRun(() -> ctx.status(204)));
    }
}