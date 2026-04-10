package app.dto.address;

import app.entities.Address;

public record AddressResponseDTO(

        int id,
        String street,
        String postalCode,
        String city,
        String country
) {
    public static AddressResponseDTO fromEntity(Address address) {

        return new AddressResponseDTO(
                address.getId(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity(),
                address.getCountry()
        );
    }
}