package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.AddressDAO;
import app.dao.OrderDAO;
import app.dao.UserDAO;
import app.dto.order.CreateOrderDTO;
import app.dto.order.OrderResponseDTO;
import app.dto.order.UpdateOrderDTO;
import app.entities.Order;
import app.entities.enums.OrderStatus;
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

        Order order = new Order();

        order.setUser(userDAO.getById(dto.userId()).orElseThrow(() -> new ApiException(404, "User not found")));

        order.setAddress(addressDAO.getById(dto.addressId()).orElseThrow(() -> new ApiException(404, "Address not found")));

        order.setOrderStatus(OrderStatus.CREATED);

        return order;
    }

    @Override
    protected Order updateDtoToEntity(Order order, UpdateOrderDTO dto) {

        if (dto.orderStatus() != null) {

            order.setOrderStatus(dto.orderStatus());
        }

        if (dto.addressId() != null) {

            order.setAddress(addressDAO.getById(dto.addressId()).orElseThrow(() -> new ApiException(404, "Address not found")));
        }

        return order;
    }

    public CompletableFuture<OrderResponseDTO> getByIdWithItems(int id) {

        return CompletableFuture.supplyAsync(() -> {

            Order order = orderDAO.getByIdWithItems(id).orElseThrow(() -> new ApiException(404, "Order not found"));

            return OrderResponseDTO.fromEntity(order);
            }, executorService);
    }

    public CompletableFuture<List<OrderResponseDTO>> getAllByUserId(int userId) {

        return CompletableFuture.supplyAsync(() -> {

            userDAO.getById(userId).orElseThrow(() -> new ApiException(404, "User not found"));

            return orderDAO.getAllByUserId(userId)
                    .stream()
                    .map(OrderResponseDTO::fromEntity)
                    .toList();
        }, executorService);
    }

    public CompletableFuture<List<OrderResponseDTO>> getAllByStatus(OrderStatus status) {

        return CompletableFuture.supplyAsync(() -> orderDAO.getAllByStatus(status)
                                .stream()
                                .map(OrderResponseDTO::fromEntity)
                                .toList(), executorService);
    }

    public CompletableFuture<BigDecimal> getTotalPriceByOrderId(int id) {

        return CompletableFuture.supplyAsync(() -> {

            BigDecimal totalPrice = orderDAO.getTotalPriceByOrderId(id);

            if (totalPrice == null) {
                throw new ApiException(404, "Order not found");
            }

            return totalPrice;
        }, executorService);
    }
}