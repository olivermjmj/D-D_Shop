package app.controllers;

import app.dto.transaction.CreateTransactionDTO;
import app.dto.transaction.UpdateTransactionDTO;
import app.entities.enums.TransactionType;
import app.exceptions.ApiException;
import app.service.impl.TransactionServiceImpl;
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

        ctx.future(() ->
                transactionService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                transactionService.getById(id).thenAccept(transaction ->
                        ctx.json(transaction.orElseThrow(() ->
                                new ApiException(404, "Transaction not found"))))
        );
    }

    public static void getAllByUserId(Context ctx) {

        int userId = Integer.parseInt(ctx.pathParam("userId"));

        ctx.future(() ->
                transactionService.getAllByUserId(userId).thenAccept(ctx::json));
    }

    public static void getAllByOrderId(Context ctx) {

        int orderId = Integer.parseInt(ctx.pathParam("orderId"));

        ctx.future(() ->
                transactionService.getAllByOrderId(orderId).thenAccept(ctx::json));
    }

    public static void getAllByType(Context ctx) {

        try {

            TransactionType type = TransactionType.valueOf(ctx.pathParam("type").toUpperCase());

            ctx.future(() ->
                    transactionService.getAllByType(type).thenAccept(ctx::json));
        } catch (IllegalArgumentException e) {
            throw new ApiException(400, "Invalid transaction type");
        }
    }

    public static void create(Context ctx) {

        CreateTransactionDTO dto = ctx.bodyAsClass(CreateTransactionDTO.class);

        ctx.future(() ->
                transactionService.create(dto).thenAccept(transaction -> {

                    ctx.status(201);
                    ctx.json(transaction);
                })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateTransactionDTO dto = ctx.bodyAsClass(UpdateTransactionDTO.class);

        ctx.future(() ->
                transactionService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                transactionService.delete(id).thenRun(() ->
                        ctx.status(204)));
    }
}