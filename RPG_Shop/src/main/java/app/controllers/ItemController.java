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

        ctx.json(itemService.getAll());
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.json(itemService.getById(id).orElseThrow(() -> new ApiException(404, "Item not found")));
    }

    public static void getAllByCategoryId(Context ctx) {

        int categoryId = Integer.parseInt(ctx.pathParam("categoryId"));

        ctx.json(itemService.getAllByCategoryId(categoryId));
    }

    public static void getAllBySupplierId(Context ctx) {

        int supplierId = Integer.parseInt(ctx.pathParam("supplierId"));

        ctx.json(itemService.getAllBySupplierId(supplierId));
    }

    public static void getAllByExternalSource(Context ctx) {

        String externalSource = ctx.pathParam("externalSource");

        ctx.json(itemService.getAllByExternalSource(externalSource));
    }

    public static void getByExternalId(Context ctx) {

        String externalId = ctx.pathParam("externalId");

        ctx.json(itemService.getByExternalId(externalId));
    }

    public static void getByExternalIdAndSource(Context ctx) {

        String externalId = ctx.pathParam("externalId");
        String externalSource = ctx.pathParam("externalSource");

        ctx.json(itemService.getByExternalIdAndSource(externalId, externalSource));
    }

    public static void create(Context ctx) {

        CreateItemDTO dto = ctx.bodyAsClass(CreateItemDTO.class);

        ctx.status(201).json(itemService.create(dto));
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        UpdateItemDTO dto = ctx.bodyAsClass(UpdateItemDTO.class);
        ctx.json(itemService.update(id, dto));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        itemService.delete(id);
        ctx.status(204);
    }
}