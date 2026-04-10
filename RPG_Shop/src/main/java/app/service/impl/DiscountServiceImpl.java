package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.DiscountDAO;
import app.dto.discount.CreateDiscountDTO;
import app.dto.discount.DiscountResponseDTO;
import app.dto.discount.UpdateDiscountDTO;
import app.entities.Discount;
import app.exceptions.ApiException;

import java.util.concurrent.ExecutorService;

public class DiscountServiceImpl extends AbstractService<CreateDiscountDTO, UpdateDiscountDTO, DiscountResponseDTO, Discount, Integer> {

    private final DiscountDAO discountDAO;

    public DiscountServiceImpl() {
        this(new DiscountDAO(), ThreadPoolConfig.getExecutor());
    }

    public DiscountServiceImpl(DiscountDAO discountDAO, ExecutorService executorService) {

        super(discountDAO, DiscountResponseDTO::fromEntity, executorService);
        this.discountDAO = discountDAO;
    }

    @Override
    protected Discount createDtoToEntity(CreateDiscountDTO dto) {

        validateDiscountPercentage(dto.discountPercentage());

        Discount discount = new Discount();
        discount.setDiscountPercentage(dto.discountPercentage());

        return discount;
    }

    @Override
    protected Discount updateDtoToEntity(Discount discount, UpdateDiscountDTO dto) {

        if (dto.discountPercentage() != null) {
            validateDiscountPercentage(dto.discountPercentage());
            discount.setDiscountPercentage(dto.discountPercentage());
        }

        return discount;
    }

    private void validateDiscountPercentage(double discountPercentage) {

        if (discountPercentage < 0 || discountPercentage > 100) {

            throw new ApiException(400, "Discount percentage must be between 0 and 100");
        }
    }
}