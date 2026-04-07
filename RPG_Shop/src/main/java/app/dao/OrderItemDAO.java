package app.dao;

import app.entities.OrderItem;
import app.utils.EMF;
import jakarta.persistence.EntityManager;

import java.util.List;

public class OrderItemDAO extends AbstractDAO<OrderItem, Integer> {

    public OrderItemDAO() {
        super(OrderItem.class);
    }

    public List<OrderItem> getAllByOrderId(int orderId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT oi " +
                                     "FROM OrderItem oi " +
                                     "WHERE oi.order.id = :orderId", OrderItem.class)
                    .setParameter("orderId", orderId).getResultList();
        }
    }

    public List<OrderItem> getAllByItemId(int itemId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT oi " +
                                     "FROM OrderItem oi " +
                                     "WHERE oi.item.id = :itemId", OrderItem.class)
                    .setParameter("itemId", itemId).getResultList();
        }
    }
}