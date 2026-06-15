package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.AddressDAO;
import app.dao.OrderDAO;
import app.dao.UserDAO;
import app.dto.order.CreateOrderDTO;
import app.dto.order.OrderResponseDTO;
import app.dto.order.UpdateOrderDTO;
import app.dto.order.UpdateOrderStatusDTO;
import app.entities.Address;
import app.entities.Order;
import app.entities.enums.OrderStatus;
import app.entities.enums.Role;
import app.exceptions.ApiException;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class OrderServiceImpl extends AbstractService<CreateOrderDTO, UpdateOrderDTO, OrderResponseDTO, Order, Integer> {

    private final OrderDAO orderDAO;
    private final UserDAO userDAO = new UserDAO();
    private final AddressDAO addressDAO = new AddressDAO();

    public OrderServiceImpl() {
        this(new OrderDAO(), ThreadPoolConfig.getExecutor());
    }

    public OrderServiceImpl(OrderDAO orderDAO, ExecutorService executorService) {
        super(orderDAO, OrderResponseDTO::fromEntity, executorService);
        this.orderDAO = orderDAO;
    }

    @Override
    protected Order createDtoToEntity(CreateOrderDTO dto) {
        throw new ApiException(400, "Use createForUser");
    }

    @Override
    protected Order updateDtoToEntity(Order order, UpdateOrderDTO dto) {
        try {
            if (dto.addressId() != null) {
                order.setAddress(addressDAO.getById(dto.addressId())
                        .orElseThrow(() -> new ApiException(404, "Address not found")));
            }

            return order;

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Could not update order");
        }
    }

    public CompletableFuture<OrderResponseDTO> createForUser(CreateOrderDTO dto, int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (dto.addressId() <= 0) {
                    throw new ApiException(400, "Address id is required");
                }

                Order order = new Order();

                order.setUser(userDAO.getById(userId)
                        .orElseThrow(() -> new ApiException(404, "User not found")));

                order.setAddress(addressDAO.getById(dto.addressId())
                        .orElseThrow(() -> new ApiException(404, "Address not found")));

                order.setOrderStatus(OrderStatus.CREATED);

                return OrderResponseDTO.fromEntity(orderDAO.create(order));

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not create order");
            }
        }, executorService);
    }

    public CompletableFuture<OrderResponseDTO> getByIdForUser(int id, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Order order = orderDAO.getById(id)
                        .orElseThrow(() -> new ApiException(404, "Order not found"));

                requireOwnerOrAdmin(order, userId, role);

                return OrderResponseDTO.fromEntity(order);

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get order");
            }
        }, executorService);
    }

    public CompletableFuture<OrderResponseDTO> getByIdWithItemsForUser(int id, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Order order = orderDAO.getByIdWithItems(id)
                        .orElseThrow(() -> new ApiException(404, "Order not found"));

                requireOwnerOrAdmin(order, userId, role);

                return OrderResponseDTO.fromEntity(order);

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get order with items");
            }
        }, executorService);
    }

    public CompletableFuture<List<OrderResponseDTO>> getMyOrders(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return orderDAO.getAllByUserId(userId)
                        .stream()
                        .map(OrderResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get orders");
            }
        }, executorService);
    }

    public CompletableFuture<List<OrderResponseDTO>> getAllByUserId(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDAO.getById(userId)
                        .orElseThrow(() -> new ApiException(404, "User not found"));

                return orderDAO.getAllByUserId(userId)
                        .stream()
                        .map(OrderResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get orders by user");
            }
        }, executorService);
    }

    public CompletableFuture<List<OrderResponseDTO>> getAllByStatus(OrderStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return orderDAO.getAllByStatus(status)
                        .stream()
                        .map(OrderResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get orders by status");
            }
        }, executorService);
    }

    public CompletableFuture<BigDecimal> getTotalPriceForUser(int id, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Order order = orderDAO.getById(id)
                        .orElseThrow(() -> new ApiException(404, "Order not found"));

                requireOwnerOrAdmin(order, userId, role);

                return orderDAO.getTotalPriceByOrderId(id);

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get total price");
            }
        }, executorService);
    }

    public CompletableFuture<OrderResponseDTO> updateAddressForUser(int id, UpdateOrderDTO dto, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Order order = orderDAO.getById(id)
                        .orElseThrow(() -> new ApiException(404, "Order not found"));

                requireOwnerOrAdmin(order, userId, role);

                if (dto.addressId() != null) {
                    Address address = addressDAO.getById(dto.addressId())
                            .orElseThrow(() -> new ApiException(404, "Address not found"));

                    order.setAddress(address);
                }

                return OrderResponseDTO.fromEntity(orderDAO.update(order));

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not update order address");
            }
        }, executorService);
    }

    public CompletableFuture<OrderResponseDTO> updateStatus(int id, UpdateOrderStatusDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (dto.orderStatus() == null) {
                    throw new ApiException(400, "Order status is required");
                }

                Order order = orderDAO.getById(id)
                        .orElseThrow(() -> new ApiException(404, "Order not found"));

                order.setOrderStatus(dto.orderStatus());

                return OrderResponseDTO.fromEntity(orderDAO.update(order));

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not update order status");
            }
        }, executorService);
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