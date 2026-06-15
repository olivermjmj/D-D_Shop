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
import app.exceptions.DatabaseException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class AdminActionLogServiceImpl extends AbstractService<CreateAdminActionLogDTO, UpdateAdminActionLogDTO, AdminActionLogResponseDTO, AdminActionLog, Integer> {

    private final AdminActionLogDAO adminActionLogDAO;
    private final UserDAO userDAO;

    // Used by tests
    public AdminActionLogServiceImpl(AdminActionLogDAO adminActionLogDAO, ExecutorService executorService) {
        this(adminActionLogDAO, new UserDAO(), executorService);
    }

    public AdminActionLogServiceImpl() {
        this(new AdminActionLogDAO(), new UserDAO(), ThreadPoolConfig.getExecutor());
    }

    public AdminActionLogServiceImpl(AdminActionLogDAO adminActionLogDAO, UserDAO userDAO, ExecutorService executorService) {
        super(adminActionLogDAO, AdminActionLogResponseDTO::fromEntity, executorService);
        this.adminActionLogDAO = adminActionLogDAO;
        this.userDAO = userDAO;
    }

    @Override
    protected AdminActionLog createDtoToEntity(CreateAdminActionLogDTO dto) {
        throw new ApiException(405, "Admin logs must be created from authenticated admin");
    }

    @Override
    protected AdminActionLog updateDtoToEntity(AdminActionLog log, UpdateAdminActionLogDTO dto) {
        throw new ApiException(405, "Admin action logs cannot be updated");
    }

    public CompletableFuture<AdminActionLogResponseDTO> createForAdmin(int adminId, CreateAdminActionLogDTO dto) {

        return CompletableFuture.supplyAsync(() -> {

            AdminActionLog log = buildLog(adminId, dto);

            try {
                AdminActionLog createdLog = adminActionLogDAO.create(log);
                return AdminActionLogResponseDTO.fromEntity(createdLog);
            } catch (DatabaseException e) {
                throw new ApiException(500, "Could not create admin action log");
            }

        }, executorService);
    }

    public CompletableFuture<List<AdminActionLogResponseDTO>> getAllByAdminId(int adminId) {

        return CompletableFuture.supplyAsync(() -> {

            User admin = getValidAdmin(adminId);

            return adminActionLogDAO.getAllByAdminId(admin.getId())
                    .stream()
                    .map(AdminActionLogResponseDTO::fromEntity)
                    .toList();

        }, executorService);
    }

    private AdminActionLog buildLog(int adminId, CreateAdminActionLogDTO dto) {

        if (dto.action() == null) {
            throw new ApiException(400, "Action is required");
        }

        if (dto.targetType() == null || dto.targetType().isBlank()) {
            throw new ApiException(400, "Target type is required");
        }

        User admin = getValidAdmin(adminId);

        AdminActionLog log = new AdminActionLog();
        log.setAdmin(admin);
        log.setAction(dto.action());
        log.setTargetType(dto.targetType());
        log.setTargetId(dto.targetId());

        return log;
    }

    private User getValidAdmin(int adminId) {

        User admin = userDAO.getById(adminId)
                .orElseThrow(() -> new ApiException(404, "Admin user not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(403, "User is not an admin");
        }

        return admin;
    }
}