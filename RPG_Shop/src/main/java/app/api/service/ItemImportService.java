package app.api.service;

import app.api.client.Dnd5eClient;
import app.api.dto.EquipmentListDTO;
import app.api.dto.ImportedItemDTO;
import app.config.ThreadPoolConfig;

import java.math.BigDecimal;
import java.util.List;

import java.util.concurrent.*;


public class ItemImportService {

    private final Dnd5eClient client;
    private final ExecutorService executorService;

    public ItemImportService(Dnd5eClient client) {
        this(client, ThreadPoolConfig.getExecutor());
    }

    public ItemImportService(Dnd5eClient client, ExecutorService executorService) {

        this.client = client;
        this.executorService = executorService;
    }

    public CompletableFuture<List<ImportedItemDTO>> importEquipment() {
        EquipmentListDTO equipmentListDTO = client.fetchEquipmentList();

        if (equipmentListDTO.getResults() == null || equipmentListDTO.getResults().isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        List<CompletableFuture<ImportedItemDTO>> futures = equipmentListDTO.getResults()
                .stream()
                .map(itemRef -> CompletableFuture.supplyAsync(() -> {

                    ImportedItemDTO dto = client.fetchEquipmentDetail(itemRef.getUrl());
                    dto.setPrice(calculatePrice(dto));
                    return dto;
                }, executorService))
                .toList();

        CompletableFuture<Void> allDone = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        return allDone.thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .toList());
    }

    private BigDecimal calculatePrice(ImportedItemDTO importedItemDTO) {

        if (importedItemDTO.getCost() == null || importedItemDTO.getCost().getUnit() == null) {
            return BigDecimal.ZERO;
        }

        int quantity = importedItemDTO.getCost().getQuantity();
        String unit = importedItemDTO.getCost().getUnit().name();

        return switch (unit) {
            case "pp" -> BigDecimal.valueOf(quantity * 1000L);
            case "gp" -> BigDecimal.valueOf(quantity * 100L);
            case "ep" -> BigDecimal.valueOf(quantity * 50L);
            case "sp" -> BigDecimal.valueOf(quantity * 10L);
            case "cp" -> BigDecimal.valueOf(quantity);

            default -> BigDecimal.ZERO;
        };
    }
}