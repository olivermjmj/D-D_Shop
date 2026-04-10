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

    @Override
    protected OrderItem createDtoToEntity(CreateOrderItemDTO dto) {

        if (dto.quantity() <= 0) {
            throw new ApiException(400, "Quantity must be greater than 0");
        }

        Order order = orderDAO.getById(dto.orderId()).orElseThrow(() -> new ApiException(404, "Order not found"));

        Item item = itemDAO.getById(dto.itemId()).orElseThrow(() -> new ApiException(404, "Item not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(dto.quantity());
        orderItem.setPriceAtPurchase(item.getBasePrice());

        return orderItem;
    }

    @Override
    protected OrderItem updateDtoToEntity(OrderItem orderItem, UpdateOrderItemDTO dto) {

        if (dto.quantity() != null) {
            if (dto.quantity() <= 0) {

                throw new ApiException(400, "Quantity must be greater than 0");
            }
            orderItem.setQuantity(dto.quantity());
        }

        return orderItem;
    }

    public CompletableFuture<List<OrderItemResponseDTO>> getAllByOrderId(int orderId) {

        return CompletableFuture.supplyAsync(() -> {

            orderDAO.getById(orderId).orElseThrow(() -> new ApiException(404, "Order not found"));

            return orderItemDAO.getAllByOrderId(orderId)
                    .stream()
                    .map(OrderItemResponseDTO::fromEntity)
                    .toList();
        }, executorService);
    }

    public CompletableFuture<List<OrderItemResponseDTO>> getAllByItemId(int itemId) {

        return CompletableFuture.supplyAsync(() -> {itemDAO.getById(itemId).orElseThrow(() -> new ApiException(404, "Item not found"));

            return orderItemDAO.getAllByItemId(itemId)
                    .stream()
                    .map(OrderItemResponseDTO::fromEntity)
                    .toList();
        }, executorService);
    }
}