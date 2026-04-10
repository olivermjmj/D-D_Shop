package app.controllers;

import app.dto.item.CreateItemDTO;
import app.dto.item.UpdateItemDTO;
import app.exceptions.ApiException;
import app.service.impl.ItemServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ItemController {

    private static final ItemServiceImpl itemService = new ItemServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/items", ItemController::getAll);
        app.get("/items/{id}", ItemController::getById);

        app.get("/items/category/{categoryId}", ItemController::getAllByCategoryId);
        app.get("/items/supplier/{supplierId}", ItemController::getAllBySupplierId);
        app.get("/items/source/{externalSource}", ItemController::getAllByExternalSource);
        app.get("/items/external/{externalId}", ItemController::getByExternalId);
        app.get("/items/external/{externalId}/source/{externalSource}", ItemController::getByExternalIdAndSource);

        app.post("/items", ItemController::create);
        app.put("/items/{id}", ItemController::update);
        app.delete("/items/{id}", ItemController::delete);
    }

    public static void getAll(Context ctx) {

        ctx.future(() ->
                itemService.getAll().thenAccept(ctx::json)
        );
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                itemService.getById(id).thenAccept(item ->
                        ctx.json(item.orElseThrow(() ->
                                new ApiException(404, "Item not found"))))
        );
    }

    public static void getAllByCategoryId(Context ctx) {

        int categoryId = Integer.parseInt(ctx.pathParam("categoryId"));

        ctx.future(() ->
                itemService.getAllByCategoryId(categoryId).thenAccept(ctx::json)
        );
    }

    public static void getAllBySupplierId(Context ctx) {

        int supplierId = Integer.parseInt(ctx.pathParam("supplierId"));

        ctx.future(() ->
                itemService.getAllBySupplierId(supplierId).thenAccept(ctx::json)
        );
    }

    public static void getAllByExternalSource(Context ctx) {

        String externalSource = ctx.pathParam("externalSource");

        ctx.future(() ->
                itemService.getAllByExternalSource(externalSource).thenAccept(ctx::json));
    }

    public static void getByExternalId(Context ctx) {

        String externalId = ctx.pathParam("externalId");

        ctx.future(() ->
                itemService.getByExternalId(externalId).thenAccept(ctx::json));
    }

    public static void getByExternalIdAndSource(Context ctx) {

        String externalId = ctx.pathParam("externalId");
        String externalSource = ctx.pathParam("externalSource");

        ctx.future(() ->
                itemService.getByExternalIdAndSource(externalId, externalSource).thenAccept(ctx::json));
    }

    public static void create(Context ctx) {

        CreateItemDTO dto = ctx.bodyAsClass(CreateItemDTO.class);

        ctx.future(() ->
                itemService.create(dto).thenAccept(item -> {

                    ctx.status(201);
                    ctx.json(item);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateItemDTO dto = ctx.bodyAsClass(UpdateItemDTO.class);

        ctx.future(() ->
                itemService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                itemService.delete(id).thenRun(() -> ctx.status(204)));
    }
}