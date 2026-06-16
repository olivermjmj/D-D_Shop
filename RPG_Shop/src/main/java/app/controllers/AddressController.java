package app.controllers;

import app.dto.address.CreateAddressDTO;
import app.dto.address.UpdateAddressDTO;
import app.exceptions.ApiException;
import app.service.impl.AddressServiceImpl;
import app.service.security.AuthMiddleware;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AddressController {

    private static final AddressServiceImpl addressService = new AddressServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/addresses", AddressController::getAll);
        app.get("/addresses/{id}", AddressController::getById);

        app.post("/addresses", AddressController::create);
        app.put("/addresses/{id}", AddressController::update);
        app.delete("/addresses/{id}", AddressController::delete);
    }

    public static void getAll(Context ctx) {
        AuthMiddleware.requireAdmin(ctx);

        ctx.future(() ->
                addressService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {
        AuthMiddleware.requireAdmin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                addressService.getById(id).thenAccept(address ->
                        ctx.json(address.orElseThrow(() ->
                                new ApiException(404, "Address not found")))));
    }

    public static void create(Context ctx) {
        AuthMiddleware.requireAdmin(ctx);

        CreateAddressDTO dto = ctx.bodyAsClass(CreateAddressDTO.class);

        ctx.future(() ->
                addressService.create(dto).thenAccept(address -> {

                    ctx.status(201);
                    ctx.json(address);
                })
        );
    }

    public static void update(Context ctx) {
        AuthMiddleware.requireAdmin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateAddressDTO dto = ctx.bodyAsClass(UpdateAddressDTO.class);

        ctx.future(() ->
                addressService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {
        AuthMiddleware.requireAdmin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                addressService.delete(id).thenRun(() -> ctx.status(204)));
    }
}