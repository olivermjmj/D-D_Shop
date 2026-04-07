package app.controllers;

import app.dao.StampDAO;
import app.entities.Stamp;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class StampController {

    private static final StampDAO stampDAO = new StampDAO();

    public static void addRoutes(Javalin app) {

        app.get("/stamps", StampController::getAll);
        app.get("/stamps/{id}", StampController::getById);

        app.post("/stamps", StampController::create);
        app.put("/stamps/{id}", StampController::update);
        app.delete("/stamps/{id}", StampController::delete);
    }

    public static void getAll(Context ctx) {

        List<Stamp> stamps = stampDAO.getAll();
        ctx.status(200).json(stamps);
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        Optional<Stamp> stamp = stampDAO.getById(id);
        if (stamp.isPresent()) {
            ctx.status(200).json(stamp.get());
        } else {
            ctx.status(404).result("Stamp not found");
        }
    }

    public static void create(Context ctx) {

        try {
            Stamp stamp = ctx.bodyAsClass(Stamp.class);
            Stamp created = stampDAO.create(stamp);

            ctx.status(201).json(created);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not create stamp");
        }
    }

    public static void update(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            Stamp stamp = ctx.bodyAsClass(Stamp.class);
            stamp.setId(id);

            Stamp updated = stampDAO.update(stamp);

            ctx.status(200).json(updated);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not update stamp");
        }
    }

    public static void delete(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));
            stampDAO.deleteById(id);

            ctx.status(204);
        } catch (DatabaseException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Could not delete stamp");
        }
    }
}