package app.dto;

import app.entities.AdminActionLog;
import app.entities.enums.AdminActionType;

import java.time.Instant;

public record AdminActionLogDTO(

        int id,
        int adminId,
        AdminActionType action,
        String targetType,
        Integer targetId,
        Instant createdAt
) {
    public static AdminActionLogDTO fromEntity(AdminActionLog adminActionLog) {

        return new AdminActionLogDTO(
                adminActionLog.getId(),
                adminActionLog.getAdmin().getId(),
                adminActionLog.getAction(),
                adminActionLog.getTargetType(),
                adminActionLog.getTargetId(),
                adminActionLog.getCreatedAt()
        );
    }
}