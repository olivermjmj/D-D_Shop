package app.dto.qualityCheck;

import app.entities.enums.QualityStatus;

public record UpdateQualityCheckDTO(

        QualityStatus status,
        Integer approvedByUserId
) {
}