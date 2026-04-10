package app.controllers;

import app.dto.order.CreateOrderDTO;
import app.dto.order.UpdateOrderDTO;
import app.entities.enums.OrderStatus;
import app.exceptions.ApiException;
import app.service.impl.OrderServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {

    private static final OrderServiceImpl orderService = new OrderServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/orders", OrderController::getAll);
        app.get("/orders/{id}", OrderController::getById);
        app.get("/orders/{id}/items", OrderController::getByIdWithItems);
        app.get("/orders/user/{userId}", OrderController::getAllByUserId);
        app.get("/orders/status/{status}", OrderController::getAllByStatus);
        app.get("/orders/{id}/total-price", OrderController::getTotalPriceByOrderId);

        app.post("/orders", OrderController::create);
        app.put("/orders/{id}", OrderController::update);
        app.delete("/orders/{id}", OrderController::delete);
    }

    public static void getAll(Context ctx) {

        ctx.future(() ->
                orderService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                orderService.getById(id).thenAccept(order ->
                        ctx.json(order.orElseThrow(() ->
                                        new ApiException(404, "Order not found"))))
        );
    }

    public static void getByIdWithItems(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                orderService.getByIdWithItems(id).thenAccept(ctx::json));
    }

    public static void getAllByUserId(Context ctx) {

        int userId = Integer.parseInt(ctx.pathParam("userId"));

        ctx.future(() ->
                orderService.getAllByUserId(userId).thenAccept(ctx::json));
    }

    public static void getAllByStatus(Context ctx) {

        try {

            OrderStatus status = OrderStatus.valueOf(ctx.pathParam("status").toUpperCase());

            ctx.future(() ->
                    orderService.getAllByStatus(status).thenAccept(ctx::json));
        } catch (IllegalArgumentException e) {
            throw new ApiException(400, "Invalid order status");
        }
    }

    public static void getTotalPriceByOrderId(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                orderService.getTotalPriceByOrderId(id).thenAccept(ctx::json));
    }

    public static void create(Context ctx) {

        CreateOrderDTO dto = ctx.bodyAsClass(CreateOrderDTO.class);

        ctx.future(() ->
                orderService.create(dto).thenAccept(order -> {

                    ctx.status(201);
                    ctx.json(order);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateOrderDTO dto = ctx.bodyAsClass(UpdateOrderDTO.class);

        ctx.future(() ->
                orderService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                orderService.delete(id).thenRun(() -> ctx.status(204)));
    }
}