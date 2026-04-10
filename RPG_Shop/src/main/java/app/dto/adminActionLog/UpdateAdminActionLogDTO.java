package app.dto.adminActionLog;

import app.entities.enums.AdminActionType;

public record UpdateAdminActionLogDTO(

        AdminActionType action,
        String targetType,
        Integer targetId
) {
}