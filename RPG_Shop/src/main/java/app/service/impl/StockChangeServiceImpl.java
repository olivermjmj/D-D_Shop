package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.ItemDAO;
import app.dao.StockChangeDAO;
import app.dao.UserDAO;
import app.dto.stockChange.CreateStockChangeDTO;
import app.dto.stockChange.StockChangeResponseDTO;
import app.dto.stockChange.UpdateStockChangeDTO;
import app.entities.StockChange;
import app.exceptions.ApiException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class StockChangeServiceImpl extends AbstractService<CreateStockChangeDTO, UpdateStockChangeDTO, StockChangeResponseDTO, StockChange, Integer> {

    private final StockChangeDAO stockChangeDAO;
    private final ItemDAO itemDAO = new ItemDAO();
    private final UserDAO userDAO = new UserDAO();

    public StockChangeServiceImpl() {
        this(new StockChangeDAO(), ThreadPoolConfig.getExecutor());
    }

    public StockChangeServiceImpl(StockChangeDAO stockChangeDAO, ExecutorService executorService) {

        super(stockChangeDAO, StockChangeResponseDTO::fromEntity, executorService);
        this.stockChangeDAO = stockChangeDAO;
    }

    @Override
    protected StockChange createDtoToEntity(CreateStockChangeDTO dto) {

        if (dto.delta() == 0) {
            throw new ApiException(400, "Delta cannot be 0");
        }

        StockChange stockChange = new StockChange();

        stockChange.setItem(itemDAO.getById(dto.itemId()).orElseThrow(() -> new ApiException(404, "Item not found")));

        stockChange.setDelta(dto.delta());
        stockChange.setReason(dto.reason());

        if (dto.performedByUserId() != null) {

            stockChange.setPerformedBy(
                    userDAO.getById(dto.performedByUserId()).orElseThrow(() -> new ApiException(404, "User not found"))
            );
        }

        return stockChange;
    }

    @Override
    protected StockChange updateDtoToEntity(StockChange stockChange, UpdateStockChangeDTO dto) {

        if (dto.delta() != null) {
            if (dto.delta() == 0) {

                throw new ApiException(400, "Delta cannot be 0");
            }
            stockChange.setDelta(dto.delta());
        }

        if (dto.reason() != null) {
            stockChange.setReason(dto.reason());
        }

        if (dto.performedByUserId() != null) {

            stockChange.setPerformedBy(userDAO.getById(dto.performedByUserId()).orElseThrow(() -> new ApiException(404, "User not found"))
            );
        }

        return stockChange;
    }

    public CompletableFuture<List<StockChangeResponseDTO>> getAllByItemId(int itemId) {

        return CompletableFuture.supplyAsync(() -> {itemDAO.getById(itemId).orElseThrow(() -> new ApiException(404, "Item not found"));

            return stockChangeDAO.getAllByItemId(itemId)
                    .stream()
                    .map(StockChangeResponseDTO::fromEntity)
                    .toList();
        }, executorService);
    }

    public CompletableFuture<List<StockChangeResponseDTO>> getAllByAdminId(int adminId) {

        return CompletableFuture.supplyAsync(() -> {userDAO.getById(adminId).orElseThrow(() -> new ApiException(404, "User not found"));

            return stockChangeDAO.getAllByAdminId(adminId)
                    .stream()
                    .map(StockChangeResponseDTO::fromEntity)
                    .toList();
        }, executorService);
    }
}