package org.example.database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EMFProvider {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("myPU");
    public static EntityManagerFactory get() { return EMF; }
    public static void close() { EMF.close(); }
}

