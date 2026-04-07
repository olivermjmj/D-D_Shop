package app.dto;

import app.entities.QualityCheck;
import app.entities.enums.QualityStatus;

import java.time.Instant;

public record QualityCheckDTO(

        int id,
        int itemId,
        QualityStatus status,
        Instant checkedAt,
        Integer approvedByUserId
) {
    public static QualityCheckDTO fromEntity(QualityCheck qualityCheck) {

        return new QualityCheckDTO(

                qualityCheck.getId(),
                qualityCheck.getItem().getId(),
                qualityCheck.getStatus(),
                qualityCheck.getCheckedAt(),
                qualityCheck.getApprovedBy() != null ? qualityCheck.getApprovedBy().getId() : null
        );
    }
}