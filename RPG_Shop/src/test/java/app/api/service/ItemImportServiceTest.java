package app.api.service;

import app.api.client.Dnd5eClient;
import app.api.dto.EquipmentListDTO;
import app.api.dto.ImportedItemDTO;
import app.api.dto.enums.DungeonAndDragonsCurrency;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ItemImportServiceTest {

    @Test
    void calculatePrice_gp() {

        ImportedItemDTO dto = new ImportedItemDTO();
        ImportedItemDTO.CostDTO cost = new ImportedItemDTO.CostDTO();

        cost.setQuantity(5);
        cost.setUnit(DungeonAndDragonsCurrency.gp);

        dto.setCost(cost);

        ItemImportService service = new ItemImportService(null);

        BigDecimal result = service.calculatePrice(dto);

        assertEquals(BigDecimal.valueOf(500), result);
    }

    @Test
    void calculatePrice_sp() {

        ImportedItemDTO dto = new ImportedItemDTO();
        ImportedItemDTO.CostDTO cost = new ImportedItemDTO.CostDTO();

        cost.setQuantity(5);
        cost.setUnit(DungeonAndDragonsCurrency.sp);

        dto.setCost(cost);

        ItemImportService service = new ItemImportService(null);

        BigDecimal result = service.calculatePrice(dto);

        assertEquals(BigDecimal.valueOf(50), result);
    }

    //This test can fail if the api is down and should not be used in real professional tests
    @Test
    void fetchEquipmentList_realCall() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Dnd5eClient client = new Dnd5eClient(mapper);

        EquipmentListDTO list = client.fetchEquipmentList();

        assertNotNull(list);
        assertFalse(list.getResults().isEmpty());
    }
}