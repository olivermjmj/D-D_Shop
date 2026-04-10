package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.AddressDAO;
import app.dao.SupplierDAO;
import app.dto.supplier.CreateSupplierDTO;
import app.dto.supplier.SupplierResponseDTO;
import app.dto.supplier.UpdateSupplierDTO;
import app.entities.Supplier;
import app.exceptions.ApiException;

import java.util.concurrent.ExecutorService;

public class SupplierServiceImpl extends AbstractService<CreateSupplierDTO, UpdateSupplierDTO, SupplierResponseDTO, Supplier, Integer> {

    private final SupplierDAO supplierDAO;
    private final AddressDAO addressDAO = new AddressDAO();

    public SupplierServiceImpl() {
        this(new SupplierDAO(), ThreadPoolConfig.getExecutor());
    }

    public SupplierServiceImpl(SupplierDAO supplierDAO, ExecutorService executorService) {
        super(supplierDAO, SupplierResponseDTO::fromEntity, executorService);
        this.supplierDAO = supplierDAO;
    }

    @Override
    protected Supplier createDtoToEntity(CreateSupplierDTO dto) {
        Supplier supplier = new Supplier();

        supplier.setName(dto.name());

        if (dto.addressId() != null) {
            supplier.setAddress(
                    addressDAO.getById(dto.addressId())
                            .orElseThrow(() -> new ApiException(404, "Address not found"))
            );
        }

        return supplier;
    }

    @Override
    protected Supplier updateDtoToEntity(Supplier supplier, UpdateSupplierDTO dto) {
        if (dto.name() != null) {
            supplier.setName(dto.name());
        }

        if (dto.addressId() != null) {
            supplier.setAddress(
                    addressDAO.getById(dto.addressId())
                            .orElseThrow(() -> new ApiException(404, "Address not found"))
            );
        }

        return supplier;
    }
}