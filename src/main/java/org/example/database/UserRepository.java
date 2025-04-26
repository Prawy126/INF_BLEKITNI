package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Employee;

import java.util.List;

public class UserRepository {

    private final EntityManagerFactory emf;

    public UserRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

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

    public void dodajPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(pracownik);
            tx.commit();
        } finally {
            if(tx.isActive()) tx.rollback();
            em.close();
        }
    }

    // Inne metody CRUD...
}