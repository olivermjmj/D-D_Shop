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

        ctx.future(() ->
                userService.getAllAsync()
                        .thenAccept(ctx::json)
        );
    }

    public static void getById(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() -> userService.getByIdAsync(id)
                .thenAccept(user -> ctx.json(user.orElseThrow(() -> new ApiException(404, "User not found")))));
    }

    public static void create(Context ctx) {

        CreateUserDTO dto = ctx.bodyAsClass(CreateUserDTO.class);

        ctx.future(() -> userService.createAsync(dto)
                        .thenAccept(user -> {
                            ctx.status(201);
                            ctx.json(user);
                        })
        );
    }

    public static void update(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));
        UpdateUserDTO dto = ctx.bodyAsClass(UpdateUserDTO.class);

        ctx.future(() -> userService.updateAsync(id, dto)
                        .thenAccept(ctx::json)
        );
    }

    public static void delete(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() -> userService.deleteAsync(id)
                        .thenRun(() -> ctx.status(204))
        );
    }

    public static void existsByEmail(Context ctx) {

        String email = ctx.pathParam("email");

        ctx.future(() -> userService.existsByEmailAsync(email)
                        .thenAccept(ctx::json)
        );
    }
}