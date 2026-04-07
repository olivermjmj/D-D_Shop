package app.dao;

import app.entities.Item;
import app.utils.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;

public class ItemDAO extends AbstractDAO<Item, Integer> {

    public ItemDAO() {
        super(Item.class);
    }

    public List<Item> getAllByCategoryId(int categoryId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT i " +
                                     "FROM Item i " +
                                     "WHERE i.itemCategory.id = :categoryId", Item.class).setParameter("categoryId", categoryId).getResultList();
        }
    }

    public List<Item> getAllBySupplierId(int supplierId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT i " +
                                     "FROM Item i " +
                                     "WHERE i.supplier.id = :supplierId", Item.class).setParameter("supplierId", supplierId).getResultList();
        }
    }

    public List<Item> getAllByExternalSource(String externalSource) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT i " +
                                     "FROM Item i " +
                                     "WHERE i.externalSource = :externalSource", Item.class).setParameter("externalSource", externalSource).getResultList();
        }
    }

    public Optional<Item> getByExternalId(String externalId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            List<Item> items = em.createQuery("SELECT i " +
                                                 "FROM Item i " +
                                                 "WHERE i.externalId = :externalId", Item.class).setParameter("externalId", externalId).getResultList();

            return items.stream().findFirst();
        }
    }

    public Optional<Item> getByExternalIdAndSource(String externalId, String externalSource) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            List<Item> items = em.createQuery("SELECT i " +
                                                 "FROM Item i " +
                                                 "WHERE i.externalId = :externalId " +
                                                 "AND i.externalSource = :externalSource", Item.class)
                                                 .setParameter("externalId", externalId)
                                                 .setParameter("externalSource", externalSource)
                                                 .getResultList();

            return items.stream().findFirst();
        }
    }
}