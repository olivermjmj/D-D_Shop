package app.dto.adminActionLog;

import app.entities.enums.AdminActionType;

public record CreateAdminActionLogDTO(

        int adminId,
        AdminActionType action,
        String targetType,
        Integer targetId
) {
}