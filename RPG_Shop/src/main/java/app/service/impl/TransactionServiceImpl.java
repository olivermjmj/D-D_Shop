package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.OrderDAO;
import app.dao.TransactionDAO;
import app.dao.UserDAO;
import app.dto.transaction.CreateTransactionDTO;
import app.dto.transaction.TransactionResponseDTO;
import app.dto.transaction.UpdateTransactionDTO;
import app.entities.Transaction;
import app.entities.enums.TransactionType;
import app.exceptions.ApiException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class TransactionServiceImpl extends AbstractService<CreateTransactionDTO, UpdateTransactionDTO, TransactionResponseDTO, Transaction, Integer> {

    private final TransactionDAO transactionDAO;
    private final UserDAO userDAO = new UserDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    public TransactionServiceImpl() {

        this(new TransactionDAO(), ThreadPoolConfig.getExecutor());
    }

    public TransactionServiceImpl(TransactionDAO transactionDAO, ExecutorService executorService) {

        super(transactionDAO, TransactionResponseDTO::fromEntity, executorService);
        this.transactionDAO = transactionDAO;
    }

    @Override
    protected Transaction createDtoToEntity(CreateTransactionDTO dto) {

        Transaction transaction = new Transaction();

        if (dto.orderId() != null) {
            transaction.setOrder(
                    orderDAO.getById(dto.orderId())
                            .orElseThrow(() -> new ApiException(404, "Order not found"))
            );
        }

        transaction.setUser(
                userDAO.getById(dto.userId())
                        .orElseThrow(() -> new ApiException(404, "User not found"))
        );
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());

        return transaction;
    }

    @Override
    protected Transaction updateDtoToEntity(Transaction transaction, UpdateTransactionDTO dto) {

        if (dto.amount() != null) {
            transaction.setAmount(dto.amount());
        }

        if (dto.type() != null) {
            transaction.setType(dto.type());
        }

        return transaction;
    }

    public CompletableFuture<List<TransactionResponseDTO>> getAllByUserId(int userId) {

        return CompletableFuture.supplyAsync(() -> {

            userDAO.getById(userId).orElseThrow(() -> new ApiException(404, "User not found"));

            return transactionDAO.getAllByUserId(userId)
                    .stream()
                    .map(TransactionResponseDTO::fromEntity)
                    .toList();
        }, executorService);
    }

    public CompletableFuture<List<TransactionResponseDTO>> getAllByOrderId(int orderId) {

        return CompletableFuture.supplyAsync(() -> {

            orderDAO.getById(orderId).orElseThrow(() -> new ApiException(404, "Order not found"));

            return transactionDAO.getAllByOrderId(orderId)
                    .stream()
                    .map(TransactionResponseDTO::fromEntity)
                    .toList();
        }, executorService);
    }

    public CompletableFuture<List<TransactionResponseDTO>> getAllByType(TransactionType type) {

        return CompletableFuture.supplyAsync(() -> transactionDAO.getAllByType(type)
                                .stream()
                                .map(TransactionResponseDTO::fromEntity)
                                .toList()
                , executorService);
    }
}