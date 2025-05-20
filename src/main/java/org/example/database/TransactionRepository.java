/*
 * Classname: TransactionRepository
 * Version information: 1.0
 * Date: 2025-05-20
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.example.sys.Product;
import org.example.sys.Transaction;
import org.example.sys.TransactionProduct;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TransactionRepository {

    private final EntityManagerFactory emf;

    public TransactionRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajTransakcje(Transaction transaction) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transaction);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
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
     * Pobiera transakcje razem z kolekcją produktów, między dwiema datami.
     * Uwzględnia nową strukturę relacji Transaction -> TransactionProduct -> Product
     */
    public List<Transaction> getTransactionsBetweenDates(Date startDate, Date endDate) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
            Root<Transaction> root = cq.from(Transaction.class);

            // Fetch transactionProducts zamiast produkty
            root.fetch("transactionProducts", JoinType.LEFT);

            cq.select(root)
                    .where(cb.between(root.get("data"), startDate, endDate))
                    .orderBy(cb.asc(root.get("data")));

            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Dodaje produkt do transakcji z określoną ilością
     */
    public void dodajProduktDoTransakcji(Transaction transaction, Product product, int quantity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Pobierz zarządzane wersje obiektów
            Transaction managedTransaction = em.find(Transaction.class, transaction.getId());
            Product managedProduct = em.find(Product.class, product.getId());

            if (managedTransaction != null && managedProduct != null) {
                // Sprawdź, czy relacja już istnieje
                String jpql = "SELECT tp FROM TransactionProduct tp " +
                        "WHERE tp.transaction.id = :transactionId " +
                        "AND tp.product.id = :productId";

                List<TransactionProduct> existing = em.createQuery(jpql, TransactionProduct.class)
                        .setParameter("transactionId", managedTransaction.getId())
                        .setParameter("productId", managedProduct.getId())
                        .getResultList();

                if (existing.isEmpty()) {
                    // Utwórz nową relację
                    TransactionProduct tp = new TransactionProduct();
                    tp.setTransaction(managedTransaction);
                    tp.setProduct(managedProduct);
                    tp.setQuantity(quantity);
                    em.persist(tp);
                } else {
                    // Aktualizuj istniejącą relację
                    TransactionProduct tp = existing.get(0);
                    tp.setQuantity(quantity);
                    em.merge(tp);
                }
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Zwraca sesję Hibernate poprzez EntityManager
     */
    public Session getSession() {
        EntityManager em = emf.createEntityManager();
        return em.unwrap(Session.class);
    }

    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}