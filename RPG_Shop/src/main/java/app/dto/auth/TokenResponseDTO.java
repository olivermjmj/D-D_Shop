package app.dto.auth;

import app.entities.enums.Role;

public record TokenResponseDTO(
        String token,
        String username,
        Role role
) {
}