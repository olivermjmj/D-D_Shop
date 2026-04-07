package app.service.impl;

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

public class OrderServiceImpl extends AbstractService<CreateOrderDTO, UpdateOrderDTO, OrderResponseDTO, Order, Integer> {

    private final OrderDAO orderDAO;
    private final UserDAO userDAO = new UserDAO();
    private final AddressDAO addressDAO = new AddressDAO();

    public OrderServiceImpl() {

        this(new OrderDAO());
    }

    public OrderServiceImpl(OrderDAO orderDAO) {

        super(orderDAO, OrderResponseDTO::fromEntity);
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

    public OrderResponseDTO getByIdWithItems(int id) {

        Order order = orderDAO.getByIdWithItems(id).orElseThrow(() -> new ApiException(404, "Order not found"));

        return OrderResponseDTO.fromEntity(order);
    }

    public List<OrderResponseDTO> getAllByUserId(int userId) {

        return orderDAO.getAllByUserId(userId)
                .stream()
                .map(OrderResponseDTO::fromEntity)
                .toList();
    }

    public List<OrderResponseDTO> getAllByStatus(OrderStatus status) {

        return orderDAO.getAllByStatus(status)
                .stream()
                .map(OrderResponseDTO::fromEntity)
                .toList();
    }

    public BigDecimal getTotalPriceByOrderId(int id) {

        return orderDAO.getTotalPriceByOrderId(id);
    }
}