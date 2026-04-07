package app.dao;

import app.entities.StockChange;
import app.utils.EMF;
import jakarta.persistence.EntityManager;

import java.util.List;

public class StockChangeDAO extends AbstractDAO<StockChange, Integer> {

    public StockChangeDAO() {
        super(StockChange.class);
    }

    public List<StockChange> getAllByItemId(int itemId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT s " +
                                     "FROM StockChange s " +
                                     "WHERE s.item.id = :itemId", StockChange.class)
                    .setParameter("itemId", itemId).getResultList();
        }
    }

    public List<StockChange> getAllByAdminId(int adminId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT s " +
                                     "FROM StockChange s " +
                                     "WHERE s.performedBy.id = :adminId", StockChange.class)
                    .setParameter("adminId", adminId).getResultList();
        }
    }
}