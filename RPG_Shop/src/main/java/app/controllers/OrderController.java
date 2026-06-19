package app.controllers;

import app.dto.order.CreateOrderDTO;
import app.dto.order.UpdateOrderDTO;
import app.dto.order.UpdateOrderStatusDTO;
import app.entities.enums.OrderStatus;
import app.entities.enums.Role;
import app.exceptions.ApiException;
import app.service.impl.OrderServiceImpl;
import app.service.security.AuthMiddleware;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {

    private static final OrderServiceImpl orderService = new OrderServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/orders", OrderController::getAll);
        app.get("/orders/me", OrderController::getMyOrders);
        app.get("/orders/{id}", OrderController::getById);
        app.get("/orders/{id}/items", OrderController::getByIdWithItems);
        app.get("/orders/user/{userId}", OrderController::getAllByUserId);
        app.get("/orders/status/{status}", OrderController::getAllByStatus);
        app.get("/orders/{id}/total-price", OrderController::getTotalPriceByOrderId);

        app.post("/orders", OrderController::create);
        app.put("/orders/{id}", OrderController::update);
        app.put("/orders/{id}/status", OrderController::updateStatus);
        app.delete("/orders/{id}", OrderController::delete);
    }

    public static void getAll(Context ctx) {
        requireAdmin(ctx);

        ctx.future(() ->
                orderService.getAll().thenAccept(ctx::json));
    }

    public static void getMyOrders(Context ctx) {

        AuthMiddleware.requireLogin(ctx);

        int userId = getUserId(ctx);

        ctx.future(() ->
                orderService.getMyOrders(userId).thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        AuthMiddleware.requireLogin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);

        ctx.future(() ->
                orderService.getByIdForUser(id, userId, role).thenAccept(ctx::json));
    }

    public static void getByIdWithItems(Context ctx) {

        AuthMiddleware.requireLogin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);

        ctx.future(() ->
                orderService.getByIdWithItemsForUser(id, userId, role).thenAccept(ctx::json));
    }

    public static void getAllByUserId(Context ctx) {
        requireAdmin(ctx);

        int userId = Integer.parseInt(ctx.pathParam("userId"));

        ctx.future(() ->
                orderService.getAllByUserId(userId).thenAccept(ctx::json));
    }

    public static void getAllByStatus(Context ctx) {
        requireAdmin(ctx);

        try {
            OrderStatus status = OrderStatus.valueOf(ctx.pathParam("status").toUpperCase());

            ctx.future(() ->
                    orderService.getAllByStatus(status).thenAccept(ctx::json));

        } catch (IllegalArgumentException e) {
            throw new ApiException(400, "Invalid order status");
        }
    }

    public static void getTotalPriceByOrderId(Context ctx) {

        AuthMiddleware.requireLogin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);

        ctx.future(() ->
                orderService.getTotalPriceForUser(id, userId, role).thenAccept(ctx::json));
    }

    public static void create(Context ctx) {

        AuthMiddleware.requireLogin(ctx);

        int userId = getUserId(ctx);
        CreateOrderDTO dto = ctx.bodyAsClass(CreateOrderDTO.class);

        ctx.future(() ->
                orderService.createForUser(dto, userId).thenAccept(order -> {
                    ctx.status(201);
                    ctx.json(order);
                })
        );
    }

    public static void update(Context ctx) {

        AuthMiddleware.requireLogin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);
        UpdateOrderDTO dto = ctx.bodyAsClass(UpdateOrderDTO.class);

        ctx.future(() ->
                orderService.updateAddressForUser(id, dto, userId, role).thenAccept(ctx::json));
    }

    public static void updateStatus(Context ctx) {
        requireAdmin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateOrderStatusDTO dto = ctx.bodyAsClass(UpdateOrderStatusDTO.class);

        ctx.future(() ->
                orderService.updateStatus(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {
        requireAdmin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                orderService.delete(id).thenRun(() -> ctx.status(204)));
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

    private static void requireAdmin(Context ctx) {

        AuthMiddleware.requireLogin(ctx);

        if (getRole(ctx) != Role.ADMIN) {
            throw new ApiException(403, "Admin only");
        }
    }
}