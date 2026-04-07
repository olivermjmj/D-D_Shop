package app.dto;

import app.entities.Address;

public record AddressDTO(

        int id,
        String street,
        String postalCode,
        String city,
        String country
) {
    public static AddressDTO fromEntity(Address address) {

        return new AddressDTO(

                address.getId(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity(),
                address.getCountry()
        );
    }
}