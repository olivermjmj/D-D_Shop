package app.controllers;

import app.dto.orderItem.CreateOrderItemDTO;
import app.dto.orderItem.UpdateOrderItemDTO;
import app.exceptions.ApiException;
import app.service.impl.OrderItemServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderItemController {

    private static final OrderItemServiceImpl orderItemService = new OrderItemServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/order-items", OrderItemController::getAll);
        app.get("/order-items/{id}", OrderItemController::getById);

        app.get("/order-items/order/{orderId}", OrderItemController::getAllByOrderId);
        app.get("/order-items/item/{itemId}", OrderItemController::getAllByItemId);

        app.post("/order-items", OrderItemController::create);
        app.put("/order-items/{id}", OrderItemController::update);
        app.delete("/order-items/{id}", OrderItemController::delete);
    }

    public static void getAll(Context ctx) {

        ctx.future(() ->
                orderItemService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                orderItemService.getById(id).thenAccept(orderItem ->
                        ctx.json(orderItem.orElseThrow(() ->
                                        new ApiException(404, "Order item not found")))));
    }

    public static void getAllByOrderId(Context ctx) {

        int orderId = Integer.parseInt(ctx.pathParam("orderId"));

        ctx.future(() ->
                orderItemService.getAllByOrderId(orderId).thenAccept(ctx::json));
    }

    public static void getAllByItemId(Context ctx) {

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));

        ctx.future(() ->
                orderItemService.getAllByItemId(itemId).thenAccept(ctx::json));
    }

    public static void create(Context ctx) {

        CreateOrderItemDTO dto = ctx.bodyAsClass(CreateOrderItemDTO.class);

        ctx.future(() ->
                orderItemService.create(dto).thenAccept(orderItem -> {

                    ctx.status(201);
                    ctx.json(orderItem);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateOrderItemDTO dto = ctx.bodyAsClass(UpdateOrderItemDTO.class);

        ctx.future(() ->
                orderItemService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                orderItemService.delete(id).thenRun(() ->
                        ctx.status(204)));
    }
}