package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.ItemDAO;
import app.dao.OrderDAO;
import app.dao.OrderItemDAO;
import app.dto.orderItem.CreateOrderItemDTO;
import app.dto.orderItem.OrderItemResponseDTO;
import app.dto.orderItem.UpdateOrderItemDTO;
import app.entities.Item;
import app.entities.Order;
import app.entities.OrderItem;
import app.entities.enums.Role;
import app.exceptions.ApiException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class OrderItemServiceImpl extends AbstractService<CreateOrderItemDTO, UpdateOrderItemDTO, OrderItemResponseDTO, OrderItem, Integer> {

    private final OrderItemDAO orderItemDAO;
    private final OrderDAO orderDAO = new OrderDAO();
    private final ItemDAO itemDAO = new ItemDAO();

    public OrderItemServiceImpl() {
        this(new OrderItemDAO(), ThreadPoolConfig.getExecutor());
    }

    public OrderItemServiceImpl(OrderItemDAO orderItemDAO, ExecutorService executorService) {
        super(orderItemDAO, OrderItemResponseDTO::fromEntity, executorService);
        this.orderItemDAO = orderItemDAO;
    }

    public CompletableFuture<OrderItemResponseDTO> createForUser(CreateOrderItemDTO dto, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (dto.quantity() <= 0) {
                    throw new ApiException(400, "Quantity must be greater than 0");
                }

                Order order = orderDAO.getById(dto.orderId())
                        .orElseThrow(() -> new ApiException(404, "Order not found"));

                requireOwnerOrAdmin(order, userId, role);

                Item item = itemDAO.getById(dto.itemId())
                        .orElseThrow(() -> new ApiException(404, "Item not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setItem(item);
                orderItem.setQuantity(dto.quantity());
                orderItem.setPriceAtPurchase(item.getBasePrice());

                return OrderItemResponseDTO.fromEntity(orderItemDAO.create(orderItem));

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not create order item");
            }
        }, executorService);
    }

    public CompletableFuture<OrderItemResponseDTO> getByIdForUser(int id, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OrderItem orderItem = orderItemDAO.getById(id)
                        .orElseThrow(() -> new ApiException(404, "Order item not found"));

                requireOwnerOrAdmin(orderItem.getOrder(), userId, role);

                return OrderItemResponseDTO.fromEntity(orderItem);

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get order item");
            }
        }, executorService);
    }

    public CompletableFuture<List<OrderItemResponseDTO>> getAllByOrderIdForUser(int orderId, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Order order = orderDAO.getById(orderId)
                        .orElseThrow(() -> new ApiException(404, "Order not found"));

                requireOwnerOrAdmin(order, userId, role);

                return orderItemDAO.getAllByOrderId(orderId)
                        .stream()
                        .map(OrderItemResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get order items by order");
            }
        }, executorService);
    }

    public CompletableFuture<OrderItemResponseDTO> updateForUser(int id, UpdateOrderItemDTO dto, int userId, Role role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OrderItem orderItem = orderItemDAO.getById(id)
                        .orElseThrow(() -> new ApiException(404, "Order item not found"));

                requireOwnerOrAdmin(orderItem.getOrder(), userId, role);

                if (dto.quantity() != null) {
                    if (dto.quantity() <= 0) {
                        throw new ApiException(400, "Quantity must be greater than 0");
                    }

                    orderItem.setQuantity(dto.quantity());
                }

                return OrderItemResponseDTO.fromEntity(orderItemDAO.update(orderItem));

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not update order item");
            }
        }, executorService);
    }

    public CompletableFuture<Void> deleteForUser(int id, int userId, Role role) {

        return CompletableFuture.runAsync(() -> {
            try {
                OrderItem orderItem = orderItemDAO.getById(id)
                        .orElseThrow(() -> new ApiException(404, "Order item not found"));

                requireOwnerOrAdmin(orderItem.getOrder(), userId, role);

                orderItemDAO.delete(orderItem);

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not delete order item");
            }
        }, executorService);
    }

    @Override
    protected OrderItem createDtoToEntity(CreateOrderItemDTO dto) {
        try {
            if (dto.quantity() <= 0) {
                throw new ApiException(400, "Quantity must be greater than 0");
            }

            Order order = orderDAO.getById(dto.orderId())
                    .orElseThrow(() -> new ApiException(404, "Order not found"));

            Item item = itemDAO.getById(dto.itemId())
                    .orElseThrow(() -> new ApiException(404, "Item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(dto.quantity());
            orderItem.setPriceAtPurchase(item.getBasePrice());

            return orderItem;

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Could not create order item");
        }
    }

    @Override
    protected OrderItem updateDtoToEntity(OrderItem orderItem, UpdateOrderItemDTO dto) {
        try {
            if (dto.quantity() != null) {
                if (dto.quantity() <= 0) {
                    throw new ApiException(400, "Quantity must be greater than 0");
                }

                orderItem.setQuantity(dto.quantity());
            }

            return orderItem;

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Could not update order item");
        }
    }

    public CompletableFuture<List<OrderItemResponseDTO>> getAllByOrderId(int orderId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                orderDAO.getById(orderId)
                        .orElseThrow(() -> new ApiException(404, "Order not found"));

                return orderItemDAO.getAllByOrderId(orderId)
                        .stream()
                        .map(OrderItemResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get order items by order");
            }
        }, executorService);
    }

    public CompletableFuture<List<OrderItemResponseDTO>> getAllByItemId(int itemId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                itemDAO.getById(itemId)
                        .orElseThrow(() -> new ApiException(404, "Item not found"));

                return orderItemDAO.getAllByItemId(itemId)
                        .stream()
                        .map(OrderItemResponseDTO::fromEntity)
                        .toList();

            } catch (ApiException e) {
                throw e;
            } catch (Exception e) {
                throw new ApiException(500, "Could not get order items by item");
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