package app.controllers;

import app.dao.TransactionDAO;
import app.dto.transaction.TransactionResponseDTO;
import app.entities.Transaction;
import app.entities.enums.TransactionType;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Optional;

public class TransactionController {

    private static final TransactionDAO transactionDAO = new TransactionDAO();

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

        ctx.status(200).json(
                transactionDAO.getAll().stream()
                        .map(TransactionResponseDTO::fromEntity)
                        .toList()
        );
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<Transaction> transaction = transactionDAO.getById(id);

        if (transaction.isPresent()) {

            ctx.status(200).json(TransactionResponseDTO.fromEntity(transaction.get()));
        } else {
            ctx.status(404).result("Transaction not found");
        }
    }

    public static void getAllByUserId(Context ctx) {

        int userId = Integer.parseInt(ctx.pathParam("userId"));

        ctx.status(200).json(
                transactionDAO.getAllByUserId(userId).stream()
                        .map(TransactionResponseDTO::fromEntity)
                        .toList()
        );
    }

    public static void getAllByOrderId(Context ctx) {

        int orderId = Integer.parseInt(ctx.pathParam("orderId"));

        ctx.status(200).json(
                transactionDAO.getAllByOrderId(orderId).stream()
                        .map(TransactionResponseDTO::fromEntity)
                        .toList()
        );
    }

    public static void getAllByType(Context ctx) {

        try {

            TransactionType type = TransactionType.valueOf(ctx.pathParam("type").toUpperCase());

            ctx.status(200).json(
                    transactionDAO.getAllByType(type).stream()
                            .map(TransactionResponseDTO::fromEntity)
                            .toList()
            );
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid transaction type");
        }
    }

    public static void create(Context ctx) {

        try {

            Transaction transaction = ctx.bodyAsClass(Transaction.class);
            Transaction createdTransaction = transactionDAO.create(transaction);

            ctx.status(201).json(TransactionResponseDTO.fromEntity(createdTransaction));
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create transaction");
        }
    }

    public static void update(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            Transaction transaction = ctx.bodyAsClass(Transaction.class);
            transaction.setId(id);

            Transaction updatedTransaction = transactionDAO.update(transaction);

            ctx.status(200).json(TransactionResponseDTO.fromEntity(updatedTransaction));
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not update transaction");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            transactionDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete transaction");
        }
    }
}