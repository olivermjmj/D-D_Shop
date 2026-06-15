package app.service.security;

import app.entities.enums.Role;
import app.exceptions.ApiException;
import app.utils.JWTUtil;
import io.javalin.http.Context;

public class AuthMiddleware {

    public static void requireLogin(Context ctx) {

        String authHeader = ctx.header("Authorization");
        String token = JWTUtil.extractTokenFromHeader(authHeader);

        int userId = JWTUtil.getUserId(token);
        Role role = JWTUtil.getRole(token);

        ctx.attribute("userId", userId);
        ctx.attribute("role", role);
    }

    public static void requireAdmin(Context ctx) {

        requireLogin(ctx);

        Role role = ctx.attribute("role");

        if (role != Role.ADMIN) {
            throw new ApiException(403, "Admin access required");
        }
    }
}