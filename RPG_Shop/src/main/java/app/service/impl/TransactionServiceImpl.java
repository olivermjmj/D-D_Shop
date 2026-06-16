package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.OrderDAO;
import app.dao.TransactionDAO;
import app.dao.UserDAO;
import app.dto.transaction.CreateTransactionDTO;
import app.dto.transaction.TransactionResponseDTO;
import app.dto.transaction.UpdateTransactionDTO;
import app.entities.Order;
import app.entities.Transaction;
import app.entities.enums.Role;
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

    public CompletableFuture<TransactionResponseDTO> createForUser(CreateTransactionDTO dto, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (role != Role.ADMIN && dto.userId() != userId) {
                    throw new ApiException(403, "Forbidden");
                }

                Transaction transaction = new Transaction();

                if (dto.orderId() != null) {
                    Order order = orderDAO.getById(dto.orderId())
                            .orElseThrow(() -> new ApiException(404, "Order not found"));

                    requireOwnerOrAdmin(order, userId, role);

                    transaction.setOrder(order);
                }

                transaction.setUser(
                        userDAO.getById(dto.userId())
                                .orElseThrow(() -> new ApiException(404, "User not found"))
                );

                transaction.setAmount(dto.amount());
                transaction.setType(dto.type());

                return TransactionResponseDTO.fromEntity(transactionDAO.create(transaction));

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not create transaction");
            }
        }, executorService);
    }

    public CompletableFuture<TransactionResponseDTO> getByIdForUser(int id, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Transaction transaction = transactionDAO.getById(id)
                        .orElseThrow(() -> new ApiException(404, "Transaction not found"));

                requireOwnerOrAdmin(transaction, userId, role);

                return TransactionResponseDTO.fromEntity(transaction);

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get transaction");
            }
        }, executorService);
    }

    public CompletableFuture<List<TransactionResponseDTO>> getAllByUserIdForUser(int requestedUserId, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (role != Role.ADMIN && requestedUserId != userId) {
                    throw new ApiException(403, "Forbidden");
                }

                userDAO.getById(requestedUserId)
                        .orElseThrow(() -> new ApiException(404, "User not found"));

                return transactionDAO.getAllByUserId(requestedUserId)
                        .stream()
                        .map(TransactionResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get transactions by user");
            }
        }, executorService);
    }

    public CompletableFuture<List<TransactionResponseDTO>> getAllByOrderIdForUser(int orderId, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Order order = orderDAO.getById(orderId)
                        .orElseThrow(() -> new ApiException(404, "Order not found"));

                requireOwnerOrAdmin(order, userId, role);

                return transactionDAO.getAllByOrderId(orderId)
                        .stream()
                        .map(TransactionResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get transactions by order");
            }
        }, executorService);
    }

    @Override
    protected Transaction createDtoToEntity(CreateTransactionDTO dto) {
        try {
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

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Could not create transaction");
        }
    }

    @Override
    protected Transaction updateDtoToEntity(Transaction transaction, UpdateTransactionDTO dto) {
        try {
            if (dto.amount() != null) {
                transaction.setAmount(dto.amount());
            }

            if (dto.type() != null) {
                transaction.setType(dto.type());
            }

            return transaction;

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Could not update transaction");
        }
    }

    public CompletableFuture<List<TransactionResponseDTO>> getAllByUserId(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDAO.getById(userId)
                        .orElseThrow(() -> new ApiException(404, "User not found"));

                return transactionDAO.getAllByUserId(userId)
                        .stream()
                        .map(TransactionResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get transactions by user");
            }
        }, executorService);
    }

    public CompletableFuture<List<TransactionResponseDTO>> getAllByOrderId(int orderId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                orderDAO.getById(orderId)
                        .orElseThrow(() -> new ApiException(404, "Order not found"));

                return transactionDAO.getAllByOrderId(orderId)
                        .stream()
                        .map(TransactionResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get transactions by order");
            }
        }, executorService);
    }

    public CompletableFuture<List<TransactionResponseDTO>> getAllByType(TransactionType type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return transactionDAO.getAllByType(type)
                        .stream()
                        .map(TransactionResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get transactions by type");
            }
        }, executorService);
    }

    private void requireOwnerOrAdmin(Transaction transaction, int userId, Role role) {
        if (role == Role.ADMIN) {
            return;
        }

        if (transaction.getUser() == null || transaction.getUser().getId() != userId) {
            throw new ApiException(403, "Forbidden");
        }
    }

    private void requireOwnerOrAdmin(Order order, int userId, Role role) {
        if (role == Role.ADMIN) {
            return;
        }

        if (order.getUser() == null || order.getUser().getId() != userId) {
            throw new ApiException(403, "Forbidden");
        }
    }
}