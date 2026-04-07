package app.dto;

import app.entities.Inventory;

public record InventoryDTO(

        int id,
        int itemId,
        int quantity
) {
    public static InventoryDTO fromEntity(Inventory inventory) {

        return new InventoryDTO(

                inventory.getId(),
                inventory.getItem().getId(),
                inventory.getQuantity()
        );
    }
}