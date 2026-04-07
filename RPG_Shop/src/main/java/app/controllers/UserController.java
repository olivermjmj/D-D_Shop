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

        app.get("/users/exists/email/{email}", UserController::existsByEmail);
    }

    public static void getAll(Context ctx) {

        ctx.json(userService.getAll());
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.json(userService.getById(id).orElseThrow(() -> new ApiException(404, "User not found"))
        );
    }

    public static void create(Context ctx) {

        CreateUserDTO dto = ctx.bodyAsClass(CreateUserDTO.class);

        ctx.status(201).json(userService.create(dto));
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateUserDTO dto = ctx.bodyAsClass(UpdateUserDTO.class);

        ctx.json(userService.update(id, dto));
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        userService.delete(id);

        ctx.status(204);
    }

    public static void existsByEmail(Context ctx) {

        ctx.json(userService.existsByEmail(ctx.pathParam("email")));
    }
}