package app.controllers;

import app.dto.discount.CreateDiscountDTO;
import app.dto.discount.UpdateDiscountDTO;
import app.exceptions.ApiException;
import app.service.impl.DiscountServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class DiscountController {

    private static final DiscountServiceImpl discountService = new DiscountServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/discounts", DiscountController::getAll);
        app.get("/discounts/{id}", DiscountController::getById);

        app.post("/discounts", DiscountController::create);
        app.put("/discounts/{id}", DiscountController::update);
        app.delete("/discounts/{id}", DiscountController::delete);
    }

    public static void getAll(Context ctx) {

        ctx.future(() ->
                discountService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                discountService.getById(id).thenAccept(discount ->
                        ctx.json(discount.orElseThrow(() ->
                                new ApiException(404, "Discount not found"))))
        );
    }

    public static void create(Context ctx) {

        CreateDiscountDTO dto = ctx.bodyAsClass(CreateDiscountDTO.class);

        ctx.future(() ->
                discountService.create(dto).thenAccept(discount -> {

                    ctx.status(201);
                    ctx.json(discount);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateDiscountDTO dto = ctx.bodyAsClass(UpdateDiscountDTO.class);

        ctx.future(() ->
                discountService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                discountService.delete(id).thenRun(() -> ctx.status(204)));
    }
}