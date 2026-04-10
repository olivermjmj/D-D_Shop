package app.controllers;

import app.dto.supplier.CreateSupplierDTO;
import app.dto.supplier.UpdateSupplierDTO;
import app.exceptions.ApiException;
import app.service.impl.SupplierServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class SupplierController {

    private static final SupplierServiceImpl supplierService = new SupplierServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/suppliers", SupplierController::getAll);
        app.get("/suppliers/{id}", SupplierController::getById);

        app.post("/suppliers", SupplierController::create);
        app.put("/suppliers/{id}", SupplierController::update);
        app.delete("/suppliers/{id}", SupplierController::delete);
    }

    public static void getAll(Context ctx) {

        ctx.future(() ->
                supplierService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                supplierService.getById(id).thenAccept(supplier ->
                        ctx.json(supplier.orElseThrow(() ->
                                new ApiException(404, "Supplier not found")))));
    }

    public static void create(Context ctx) {

        CreateSupplierDTO dto = ctx.bodyAsClass(CreateSupplierDTO.class);

        ctx.future(() ->
                supplierService.create(dto).thenAccept(supplier -> {

                    ctx.status(201);
                    ctx.json(supplier);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateSupplierDTO dto = ctx.bodyAsClass(UpdateSupplierDTO.class);

        ctx.future(() ->
                supplierService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                supplierService.delete(id).thenRun(() ->
                        ctx.status(204)));
    }
}