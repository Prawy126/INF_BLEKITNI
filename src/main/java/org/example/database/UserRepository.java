/*
 * Classname: UserRepository
 * Version information: 1.0
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.example.sys.Employee;
import java.util.List;

/**
 * Repozytorium do operacji na pracownikach.
 */
public class UserRepository {

    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory.
     */
    public UserRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    /**
     * Pobiera wszystkich pracowników.
     *
     * @return lista wszystkich pracowników
     */
    public List<Employee> pobierzWszystkichPracownikow() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Employee e", Employee.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Zwraca listę wszystkich kasjerów.
     *
     * @return lista kasjerów
     */
    public List<Employee> pobierzKasjerow() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Employee e WHERE e.stanowisko = 'Kasjer'",
                    Employee.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po loginie.
     *
     * @param login login pracownika
     * @return znaleziony pracownik
     */
    public Employee znajdzPoLoginie(String login) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login",
                            Employee.class
                    ).setParameter("login", login)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Dodaje nowego pracownika.
     *
     * @param pracownik pracownik do dodania
     */
    public void dodajPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(pracownik);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Aktualizuje dane istniejącego pracownika.
     *
     * @param pracownik pracownik do aktualizacji
     */
    public void aktualizujPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(pracownik);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Usuwa pracownika na podstawie identyfikatora.
     *
     * TODO: Rozwiązać problem więzów integralności. Usunięcie pracownika
     * nie powinno naruszać relacji z raportami.
     *
     * @param pracownik pracownik do usunięcia
     */
    public void usunPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee managed = em.find(Employee.class, pracownik.getId());
            if (managed != null) {
                em.remove(managed);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po loginie i haśle.
     *
     * @param login login pracownika
     * @param haslo hasło pracownika
     * @return znaleziony pracownik lub null, jeśli brak
     */
    public Employee znajdzPoLoginieIHasle(String login, String haslo) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login "
                                    + "AND e.password = :haslo",
                            Employee.class
                    ).setParameter("login", login)
                    .setParameter("haslo", haslo)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Zamknięcie EntityManagerFactory.
     */
    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
