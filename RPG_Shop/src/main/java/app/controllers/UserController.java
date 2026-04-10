package app.controllers;

import app.dto.user.CreateUserDTO;
import app.dto.user.UpdateUserDTO;
import app.exceptions.ApiException;
import app.service.impl.UserServiceImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    private static final UserServiceImpl userService = new UserServiceImpl();

    public static void addRoutes(Javalin app) {

        app.get("/users", UserController::getAll);
        app.get("/users/{id}", UserController::getById);
        app.post("/users", UserController::create);
        app.put("/users/{id}", UserController::update);
        app.delete("/users/{id}", UserController::delete);
    }

    public static void getAll(Context ctx) {

        ctx.future(() -> userService.getAll().thenAccept(ctx::json));
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() -> userService.getById(id).thenAccept(user -> ctx.json(user.orElseThrow(() -> new ApiException(404, "User not found")))));
    }

    public static void create(Context ctx) {

        CreateUserDTO dto = ctx.bodyAsClass(CreateUserDTO.class);

        ctx.future(() ->
                        userService.create(dto).thenAccept(user -> {

                            ctx.status(201);ctx.json(user);
                        })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateUserDTO dto = ctx.bodyAsClass(UpdateUserDTO.class);

        ctx.future(() ->
                userService.update(id, dto).thenAccept(ctx::json));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                userService.delete(id).thenRun(() -> ctx.status(204)));
    }
}