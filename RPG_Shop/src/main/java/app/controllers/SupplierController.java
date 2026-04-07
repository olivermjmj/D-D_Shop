package app.controllers;

import app.dao.SupplierDAO;
import app.entities.Supplier;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class SupplierController {

    private static final SupplierDAO supplierDAO = new SupplierDAO();

    public static void addRoutes(Javalin app) {

        app.get("/suppliers", SupplierController::getAll);
        app.get("/suppliers/{id}", SupplierController::getById);

        app.post("/suppliers", SupplierController::create);
        app.put("/suppliers/{id}", SupplierController::update);
        app.delete("/suppliers/{id}", SupplierController::delete);
    }

    public static void getAll(Context ctx) {

        List<Supplier> suppliers = supplierDAO.getAll();
        ctx.status(200).json(suppliers);
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        Optional<Supplier> supplier = supplierDAO.getById(id);
        if (supplier.isPresent()) {
            ctx.status(200).json(supplier.get());
        } else {
            ctx.status(404).result("Supplier not found");
        }
    }

    public static void create(Context ctx) {

        try {

            Supplier supplier = ctx.bodyAsClass(Supplier.class);
            Supplier created = supplierDAO.create(supplier);

            ctx.status(201).json(created);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create supplier");
        }
    }

    public static void update(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            Supplier supplier = ctx.bodyAsClass(Supplier.class);
            supplier.setId(id);

            Supplier updated = supplierDAO.update(supplier);

            ctx.status(200).json(updated);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not update supplier");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            supplierDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete supplier");
        }
    }
}