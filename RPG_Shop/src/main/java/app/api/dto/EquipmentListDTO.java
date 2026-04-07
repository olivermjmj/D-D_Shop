package app.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentListDTO {

    private List<ItemRefDTO> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemRefDTO {

        private String index;
        private String name;
        private String url;
    }
}