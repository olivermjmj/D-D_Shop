package app.utils;

import app.entities.User;
import app.entities.enums.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JWTUtil {

    private static final String SECRET = "CHANGE_THIS_TO_A_LONG_SECRET_KEY";
    private static final String ISSUER = "dnd-shop-backend";

    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    public static String generateToken(User user) {

        Instant now = Instant.now();
        Instant expiration = now.plus(2, ChronoUnit.HOURS);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getUsername())
                .withClaim("userId", user.getId())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole().name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiration))
                .sign(algorithm);
    }

    public static DecodedJWT validateToken(String token) {

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();

        return verifier.verify(token);
    }

    public static int getUserId(String token) {

        DecodedJWT decodedJWT = validateToken(token);
        return decodedJWT.getClaim("userId").asInt();
    }

    public static String getUsername(String token) {

        DecodedJWT decodedJWT = validateToken(token);
        return decodedJWT.getSubject();
    }

    public static Role getRole(String token) {

        DecodedJWT decodedJWT = validateToken(token);
        String role = decodedJWT.getClaim("role").asString();

        return Role.valueOf(role);
    }

    public static String extractTokenFromHeader(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }

        return authHeader.substring(7);
    }
}