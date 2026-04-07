package app.controllers;

import app.dao.DiscountDAO;
import app.entities.Discount;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class DiscountController {

    private static final DiscountDAO discountDAO = new DiscountDAO();

    public static void addRoutes(Javalin app) {

        app.get("/discounts", DiscountController::getAll);
        app.get("/discounts/{id}", DiscountController::getById);

        app.post("/discounts", DiscountController::create);
        app.put("/discounts/{id}", DiscountController::update);
        app.delete("/discounts/{id}", DiscountController::delete);
    }

    public static void getAll(Context ctx) {

        List<Discount> discounts = discountDAO.getAll();
        ctx.status(200).json(discounts);
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<Discount> discount = discountDAO.getById(id);

        if (discount.isPresent()) {

            ctx.status(200).json(discount.get());
        } else {
            ctx.status(404).result("Discount not found");
        }
    }

    public static void create(Context ctx) {

        try {

            Discount discount = ctx.bodyAsClass(Discount.class);
            Discount created = discountDAO.create(discount);

            ctx.status(201).json(created);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create discount");
        }
    }

    public static void update(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            Discount discount = ctx.bodyAsClass(Discount.class);
            discount.setId(id);

            Discount updated = discountDAO.update(discount);

            ctx.status(200).json(updated);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not update discount");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            discountDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {

            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete discount");
        }
    }
}