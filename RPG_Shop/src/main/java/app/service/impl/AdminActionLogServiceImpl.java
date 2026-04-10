package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.AdminActionLogDAO;
import app.dao.UserDAO;
import app.dto.adminActionLog.AdminActionLogResponseDTO;
import app.dto.adminActionLog.CreateAdminActionLogDTO;
import app.dto.adminActionLog.UpdateAdminActionLogDTO;
import app.entities.AdminActionLog;

import app.entities.User;
import app.entities.enums.Role;
import app.exceptions.ApiException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class AdminActionLogServiceImpl extends AbstractService<CreateAdminActionLogDTO, UpdateAdminActionLogDTO, AdminActionLogResponseDTO, AdminActionLog, Integer> {

    private final AdminActionLogDAO adminActionLogDAO;
    private final UserDAO userDAO = new UserDAO();

    public AdminActionLogServiceImpl() {
        this(new AdminActionLogDAO(), ThreadPoolConfig.getExecutor());
    }

    public AdminActionLogServiceImpl(AdminActionLogDAO adminActionLogDAO, ExecutorService executorService) {

        super(adminActionLogDAO, AdminActionLogResponseDTO::fromEntity, executorService);
        this.adminActionLogDAO = adminActionLogDAO;
    }

    @Override
    protected AdminActionLog createDtoToEntity(CreateAdminActionLogDTO dto) {

        if (dto.action() == null) {
            throw new ApiException(400, "Action is required");
        }

        if (dto.targetType() == null || dto.targetType().isBlank()) {
            throw new ApiException(400, "Target type is required");
        }

        User admin = userDAO.getById(dto.adminId()).orElseThrow(() -> new ApiException(404, "Admin user not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(403, "User is not an admin");
        }

        AdminActionLog log = new AdminActionLog();

        log.setAdmin(admin);
        log.setAction(dto.action());
        log.setTargetType(dto.targetType());
        log.setTargetId(dto.targetId());

        return log;
    }

    @Override
    protected AdminActionLog updateDtoToEntity(AdminActionLog log, UpdateAdminActionLogDTO dto) {

        if (dto.action() != null) {
            log.setAction(dto.action());
        }

        if (dto.targetType() != null) {
            if (dto.targetType().isBlank()) {

                throw new ApiException(400, "Target type cannot be blank");
            }
            log.setTargetType(dto.targetType());
        }

        if (dto.targetId() != null) {
            log.setTargetId(dto.targetId());
        }

        return log;
    }

    public CompletableFuture<List<AdminActionLogResponseDTO>> getAllByAdminId(int adminId) {

        return CompletableFuture.supplyAsync(() -> {

            User admin = userDAO.getById(adminId).orElseThrow(() -> new ApiException(404, "Admin user not found"));

            if (admin.getRole() != Role.ADMIN) {
                throw new ApiException(403, "User is not an admin");
            }

            return adminActionLogDAO.getAllByAdminId(adminId)
                    .stream()
                    .map(AdminActionLogResponseDTO::fromEntity)
                    .toList();
        }, executorService);
    }
}