package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Employee;

import java.util.List;

public class UserRepository {

    private final EntityManagerFactory emf;

    public UserRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public List<Employee> pobierzWszystkichPracownikow() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Employee e", Employee.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Zwraca listę wszystkich kasjerów. */
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

    /** Wyszukuje pracownika po loginie (unikalnym). */
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

    /** Dodaje nowego pracownika. */
    public void dodajPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(pracownik);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    /**
     * Aktualizuje dane istniejącego pracownika.
     * Przyjmuje encję (np. już zmodyfikowaną w warstwie serwisu lub DTO-maperze).
     * Jeśli obiekt pochodzi spoza bieżącego kontekstu PU, użycie merge()
     * sprawi, że zostanie do niego włączony i nadpisze stan w bazie.
     */
    public void aktualizujPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(pracownik);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    /**
     * Usuwa pracownika na podstawie identyfikatora.
     * Pobiera encję przy pomocy find(), aby móc użyć remove().
     */

    //TODO: Przemyśleć problem z więzami integralności, jak usunie się pracownika to jego raporty powinny zostać, a narazie jest naruszenie więzów integralności
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
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public Employee znajdzPoLoginieIHasle(String login, String haslo) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login AND e.haslo = :haslo",
                            Employee.class
                    )
                    .setParameter("login", login)
                    .setParameter("haslo", haslo)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // nie znaleziono użytkownika
        } finally {
            em.close();
        }
    }


    /** Opcjonalnie: metoda do zamknięcia fabryki przy zamykaniu aplikacji. */
    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
