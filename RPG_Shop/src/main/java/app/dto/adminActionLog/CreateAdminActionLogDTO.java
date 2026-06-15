package app.dto.adminActionLog;

import app.entities.enums.AdminActionType;

public record CreateAdminActionLogDTO(

        AdminActionType action,
        String targetType,
        Integer targetId
) {
}