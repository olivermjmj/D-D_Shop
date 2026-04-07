package app.dao;

import app.entities.Transaction;
import app.entities.enums.TransactionType;
import app.utils.EMF;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TransactionDAO extends AbstractDAO<Transaction, Integer> {

    public TransactionDAO() {
        super(Transaction.class);
    }

    public List<Transaction> getAllByUserId(int userId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT t " +
                                     "FROM Transaction t " +
                                     "WHERE t.user.id = :userId", Transaction.class)
                    .setParameter("userId", userId).getResultList();
        }
    }

    public List<Transaction> getAllByOrderId(int orderId) {

        try (EntityManager em = EMF.get().createEntityManager()) {
            return em.createQuery("SELECT t FROM Transaction t WHERE t.order.id = :orderId", Transaction.class)
                    .setParameter("orderId", orderId)
                    .getResultList();
        }
    }

    public List<Transaction> getAllByType(TransactionType type) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT t FROM Transaction t WHERE t.type = :type", Transaction.class)
                    .setParameter("type", type)
                    .getResultList();
        }
    }
}