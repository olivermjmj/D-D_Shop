package app.dto.inventory;

import app.entities.Inventory;

public record InventoryResponseDTO(

        int id,
        int itemId,
        int quantity
) {
    public static InventoryResponseDTO fromEntity(Inventory inventory) {

        return new InventoryResponseDTO(
                inventory.getId(),
                inventory.getItem().getId(),
                inventory.getQuantity()
        );
    }
}