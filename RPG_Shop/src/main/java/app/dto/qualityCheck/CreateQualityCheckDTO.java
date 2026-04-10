package app.dto.qualityCheck;

import app.entities.enums.QualityStatus;

public record CreateQualityCheckDTO(

        int itemId,
        QualityStatus status,
        Integer approvedByUserId
) {
}