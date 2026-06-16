package app.controllers;

import app.dto.orderItem.CreateOrderItemDTO;
import app.dto.orderItem.UpdateOrderItemDTO;
import app.entities.enums.Role;
import app.exceptions.ApiException;
import app.service.impl.OrderItemServiceImpl;
import app.service.security.AuthMiddleware;
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
        AuthMiddleware.requireAdmin(ctx);

        ctx.future(() ->
                orderItemService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {
        AuthMiddleware.requireLogin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);

        ctx.future(() ->
                orderItemService.getByIdForUser(id, userId, role).thenAccept(ctx::json));
    }

    public static void getAllByOrderId(Context ctx) {
        AuthMiddleware.requireLogin(ctx);

        int orderId = Integer.parseInt(ctx.pathParam("orderId"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);

        ctx.future(() ->
                orderItemService.getAllByOrderIdForUser(orderId, userId, role).thenAccept(ctx::json));
    }

    public static void getAllByItemId(Context ctx) {
        AuthMiddleware.requireAdmin(ctx);

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));

        ctx.future(() ->
                orderItemService.getAllByItemId(itemId).thenAccept(ctx::json));
    }

    public static void create(Context ctx) {
        AuthMiddleware.requireLogin(ctx);

        int userId = getUserId(ctx);
        Role role = getRole(ctx);
        CreateOrderItemDTO dto = ctx.bodyAsClass(CreateOrderItemDTO.class);

        ctx.future(() ->
                orderItemService.createForUser(dto, userId, role).thenAccept(orderItem -> {
                    ctx.status(201);
                    ctx.json(orderItem);
                })
        );
    }

    public static void update(Context ctx) {
        AuthMiddleware.requireLogin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);
        UpdateOrderItemDTO dto = ctx.bodyAsClass(UpdateOrderItemDTO.class);

        ctx.future(() ->
                orderItemService.updateForUser(id, dto, userId, role).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {
        AuthMiddleware.requireLogin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);

        ctx.future(() ->
                orderItemService.deleteForUser(id, userId, role).thenRun(() ->
                        ctx.status(204)));
    }

    private static int getUserId(Context ctx) {
        Integer userId = ctx.attribute("userId");

        if (userId == null) {
            throw new ApiException(401, "Unauthorized");
        }

        return userId;
    }

    private static Role getRole(Context ctx) {
        Role role = ctx.attribute("role");

        if (role == null) {
            throw new ApiException(401, "Unauthorized");
        }

        return role;
    }
}