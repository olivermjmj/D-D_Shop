package app.dto.adminActionLog;

import app.entities.AdminActionLog;
import app.entities.enums.AdminActionType;

import java.time.Instant;

public record AdminActionLogResponseDTO(

        int id,
        int adminId,
        AdminActionType action,
        String targetType,
        Integer targetId,
        Instant createdAt
) {
    public static AdminActionLogResponseDTO fromEntity(AdminActionLog adminActionLog) {

        return new AdminActionLogResponseDTO(

                adminActionLog.getId(),
                adminActionLog.getAdmin().getId(),
                adminActionLog.getAction(),
                adminActionLog.getTargetType(),
                adminActionLog.getTargetId(),
                adminActionLog.getCreatedAt()
        );
    }
}