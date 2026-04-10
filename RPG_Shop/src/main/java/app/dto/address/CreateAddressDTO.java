package app.dto.address;

public record CreateAddressDTO(

        String street,
        String postalCode,
        String city,
        String country
) {
}