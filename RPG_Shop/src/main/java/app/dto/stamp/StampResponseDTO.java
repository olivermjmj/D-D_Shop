package app.dto.stamp;

import app.entities.Stamp;

public record StampResponseDTO(

        int id,
        String name
) {
    public static StampResponseDTO fromEntity(Stamp stamp) {

        return new StampResponseDTO(
                stamp.getId(),
                stamp.getName()
        );
    }
}