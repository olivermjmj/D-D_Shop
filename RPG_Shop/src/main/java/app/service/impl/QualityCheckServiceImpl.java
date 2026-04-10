package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.ItemDAO;
import app.dao.QualityCheckDAO;
import app.dao.UserDAO;
import app.dto.qualityCheck.CreateQualityCheckDTO;
import app.dto.qualityCheck.QualityCheckResponseDTO;
import app.dto.qualityCheck.UpdateQualityCheckDTO;
import app.entities.QualityCheck;
import app.entities.enums.QualityStatus;
import app.exceptions.ApiException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class QualityCheckServiceImpl extends AbstractService<CreateQualityCheckDTO, UpdateQualityCheckDTO, QualityCheckResponseDTO, QualityCheck, Integer> {

    private final QualityCheckDAO qualityCheckDAO;
    private final ItemDAO itemDAO = new ItemDAO();
    private final UserDAO userDAO = new UserDAO();

    public QualityCheckServiceImpl() {
        this(new QualityCheckDAO(), ThreadPoolConfig.getExecutor());
    }

    public QualityCheckServiceImpl(QualityCheckDAO qualityCheckDAO, ExecutorService executorService) {

        super(qualityCheckDAO, QualityCheckResponseDTO::fromEntity, executorService);
        this.qualityCheckDAO = qualityCheckDAO;
    }

    @Override
    protected QualityCheck createDtoToEntity(CreateQualityCheckDTO dto) {

        if (dto.status() == null) {
            throw new ApiException(400, "Quality status is required");
        }

        QualityCheck qualityCheck = new QualityCheck();

        qualityCheck.setItem(itemDAO.getById(dto.itemId()).orElseThrow(() -> new ApiException(404, "Item not found")));

        qualityCheck.setStatus(dto.status());

        if (dto.approvedByUserId() != null) {

            qualityCheck.setApprovedBy(userDAO.getById(dto.approvedByUserId()).orElseThrow(() -> new ApiException(404, "User not found")));
        }

        return qualityCheck;
    }

    @Override
    protected QualityCheck updateDtoToEntity(QualityCheck qualityCheck, UpdateQualityCheckDTO dto) {

        if (dto.status() != null) {
            qualityCheck.setStatus(dto.status());
        }

        if (dto.approvedByUserId() != null) {
            qualityCheck.setApprovedBy(userDAO.getById(dto.approvedByUserId()).orElseThrow(() -> new ApiException(404, "User not found")));
        }

        return qualityCheck;
    }

    public CompletableFuture<List<QualityCheckResponseDTO>> getAllByItemId(int itemId) {

        return CompletableFuture.supplyAsync(() -> {itemDAO.getById(itemId).orElseThrow(() -> new ApiException(404, "Item not found"));

            return qualityCheckDAO.getAllByItemId(itemId)
                    .stream()
                    .map(QualityCheckResponseDTO::fromEntity)
                    .toList();
        }, executorService);
    }

    public CompletableFuture<List<QualityCheckResponseDTO>> getAllByStatus(QualityStatus status) {

        return CompletableFuture.supplyAsync(() -> qualityCheckDAO.getAllByStatus(status)
                                .stream()
                                .map(QualityCheckResponseDTO::fromEntity)
                                .toList()
                , executorService);
    }

    public CompletableFuture<List<QualityCheckResponseDTO>> getAllByApprovedById(int userId) {

        return CompletableFuture.supplyAsync(() -> {userDAO.getById(userId).orElseThrow(() -> new ApiException(404, "User not found"));

            return qualityCheckDAO.getAllByApprovedById(userId)
                    .stream()
                    .map(QualityCheckResponseDTO::fromEntity)
                    .toList();
        }, executorService);
    }
}