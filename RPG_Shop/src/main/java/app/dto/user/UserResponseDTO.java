package app.dto.user;

import app.entities.User;
import app.entities.enums.Role;

import java.math.BigDecimal;
import java.time.Instant;

public record UserResponseDTO(

        int id,
        String email,
        String name,
        String username,
        BigDecimal wallet,
        Instant createdAt,
        Role role
) {
    public static UserResponseDTO fromEntity(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getUsername(),
                user.getWallet(),
                user.getCreatedAt(),
                user.getRole()
        );
    }
}