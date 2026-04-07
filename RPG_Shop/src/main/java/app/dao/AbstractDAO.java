package app.dao;

import app.dao.interfaces.IDAO;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDAO<T, ID> implements IDAO<T, ID> {

    protected final Class<T> entityClass;

    protected AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T create(T entity) throws DatabaseException {

        EntityManager em = EMF.get().createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(entity);

            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException("Could not create " + entityClass.getSimpleName(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public T update(T entity) throws DatabaseException {

        EntityManager em = EMF.get().createEntityManager();

        try {
            em.getTransaction().begin();

            T updated = em.merge(entity);

            em.getTransaction().commit();
            return updated;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new DatabaseException("Could not update " + entityClass.getSimpleName(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<T> getById(ID id) {

        try (EntityManager em = EMF.get().createEntityManager()) {
            return Optional.ofNullable(em.find(entityClass, id));
        }
    }

    @Override
    public List<T> getAll() {

        try (EntityManager em = EMF.get().createEntityManager()) {

            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        }
    }

    @Override
    public boolean delete(T entity) throws DatabaseException{

        if (entity == null) {
            return false;
        }

        EntityManager em = EMF.get().createEntityManager();

        try {
            em.getTransaction().begin();

            T managed = em.contains(entity) ? entity : em.merge(entity);
            em.remove(managed);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteById(ID id) throws DatabaseException{

        EntityManager em = EMF.get().createEntityManager();

        try {
            em.getTransaction().begin();

            T found = em.find(entityClass, id);

            if (found == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(found);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteAll() throws DatabaseException {

        EntityManager em = EMF.get().createEntityManager();

        try {
            em.getTransaction().begin();

            String jpql = "DELETE FROM " + entityClass.getSimpleName() + " e";
            em.createQuery(jpql).executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException("Could not deleteAll " + entityClass.getSimpleName(), e);
        } finally {
            em.close();
        }
    }
}