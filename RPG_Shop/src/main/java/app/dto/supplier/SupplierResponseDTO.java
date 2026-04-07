package app.dto.supplier;

import app.entities.Supplier;

public record SupplierResponseDTO(

        int id,
        String name,
        Integer addressId
) {
    public static SupplierResponseDTO fromEntity(Supplier supplier) {

        return new SupplierResponseDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getAddress() != null ? supplier.getAddress().getId() : null
        );
    }
}