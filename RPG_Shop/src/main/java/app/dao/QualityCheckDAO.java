package app.dao;

import app.entities.QualityCheck;
import app.entities.enums.QualityStatus;
import app.utils.EMF;
import jakarta.persistence.EntityManager;

import java.util.List;

public class QualityCheckDAO extends AbstractDAO<QualityCheck, Integer> {

    public QualityCheckDAO() {
        super(QualityCheck.class);
    }

    public List<QualityCheck> getAllByItemId(int itemId) {
        try (EntityManager em = EMF.get().createEntityManager()) {
            return em.createQuery("SELECT q " +
                                     "FROM QualityCheck q " +
                                     "WHERE q.item.id = :itemId", QualityCheck.class)
                    .setParameter("itemId", itemId).getResultList();
        }
    }

    public List<QualityCheck> getAllByStatus(QualityStatus status) {
        try (EntityManager em = EMF.get().createEntityManager()) {
            return em.createQuery("SELECT q " +
                                     "FROM QualityCheck q " +
                                     "WHERE q.status = :status", QualityCheck.class)
                    .setParameter("status", status).getResultList();
        }
    }

    public List<QualityCheck> getAllByApprovedById(int userId) {
        try (EntityManager em = EMF.get().createEntityManager()) {
            return em.createQuery("SELECT q " +
                                     "FROM QualityCheck q " +
                                     "WHERE q.approvedBy.id = :userId", QualityCheck.class)
                    .setParameter("userId", userId).getResultList();
        }
    }
}