package app.dto.user;

import app.entities.enums.Role;

public record CreateUserDTO(

        String email,
        String name,
        String username,
        String password,
        Role role
) {
}