package app.api.service;

import app.api.client.Dnd5eClient;
import app.api.dto.EquipmentListDTO;
import app.api.dto.ImportedItemDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ItemImportService {

    private final Dnd5eClient client;

    public ItemImportService(Dnd5eClient client) {
        this.client = client;
    }

    public List<ImportedItemDTO> importEquipment() throws Exception {

        EquipmentListDTO equipmentListDTO = client.fetchEquipmentList();

        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<Callable<ImportedItemDTO>> tasks = new ArrayList<>();

        for (EquipmentListDTO.ItemRefDTO itemRef : equipmentListDTO.getResults().subList(0, 20)) {
            tasks.add(() -> {

                ImportedItemDTO dto = client.fetchEquipmentDetail(itemRef.getUrl());

                dto.setPrice(calculatePrice(dto));

                return dto;
            });
        }

        List<Future<ImportedItemDTO>> futures =
                executor.invokeAll(tasks);

        List<ImportedItemDTO> results = new ArrayList<>();

        for (Future<ImportedItemDTO> future : futures) {
            results.add(future.get());
        }

        executor.shutdown();

        return results;
    }

    BigDecimal calculatePrice(ImportedItemDTO importedItemDTO) {

        if (importedItemDTO.getCost() == null) {
            return BigDecimal.ZERO;
        }

        int quantity = importedItemDTO.getCost().getQuantity();
        String unit = importedItemDTO.getCost().getUnit().name();

        return switch (unit) {
            case "gp" -> BigDecimal.valueOf(quantity * 100L);
            case "sp" -> BigDecimal.valueOf(quantity * 10L);
            case "cp" -> BigDecimal.valueOf(quantity);
            default -> BigDecimal.ZERO;
        };
    }
}