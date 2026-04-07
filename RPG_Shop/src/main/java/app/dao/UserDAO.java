package app.dao;

import app.entities.User;
import app.entities.enums.Role;
import app.utils.EMF;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class UserDAO extends AbstractDAO<User, Integer> {


    public UserDAO() {
        super(User.class);
    }

    public long countByRole(Role role) {

        try(EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT COUNT(u) " +
                                     "FROM User u " +
                                     "WHERE u.role = :role", Long.class).setParameter("role", role).getSingleResult();
        }
    }

    public List<User> getAllByRole(Role role) {

        try(EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT u " +
                                     "FROM User u " +
                                     "WHERE u.role = :role", User.class).setParameter("role", role).getResultList();
        }
    }

    public boolean existsByUsername(String username) {

        try(EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT COUNT(u) " +
                                     "FROM User u " +
                                     "WHERE u.username = :username", Long.class).setParameter("username", username).getSingleResult() > 0;
        }
    }

    public boolean existsByEmail(String email) {

        try(EntityManager em = EMF.get().createEntityManager()) {

            return em.createQuery("SELECT COUNT(u) " +
                                     "FROM User u " +
                                     "WHERE u.email = :email", Long.class).setParameter("email", email).getSingleResult() > 0;
        }
    }

    public Optional<User> getByUsername(String username) {

        try(EntityManager em = EMF.get().createEntityManager()) {

            List<User> users = em.createQuery("SELECT u " +
                                                 "FROM User u " +
                                                 "WHERE u.username = :username", User.class).setParameter("username", username).getResultList();

            return users.stream().findFirst();
        }
    }

    public Optional<User> getByEmail(String email) {

        try(EntityManager em = EMF.get().createEntityManager()) {

            List<User> users = em.createQuery("SELECT u " +
                                                 "FROM User u " +
                                                 "WHERE u.email = :email", User.class).setParameter("email", email).getResultList();

            return users.stream().findFirst();
        }
    }
}