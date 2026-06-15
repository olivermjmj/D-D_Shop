package app.dto.user;

public record UpdateUserDTO(

        String email,
        String name,
        String username,
        String password
) {
}