package app.dto;

import app.entities.Stamp;

public record StampDTO(

        int id,
        String name
) {
    public static StampDTO fromEntity(Stamp stamp) {

        return new StampDTO(

                stamp.getId(),
                stamp.getName()
        );
    }
}