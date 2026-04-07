package app.controllers;

import app.dao.AddressDAO;
import app.entities.Address;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class AddressController {

    private static final AddressDAO addressDAO = new AddressDAO();

    public static void addRoutes(Javalin app) {

        app.get("/addresses", AddressController::getAll);
        app.get("/addresses/{id}", AddressController::getById);

        app.post("/addresses", AddressController::create);
        app.put("/addresses/{id}", AddressController::update);
        app.delete("/addresses/{id}", AddressController::delete);
    }

    public static void getAll(Context ctx) {

        List<Address> addresses = addressDAO.getAll();
        ctx.status(200).json(addresses);
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<Address> address = addressDAO.getById(id);

        if (address.isPresent()) {

            ctx.status(200).json(address.get());
        } else {
            ctx.status(404).result("Address not found");
        }
    }

    public static void create(Context ctx) {

        try {

            Address address = ctx.bodyAsClass(Address.class);
            Address created = addressDAO.create(address);

            ctx.status(201).json(created);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create address");
        }
    }

    public static void update(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            Address address = ctx.bodyAsClass(Address.class);
            address.setId(id);

            Address updated = addressDAO.update(address);

            ctx.status(200).json(updated);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not update address");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            addressDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete address");
        }
    }
}