package app.dto.address;

public record UpdateAddressDTO(

        String street,
        String postalCode,
        String city,
        String country
) {
}