package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.AddressDAO;
import app.dto.address.AddressResponseDTO;
import app.dto.address.CreateAddressDTO;
import app.dto.address.UpdateAddressDTO;
import app.entities.Address;
import app.exceptions.ApiException;

import java.util.concurrent.ExecutorService;

public class AddressServiceImpl extends AbstractService<CreateAddressDTO, UpdateAddressDTO, AddressResponseDTO, Address, Integer> {

    private final AddressDAO addressDAO;

    public AddressServiceImpl() {
        this(new AddressDAO(), ThreadPoolConfig.getExecutor());
    }

    public AddressServiceImpl(AddressDAO addressDAO, ExecutorService executorService) {

        super(addressDAO, AddressResponseDTO::fromEntity, executorService);
        this.addressDAO = addressDAO;
    }

    @Override
    protected Address createDtoToEntity(CreateAddressDTO dto) {

        validateAddressFields(dto.street(), dto.postalCode(), dto.city(), dto.country());

        Address address = new Address();

        address.setStreet(dto.street());
        address.setPostalCode(dto.postalCode());
        address.setCity(dto.city());
        address.setCountry(dto.country());

        return address;
    }

    @Override
    protected Address updateDtoToEntity(Address address, UpdateAddressDTO dto) {

        if (dto.street() != null) {

            validateNotBlank(dto.street(), "Street cannot be blank");
            address.setStreet(dto.street());
        }

        if (dto.postalCode() != null) {

            validateNotBlank(dto.postalCode(), "Postal code cannot be blank");
            address.setPostalCode(dto.postalCode());
        }

        if (dto.city() != null) {

            validateNotBlank(dto.city(), "City cannot be blank");
            address.setCity(dto.city());
        }

        if (dto.country() != null) {

            validateNotBlank(dto.country(), "Country cannot be blank");
            address.setCountry(dto.country());
        }

        return address;
    }

    private void validateAddressFields(String street, String postalCode, String city, String country) {

        validateNotBlank(street, "Street is required");
        validateNotBlank(postalCode, "Postal code is required");
        validateNotBlank(city, "City is required");
        validateNotBlank(country, "Country is required");
    }

    private void validateNotBlank(String value, String message) {

        if (value == null || value.isBlank()) {
            throw new ApiException(400, message);
        }
    }
}