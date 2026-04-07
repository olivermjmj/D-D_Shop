package app.dao;

import app.entities.Order;
import app.entities.enums.OrderStatus;
import app.utils.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OrderDAO extends AbstractDAO<Order, Integer> {

    public OrderDAO() {
        super(Order.class);
    }

    public Optional<Order> getByIdWithItems(int id) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            Order order = em.createQuery("SELECT o FROM Order o " +
                                            "LEFT JOIN FETCH o.orderItems " +
                                            "WHERE o.id = :id", Order.class).setParameter("id", id).getSingleResult();

            return Optional.of(order);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Order> getAllByUserId(int userId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT o " +
                                     "FROM Order o " +
                                     "WHERE o.user.id = :userId", Order.class).setParameter("userId", userId).getResultList();
        }
    }

    public List<Order> getAllByStatus(OrderStatus status) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT o " +
                                     "FROM Order o " +
                                     "WHERE o.orderStatus = :status", Order.class).setParameter("status", status).getResultList();
        }
    }

    public BigDecimal getTotalPriceByOrderId(int orderId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            BigDecimal totalOrderPrice = em.createQuery("SELECT SUM(oi.priceAtPurchase * oi.quantity) " +
                                                           "FROM OrderItem oi " +
                                                           "WHERE oi.order.id = :orderId", BigDecimal.class).setParameter("orderId", orderId).getSingleResult();

            return totalOrderPrice != null ? totalOrderPrice : BigDecimal.ZERO;
        }
    }

    public long countByUserId(int userId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT COUNT(o) " +
                                     "FROM Order o " +
                                     "WHERE o.user.id = :userId", Long.class).setParameter("userId", userId).getSingleResult();
        }
    }
}