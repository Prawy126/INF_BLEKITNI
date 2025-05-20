/*
 * Classname: TransactionRepository
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import com.mysql.cj.Session;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.example.sys.PeriodType;
import org.example.sys.Product;
import org.example.sys.Transaction;
import pdf.SalesReportGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static pdf.SalesReportGenerator.PeriodType.DAILY;

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

    /**
     * Zwraca liczbę sztuk danego produktu sprzedanych w podanym dniu
     * (liczymy na podstawie tabeli Zamowienia).
     *
     * @param produkt encja produktu (org.example.sys.Product)
     * @param date    dzień, dla którego liczymy sprzedaż (LocalDate bez czasu)
     * @return        liczba sprzedanych sztuk (0 – gdy brak rekordów)
     */
    public int getSoldQuantityForProductOnDate(Product produkt, LocalDate date) {
        EntityManager em = emf.createEntityManager();
        try {
            // konwersja LocalDate -> java.util.Date (początek dnia w strefie systemowej)
            Date sqlDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Long total = em.createQuery(
                            "SELECT COALESCE(SUM(z.ilosc), 0) " +
                                    "FROM Zamowienie z " +
                                    "WHERE z.produkt = :produkt " +
                                    "  AND z.data      = :data", Long.class)
                    .setParameter("produkt", produkt)
                    .setParameter("data",    sqlDate)
                    .getSingleResult();          // zawsze jedna liczba, nigdy null dzięki COALESCE

            return total.intValue();
        } finally {
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

    /**
     * Fetche transakcje razem z kolekcją produktów, między dwiema datami.
     */
    public List<Transaction> getTransactionsBetweenDates(Date startDate, Date endDate) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
            Root<Transaction> root = cq.from(Transaction.class);
            // fetch produktów, żeby nie było LazyInitializationException
            root.fetch("produkty", JoinType.LEFT);

            cq.select(root)
                    .where(cb.between(root.get("data"), startDate, endDate))
                    .orderBy(cb.asc(root.get("data")));

            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }


    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
