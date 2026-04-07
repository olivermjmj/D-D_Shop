package app.utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


//Lombok getter won't work, since it cannot touch static fields

public class EMF {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("poems");

    public static EntityManagerFactory get() {
        return emf;
    }

    public static void close() {

        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}