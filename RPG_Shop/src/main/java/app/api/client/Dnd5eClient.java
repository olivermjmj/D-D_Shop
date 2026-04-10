package app.api.client;

import app.api.dto.EquipmentListDTO;
import app.api.dto.ImportedItemDTO;
import app.exceptions.ApiImportException;
import app.utils.HttpClientHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Dnd5eClient {

    private static final String BASE_URL = "https://www.dnd5eapi.co";
    private final ObjectMapper mapper;

    public Dnd5eClient(ObjectMapper mapper) {

        this.mapper = mapper;
    }

    public EquipmentListDTO fetchEquipmentList() {

        try {

            String json = HttpClientHelper.get(BASE_URL + "/api/2014/equipment");

            return mapper.readValue(json, EquipmentListDTO.class);
        } catch (JsonProcessingException e) {
            throw new ApiImportException("Failed to parse equipment list", e);
        }
    }

    public ImportedItemDTO fetchEquipmentDetail(String url) {

        try {

            String json = HttpClientHelper.get(BASE_URL + url);

            ImportedItemDTO dto = mapper.readValue(json, ImportedItemDTO.class);
            dto.setExternalSource("DND5E");

            return dto;
        } catch (JsonProcessingException e) {
            throw new ApiImportException("Failed to parse equipment detail", e);
        }
    }
}