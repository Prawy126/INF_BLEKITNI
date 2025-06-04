/*
 * Classname: EMFProvider
 * Version information: 1.0
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Dostawca fabryki EntityManagerFactory dla całej aplikacji.
 * Implementuje wzorzec Singleton zapewniający jeden współdzielony
 * obiekt EntityManagerFactory dla wszystkich repozytoriów.
 */
public class EMFProvider {

    /**
     * Współdzielona instancja EntityManagerFactory.
     * Inicjalizowana przy pierwszym załadowaniu klasy.
     * Używa jednostki trwałości "myPU" zdefiniowanej w persistence.xml.
     */
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("myPU");

    /**
     * Zwraca instancję EntityManagerFactory.
     * Metoda umożliwia dostęp do współdzielonej fabryki
     * w całej aplikacji.
     *
     * @return współdzielona instancja EntityManagerFactory
     */
    public static EntityManagerFactory get() {
        return EMF;
    }

    /**
     * Zamyka EntityManagerFactory.
     * Metoda powinna być wywołana przy zamykaniu aplikacji,
     * aby poprawnie zwolnić zasoby.
     */
    public static void close() {
        EMF.close();
    }
}