package app.dto.user;

public record CreateUserDTO(

        String email,
        String name,
        String username,
        String password
) {
}