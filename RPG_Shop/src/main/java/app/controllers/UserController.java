package app.controllers;

import app.dto.auth.LoginDTO;
import app.dto.auth.TokenResponseDTO;
import app.dto.user.CreateUserDTO;
import app.dto.user.UpdateUserDTO;
import app.entities.User;
import app.entities.enums.Role;
import app.exceptions.ApiException;
import app.service.impl.UserServiceImpl;
import app.service.security.AuthMiddleware;
import app.utils.JWTUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.concurrent.CompletableFuture;

public class UserController {

    private static final UserServiceImpl userService = new UserServiceImpl();

    public static void addRoutes(Javalin app) {

        // Auth
        app.post("/auth/login", UserController::login);

        // Public register
        app.post("/users", UserController::create);

        // Protected routes
        app.get("/users", UserController::getAll);
        app.get("/users/{id}", UserController::getById);
        app.put("/users/{id}", UserController::update);
        app.delete("/users/{id}", UserController::delete);
    }

    public static void login(Context ctx) {

        LoginDTO dto = ctx.bodyAsClass(LoginDTO.class);

        ctx.future(() ->
                CompletableFuture.supplyAsync(() -> {

                    User user = userService.validateLogin(dto.username(), dto.password())
                            .orElseThrow(() -> new ApiException(401, "Invalid username or password"));

                    String token = JWTUtil.generateToken(user);

                    return new TokenResponseDTO(
                            token,
                            user.getUsername(),
                            user.getRole()
                    );

                }).thenAccept(ctx::json)
        );
    }

    public static void getAll(Context ctx) {

        AuthMiddleware.requireAdmin(ctx);

        ctx.future(() ->
                userService.getAll().thenAccept(ctx::json)
        );
    }

    public static void getById(Context ctx) {

        AuthMiddleware.requireLogin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        requireSelfOrAdmin(ctx, id);

        ctx.future(() ->
                userService.getById(id)
                        .thenAccept(user ->
                                ctx.json(user.orElseThrow(() -> new ApiException(404, "User not found")))
                        )
        );
    }

    public static void create(Context ctx) {

        CreateUserDTO dto = ctx.bodyAsClass(CreateUserDTO.class);

        ctx.future(() ->
                userService.create(dto)
                        .thenAccept(user -> {
                            ctx.status(201);
                            ctx.json(user);
                        })
        );
    }

    public static void update(Context ctx) {

        AuthMiddleware.requireLogin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));
        requireSelfOrAdmin(ctx, id);

        UpdateUserDTO dto = ctx.bodyAsClass(UpdateUserDTO.class);

        ctx.future(() ->
                userService.update(id, dto).thenAccept(ctx::json)
        );
    }

    public static void delete(Context ctx) {

        AuthMiddleware.requireAdmin(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.future(() ->
                userService.delete(id)
                        .thenRun(() -> ctx.status(204))
        );
    }

    private static void requireSelfOrAdmin(Context ctx, int targetUserId) {

        Integer userId = ctx.attribute("userId");
        Role role = ctx.attribute("role");

        if (userId == null || role == null) {
            throw new ApiException(401, "Unauthorized");
        }

        if (role == Role.ADMIN) {
            return;
        }

        if (!userId.equals(targetUserId)) {
            throw new ApiException(403, "You can only access your own user");
        }
    }
}