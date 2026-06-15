package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.UserDAO;
import app.dto.user.CreateUserDTO;
import app.dto.user.UpdateUserDTO;
import app.dto.user.UserResponseDTO;
import app.entities.User;
import app.entities.enums.Role;
import app.exceptions.ApiException;
import app.service.security.PasswordService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public class UserServiceImpl extends AbstractService<CreateUserDTO, UpdateUserDTO, UserResponseDTO, User, Integer> {

    private final UserDAO userDAO;
    private final PasswordService passwordService;

    public UserServiceImpl() {
        this(new UserDAO(), ThreadPoolConfig.getExecutor());
    }

    public UserServiceImpl(UserDAO userDAO, ExecutorService executorService) {
        super(userDAO, UserResponseDTO::fromEntity, executorService);
        this.userDAO = userDAO;
        this.passwordService = new PasswordService();
    }

    @Override
    protected User createDtoToEntity(CreateUserDTO dto) {

        if (dto.email() == null || dto.email().isBlank()) {
            throw new ApiException(400, "Email is required");
        }

        if (dto.username() == null || dto.username().isBlank()) {
            throw new ApiException(400, "Username is required");
        }

        if (dto.password() == null || dto.password().isBlank()) {
            throw new ApiException(400, "Password is required");
        }

        if (userDAO.existsByEmail(dto.email())) {
            throw new ApiException(409, "Email already exists");
        }

        if (userDAO.existsByUsername(dto.username())) {
            throw new ApiException(409, "Username already exists");
        }

        User user = new User();
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setUsername(dto.username());
        user.setPasswordHash(passwordService.hash(dto.password()));
        user.setWallet(BigDecimal.ZERO);
        user.setRole(Role.USER);

        return user;
    }

    @Override
    protected User updateDtoToEntity(User user, UpdateUserDTO dto) {

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (userDAO.existsByEmail(dto.email())) {
                throw new ApiException(409, "Email already exists");
            }

            user.setEmail(dto.email());
        }

        if (dto.username() != null && !dto.username().equals(user.getUsername())) {
            if (userDAO.existsByUsername(dto.username())) {
                throw new ApiException(409, "Username already exists");
            }

            user.setUsername(dto.username());
        }

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPasswordHash(passwordService.hash(dto.password()));
        }

        return user;
    }

    public Optional<User> validateLogin(String username, String rawPassword) {

        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            return Optional.empty();
        }

        Optional<User> userOptional = userDAO.getByUsername(username);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            return Optional.empty();
        }

        boolean passwordMatches = passwordService.verify(rawPassword, user.getPasswordHash());

        if (!passwordMatches) {
            return Optional.empty();
        }

        return Optional.of(user);
    }
}