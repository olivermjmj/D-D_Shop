package app.api.dto;

import app.api.dto.enums.DungeonAndDragonsCurrency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportedItemDTO {

    private String externalSource;

    @JsonProperty("index")
    private String externalId;

    private String name;

    @JsonProperty("equipment_category")
    private CategoryDTO category;

    @JsonProperty("desc")
    private List<String> descriptionLines;

    private CostDTO cost;

    private BigDecimal price;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategoryDTO {

        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CostDTO {

        private int quantity;
        private DungeonAndDragonsCurrency unit;
    }
}