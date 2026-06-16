package app.controllers;

import app.dto.transaction.CreateTransactionDTO;
import app.dto.transaction.UpdateTransactionDTO;
import app.entities.enums.Role;
import app.entities.enums.TransactionType;
import app.exceptions.ApiException;
import app.service.impl.TransactionServiceImpl;
import app.service.security.AuthMiddleware;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class TransactionController {

    private static final TransactionServiceImpl transactionService = new TransactionServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/transactions", TransactionController::getAll);
        app.get("/transactions/{id}", TransactionController::getById);
        app.get("/transactions/user/{userId}", TransactionController::getAllByUserId);
        app.get("/transactions/order/{orderId}", TransactionController::getAllByOrderId);
        app.get("/transactions/type/{type}", TransactionController::getAllByType);

        app.post("/transactions", TransactionController::create);
        app.put("/transactions/{id}", TransactionController::update);
        app.delete("/transactions/{id}", TransactionController::delete);
    }

    public static void getAll(Context ctx) {
        AuthMiddleware.requireAdmin(ctx);

        ctx.future(() ->
                transactionService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {
        AuthMiddleware.requireLogin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);

        ctx.future(() ->
                transactionService.getByIdForUser(id, userId, role).thenAccept(ctx::json));
    }

    public static void getAllByUserId(Context ctx) {
        AuthMiddleware.requireLogin(ctx);

        int requestedUserId = Integer.parseInt(ctx.pathParam("userId"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);

        ctx.future(() ->
                transactionService.getAllByUserIdForUser(requestedUserId, userId, role).thenAccept(ctx::json));
    }

    public static void getAllByOrderId(Context ctx) {
        AuthMiddleware.requireLogin(ctx);

        int orderId = Integer.parseInt(ctx.pathParam("orderId"));
        int userId = getUserId(ctx);
        Role role = getRole(ctx);

        ctx.future(() ->
                transactionService.getAllByOrderIdForUser(orderId, userId, role).thenAccept(ctx::json));
    }

    public static void getAllByType(Context ctx) {
        AuthMiddleware.requireAdmin(ctx);

        try {
            TransactionType type = TransactionType.valueOf(ctx.pathParam("type").toUpperCase());

            ctx.future(() ->
                    transactionService.getAllByType(type).thenAccept(ctx::json));

        } catch (IllegalArgumentException e) {
            throw new ApiException(400, "Invalid transaction type");
        }
    }

    public static void create(Context ctx) {
        AuthMiddleware.requireLogin(ctx);

        int userId = getUserId(ctx);
        Role role = getRole(ctx);
        CreateTransactionDTO dto = ctx.bodyAsClass(CreateTransactionDTO.class);

        ctx.future(() ->
                transactionService.createForUser(dto, userId, role).thenAccept(transaction -> {
                    ctx.status(201);
                    ctx.json(transaction);
                })
        );
    }

    public static void update(Context ctx) {
        AuthMiddleware.requireAdmin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateTransactionDTO dto = ctx.bodyAsClass(UpdateTransactionDTO.class);

        ctx.future(() ->
                transactionService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {
        AuthMiddleware.requireAdmin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                transactionService.delete(id).thenRun(() ->
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