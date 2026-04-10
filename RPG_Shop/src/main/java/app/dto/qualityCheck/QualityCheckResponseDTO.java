package app.dto.qualityCheck;

import app.entities.QualityCheck;
import app.entities.enums.QualityStatus;

import java.time.Instant;

public record QualityCheckResponseDTO(

        int id,
        int itemId,
        QualityStatus status,
        Instant checkedAt,
        Integer approvedByUserId
) {
    public static QualityCheckResponseDTO fromEntity(QualityCheck qualityCheck) {

        return new QualityCheckResponseDTO(

                qualityCheck.getId(),
                qualityCheck.getItem().getId(),
                qualityCheck.getStatus(),
                qualityCheck.getCheckedAt(),
                qualityCheck.getApprovedBy() != null ? qualityCheck.getApprovedBy().getId() : null
        );
    }
}