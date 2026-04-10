package app.api.service;

import app.api.client.Dnd5eClient;
import app.api.dto.EquipmentListDTO;
import app.api.dto.ImportedItemDTO;
import app.api.dto.enums.DungeonAndDragonsCurrency;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ItemImportServiceTest {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Test
    void importEquipment_shouldReturnEmptyList_whenNoResults() {

        Dnd5eClient client = new FakeDnd5eClientEmpty();
        ItemImportService service = new ItemImportService(client, executorService);

        List<ImportedItemDTO> result = service.importEquipment().join();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void importEquipment_shouldImportEquipmentAndCalculatePrice() {

        Dnd5eClient client = new FakeDnd5eClientWithData();
        ItemImportService service = new ItemImportService(client, executorService);

        List<ImportedItemDTO> result = service.importEquipment().join();

        assertEquals(2, result.size());

        ImportedItemDTO first = result.get(0);
        ImportedItemDTO second = result.get(1);

        assertEquals("club", first.getExternalId());
        assertEquals("DND5E", first.getExternalSource());
        assertEquals(BigDecimal.valueOf(10), first.getPrice());

        assertEquals("chain-mail", second.getExternalId());
        assertEquals("DND5E", second.getExternalSource());
        assertEquals(BigDecimal.valueOf(7500), second.getPrice());
    }

    @Test
    void importEquipment_shouldSetZeroPrice_whenCostIsMissing() {

        Dnd5eClient client = new FakeDnd5eClientNoCost();
        ItemImportService service = new ItemImportService(client, executorService);

        List<ImportedItemDTO> result = service.importEquipment().join();

        assertEquals(1, result.size());
        assertEquals(BigDecimal.ZERO, result.get(0).getPrice());
    }

    static class FakeDnd5eClientEmpty extends Dnd5eClient {

        FakeDnd5eClientEmpty() {
            super(new ObjectMapper());
        }

        @Override
        public EquipmentListDTO fetchEquipmentList() {

            EquipmentListDTO dto = new EquipmentListDTO();
            dto.setResults(List.of());
            return dto;
        }
    }

    static class FakeDnd5eClientWithData extends Dnd5eClient {
        FakeDnd5eClientWithData() {
            super(new ObjectMapper());
        }

        @Override
        public EquipmentListDTO fetchEquipmentList() {

            EquipmentListDTO dto = new EquipmentListDTO();

            EquipmentListDTO.ItemRefDTO item1 = new EquipmentListDTO.ItemRefDTO();
            item1.setUrl("/api/2014/equipment/club");

            EquipmentListDTO.ItemRefDTO item2 = new EquipmentListDTO.ItemRefDTO();
            item2.setUrl("/api/2014/equipment/chain-mail");

            dto.setResults(List.of(item1, item2));
            return dto;
        }

        @Override
        public ImportedItemDTO fetchEquipmentDetail(String url) {

            ImportedItemDTO dto = new ImportedItemDTO();

            ImportedItemDTO.CostDTO cost = new ImportedItemDTO.CostDTO();

            if (url.contains("club")) {

                dto.setExternalId("club");
                dto.setExternalSource("DND5E");
                cost.setQuantity(1);
                cost.setUnit(DungeonAndDragonsCurrency.sp);
            } else {

                dto.setExternalId("chain-mail");
                dto.setExternalSource("DND5E");
                cost.setQuantity(75);
                cost.setUnit(DungeonAndDragonsCurrency.gp);
            }

            dto.setCost(cost);
            return dto;
        }
    }

    static class FakeDnd5eClientNoCost extends Dnd5eClient {

        FakeDnd5eClientNoCost() {
            super(new ObjectMapper());
        }

        @Override
        public EquipmentListDTO fetchEquipmentList() {
            EquipmentListDTO dto = new EquipmentListDTO();

            EquipmentListDTO.ItemRefDTO item = new EquipmentListDTO.ItemRefDTO();
            item.setUrl("/api/2014/equipment/test-item");

            dto.setResults(List.of(item));
            return dto;
        }

        @Override
        public ImportedItemDTO fetchEquipmentDetail(String url) {

            ImportedItemDTO dto = new ImportedItemDTO();
            dto.setExternalId("test-item");
            dto.setExternalSource("DND5E");
            return dto;
        }
    }
}