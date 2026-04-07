package app.dto.user;

import app.entities.enums.Role;

import java.math.BigDecimal;

public record UpdateUserDTO(

        String email,
        String name,
        String username,
        String password,
        BigDecimal wallet,
        Role role
) {
}