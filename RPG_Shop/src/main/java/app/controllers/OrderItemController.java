package app.controllers;

import app.dao.OrderItemDAO;
import app.entities.OrderItem;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class OrderItemController {

    private static final OrderItemDAO orderItemDAO = new OrderItemDAO();

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

        List<OrderItem> orderItems = orderItemDAO.getAll();
        ctx.status(200).json(orderItems);
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<OrderItem> orderItem = orderItemDAO.getById(id);

        if (orderItem.isPresent()) {

            ctx.status(200).json(orderItem.get());
        } else {
            ctx.status(404).result("Order item not found");
        }
    }

    public static void getAllByOrderId(Context ctx) {

        int orderId = Integer.parseInt(ctx.pathParam("orderId"));
        ctx.status(200).json(orderItemDAO.getAllByOrderId(orderId));
    }

    public static void getAllByItemId(Context ctx) {

        int itemId = Integer.parseInt(ctx.pathParam("itemId"));
        ctx.status(200).json(orderItemDAO.getAllByItemId(itemId));
    }

    public static void create(Context ctx) {

        try {

            OrderItem orderItem = ctx.bodyAsClass(OrderItem.class);
            OrderItem created = orderItemDAO.create(orderItem);

            ctx.status(201).json(created);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create order item");
        }
    }

    public static void update(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            OrderItem orderItem = ctx.bodyAsClass(OrderItem.class);
            orderItem.setId(id);

            OrderItem updated = orderItemDAO.update(orderItem);

            ctx.status(200).json(updated);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not update order item");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            orderItemDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete order item");
        }
    }
}