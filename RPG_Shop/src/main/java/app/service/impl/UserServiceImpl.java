package app.service.impl;

import app.dao.UserDAO;
import app.dto.user.CreateUserDTO;
import app.dto.user.UpdateUserDTO;
import app.dto.user.UserResponseDTO;
import app.entities.User;
import app.entities.enums.Role;
import app.exceptions.ApiException;
import app.service.security.PasswordService;

import java.math.BigDecimal;

public class UserServiceImpl extends AbstractService<CreateUserDTO, UpdateUserDTO, UserResponseDTO, User, Integer> {

    private final UserDAO userDAO;
    private final PasswordService passwordService;

    public UserServiceImpl() {
        super(new UserDAO(), UserResponseDTO::fromEntity);
        this.userDAO = (UserDAO) dao;
        this.passwordService = new PasswordService();
    }

    @Override
    protected User createDtoToEntity(CreateUserDTO dto) {

        if (existsByEmail(dto.email())) {
            throw new ApiException(409, "Email already exists");
        }

        if (dto.username() != null && userDAO.existsByUsername(dto.username())) {
            throw new ApiException(409, "Username already exists");
        }

        User user = new User();
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setUsername(dto.username());
        user.setPasswordHash(passwordService.hash(dto.password()));
        user.setWallet(BigDecimal.ZERO);
        user.setRole(dto.role() != null ? dto.role() : Role.USER);

        return user;
    }

    @Override
    protected User updateDtoToEntity(User user, UpdateUserDTO dto) {

        if (dto.email() != null && !dto.email().equals(user.getEmail()) && existsByEmail(dto.email())) {
            throw new ApiException(409, "Email already exists");
        }

        if (dto.email() != null) {user.setEmail(dto.email()); }
        if (dto.name() != null) {user.setName(dto.name()); }

        if (dto.username() != null && !dto.username().equals(user.getUsername()) && userDAO.existsByUsername(dto.username())) {
            throw new ApiException(409, "Username already exists");
        }

        if (dto.password() != null) {user.setPasswordHash(passwordService.hash(dto.password())); }
        if (dto.wallet() != null) {user.setWallet(dto.wallet()); }
        if (dto.role() != null) {user.setRole(dto.role()); }

        return user;
    }

    public boolean existsByEmail(String email) {
        return userDAO.getByEmail(email).isPresent();
    }
}