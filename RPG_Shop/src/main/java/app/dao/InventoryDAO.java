package app.dao;

import app.entities.Inventory;
import app.utils.EMF;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class InventoryDAO extends AbstractDAO<Inventory, Integer> {

    public InventoryDAO() {
        super(Inventory.class);
    }

    public Optional<Inventory> getByItemId(int itemId) {

        try (EntityManager em = EMF.get().createEntityManager()) {

            List<Inventory> inventories = em.createQuery("SELECT i " +
                                                            "FROM Inventory i " +
                                                            "WHERE i.item.id = :itemId", Inventory.class)
                    .setParameter("itemId", itemId).getResultList();

            return inventories.stream().findFirst();
        }
    }
}