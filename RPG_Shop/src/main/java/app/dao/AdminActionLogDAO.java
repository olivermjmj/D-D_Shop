package app.dao;

import app.entities.AdminActionLog;
import app.utils.EMF;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AdminActionLogDAO extends AbstractDAO<AdminActionLog, Integer> {

    public AdminActionLogDAO() {
        super(AdminActionLog.class);
    }

    public List<AdminActionLog> getAllByAdminId(int adminId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT a " +
                                     "FROM AdminActionLog a " +
                                     "WHERE a.admin.id = :adminId", AdminActionLog.class)
                    .setParameter("adminId", adminId).getResultList();
        }
    }
}