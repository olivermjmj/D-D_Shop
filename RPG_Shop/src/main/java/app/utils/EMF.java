package app.utils;

import app.config.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;

public class EMF {

    public static EntityManagerFactory get() {

        return HibernateConfig.getEntityManagerFactory();
    }

    public static EntityManagerFactory getTestEmf() {

        return HibernateConfig.getEntityManagerFactoryForTest();
    }

    public static void close() {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        if (emf != null && emf.isOpen()) {
            emf.close();
        }

        EntityManagerFactory testEmf = HibernateConfig.getEntityManagerFactoryForTest();

        if (testEmf != null && testEmf.isOpen()) {
            testEmf.close();
        }
    }
}