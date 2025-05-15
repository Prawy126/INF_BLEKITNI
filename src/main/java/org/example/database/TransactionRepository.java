package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Transaction;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TransactionRepository {

    private final EntityManagerFactory emf;

    public TransactionRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajTransakcje(Transaction transakcja) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transakcja);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public List<Transaction> getTransactionsByDate(LocalDate date) {
        EntityManager em = emf.createEntityManager();
        try {
            // Konwersja LocalDate na Date (bez godziny - początek dnia i koniec dnia)
            Date startDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

            return em.createQuery("SELECT t FROM Transaction t WHERE t.data >= :startDate AND t.data < :endDate", Transaction.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Transaction znajdzTransakcjePoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Transaction.class, id);
        } finally {
            em.close();
        }
    }

    public List<Transaction> pobierzWszystkieTransakcje() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void usunTransakcje(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction transakcja = em.find(Transaction.class, id);
            if (transakcja != null) {
                em.remove(transakcja);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void aktualizujTransakcje(Transaction transakcja) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(transakcja);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
