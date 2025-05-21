/*
 * Classname: TransactionRepository
 * Version information: 1.1
 * Date: 2025-05-21
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TemporalType;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import org.example.sys.Product;
import org.example.sys.Transaction;
import org.example.sys.TransactionProduct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TransactionRepository {
    private static final Logger logger = LogManager.getLogger(TransactionRepository.class);
    private final EntityManagerFactory emf;

    public TransactionRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono TransactionRepository, EMF={}", emf);
    }

    /** Dodaje nową transakcję. */
    public void dodajTransakcje(Transaction transakcja) {
        logger.debug("dodajTransakcje() – start, transakcja={}", transakcja);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transakcja);
            tx.commit();
            logger.info("dodajTransakcje() – transakcja dodana: {}", transakcja);
        } catch (Exception e) {
            logger.error("dodajTransakcje() – błąd podczas dodawania transakcji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajTransakcje() – EntityManager zamknięty");
        }
    }

    /** Pobiera transakcję po jej ID. */
    public Transaction znajdzTransakcjePoId(int id) {
        logger.debug("znajdzTransakcjePoId() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Transaction t = em.find(Transaction.class, id);
            logger.info("znajdzTransakcjePoId() – znaleziono: {}", t);
            return t;
        } catch (Exception e) {
            logger.error("znajdzTransakcjePoId() – błąd podczas pobierania id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzTransakcjePoId() – EntityManager zamknięty");
        }
    }

    /** Pobiera wszystkie transakcje. */
    public List<Transaction> pobierzWszystkieTransakcje() {
        logger.debug("pobierzWszystkieTransakcje() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery("SELECT t FROM Transaction t", Transaction.class)
                    .getResultList();
            logger.info("pobierzWszystkieTransakcje() – pobrano {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkieTransakcje() – błąd podczas pobierania transakcji", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieTransakcje() – EntityManager zamknięty");
        }
    }

    /** Usuwa transakcję o danym ID. */
    public void usunTransakcje(int id) {
        logger.debug("usunTransakcje() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction t = em.find(Transaction.class, id);
            if (t != null) {
                em.remove(t);
                logger.info("usunTransakcje() – usunięto transakcję: {}", t);
            } else {
                logger.warn("usunTransakcje() – brak transakcji o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunTransakcje() – błąd podczas usuwania id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunTransakcje() – EntityManager zamknięty");
        }
    }

    /** Aktualizuje istniejącą transakcję. */
    public void aktualizujTransakcje(Transaction transakcja) {
        logger.debug("aktualizujTransakcje() – start, transakcja={}", transakcja);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(transakcja);
            tx.commit();
            logger.info("aktualizujTransakcje() – transakcja zaktualizowana: {}", transakcja);
        } catch (Exception e) {
            logger.error("aktualizujTransakcje() – błąd podczas aktualizacji transakcji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujTransakcje() – EntityManager zamknięty");
        }
    }

    // =========================================================
    // === Dodatkowe metody wyszukiwania po różnych kryteriach ===
    // =========================================================

    /**
     * Zwraca liczbę sztuk danego produktu sprzedanych w podanym dniu
     * (liczymy na podstawie tabeli Zamowienia).
     *
     * @param produkt encja produktu (org.example.sys.Product)
     * @param date    dzień, dla którego liczymy sprzedaż (LocalDate bez czasu)
     * @return        liczba sprzedanych sztuk (0 – gdy brak rekordów)
     */
    public int getSoldQuantityForProductOnDate(Product produkt, LocalDate date) {
        logger.debug("getSoldQuantityForProductOnDate() – start, produkt={}, date={}", produkt, date);
        EntityManager em = emf.createEntityManager();
        try {
            Date sqlDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Long total = em.createQuery(
                            "SELECT COALESCE(SUM(z.ilosc), 0) " +
                                    "FROM Zamowienie z " +
                                    "WHERE z.produkt = :produkt " +
                                    "  AND z.data      = :data", Long.class)
                    .setParameter("produkt", produkt)
                    .setParameter("data", sqlDate)
                    .getSingleResult();

            int result = total.intValue();
            logger.info("getSoldQuantityForProductOnDate() – sprzedano {} sztuk produktu {} w dniu {}", result, produkt, date);
            return result;
        } catch (Exception e) {
            logger.error("getSoldQuantityForProductOnDate() – błąd podczas liczenia sprzedaży produktu {} w dniu {}", produkt, date, e);
            return 0;
        } finally {
            em.close();
            logger.debug("getSoldQuantityForProductOnDate() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera transakcje razem z kolekcją produktów, między dwiema datami.
     * Uwzględnia nową strukturę relacji Transaction -> TransactionProduct -> Product
     */
    public List<Transaction> getTransactionsBetweenDates(Date startDate, Date endDate) {
        logger.debug("getTransactionsBetweenDates() – start, from={}, to={}", startDate, endDate);
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
            Root<Transaction> root = cq.from(Transaction.class);
            root.fetch("transactionProducts", JoinType.LEFT);
            cq.select(root)
                    .where(cb.between(root.get("data"), startDate, endDate))
                    .orderBy(cb.asc(root.get("data")));

            List<Transaction> result = em.createQuery(cq).getResultList();
            logger.info("getTransactionsBetweenDates() – znaleziono {} transakcji", result.size());
            return result;
        } catch (Exception e) {
            logger.error("getTransactionsBetweenDates() – błąd podczas pobierania transakcji między {} a {}", startDate, endDate, e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getTransactionsBetweenDates() – EntityManager zamknięty");
        }
    }

    /**
     * Dodaje produkt do transakcji z określoną ilością
     */
    public void dodajProduktDoTransakcji(Transaction transaction, Product product, int quantity) {
        logger.debug("dodajProduktDoTransakcji() – start, transactionId={}, productId={}, quantity={}",
                transaction.getId(), product.getId(), quantity);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Transaction managedTransaction = em.find(Transaction.class, transaction.getId());
            Product     managedProduct     = em.find(Product.class, product.getId());

            if (managedTransaction != null && managedProduct != null) {
                String jpql = "SELECT tp FROM TransactionProduct tp " +
                        "WHERE tp.transaction.id = :transactionId " +
                        "  AND tp.product.id     = :productId";
                List<TransactionProduct> existing = em.createQuery(jpql, TransactionProduct.class)
                        .setParameter("transactionId", managedTransaction.getId())
                        .setParameter("productId",     managedProduct.getId())
                        .getResultList();

                if (existing.isEmpty()) {
                    TransactionProduct tp = new TransactionProduct();
                    tp.setTransaction(managedTransaction);
                    tp.setProduct(managedProduct);
                    tp.setQuantity(quantity);
                    em.persist(tp);
                    logger.info("dodajProduktDoTransakcji() – utworzono relację dla transakcji {} i produktu {} ilość={}",
                            managedTransaction.getId(), managedProduct.getId(), quantity);
                } else {
                    TransactionProduct tp = existing.get(0);
                    tp.setQuantity(quantity);
                    em.merge(tp);
                    logger.info("dodajProduktDoTransakcji() – zaktualizowano ilość w relacji transakcja {} produkt {} na {}",
                            managedTransaction.getId(), managedProduct.getId(), quantity);
                }
            } else {
                logger.warn("dodajProduktDoTransakcji() – nie znaleziono transakcji lub produktu (tx={}, prod={})",
                        transaction.getId(), product.getId());
            }

            tx.commit();
        } catch (Exception e) {
            logger.error("dodajProduktDoTransakcji() – błąd podczas dodawania/aktualizacji relacji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajProduktDoTransakcji() – EntityManager zamknięty");
        }
    }


    /**
     * Znajduje wszystkie transakcje wykonane przez danego pracownika.
     */
    public List<Transaction> znajdzPoPracowniku(int pracownikId) {
        logger.debug("znajdzPoPracowniku() – pracownikId={}", pracownikId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.pracownik.id = :pid",
                            Transaction.class)
                    .setParameter("pid", pracownikId)
                    .getResultList();
            logger.info("znajdzPoPracowniku() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoPracowniku() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoPracowniku() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje transakcje dokonane dokładnie w danym dniu.
     */
    public List<Transaction> znajdzPoDacie(Date data) {
        logger.debug("znajdzPoDacie() – data={}", data);
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.data = :data",
                            Transaction.class)
                    .setParameter("data", data, TemporalType.DATE)
                    .getResultList();
            logger.info("znajdzPoDacie() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoDacie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoDacie() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje transakcje z przedziału dat [fromDate, toDate].
     */
    public List<Transaction> znajdzPoZakresieDat(Date fromDate, Date toDate) {
        logger.debug("znajdzPoZakresieDat() – from={}, to={}", fromDate, toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.data BETWEEN :fromDate AND :toDate",
                            Transaction.class)
                    .setParameter("fromDate", fromDate, TemporalType.DATE)
                    .setParameter("toDate", toDate,   TemporalType.DATE)
                    .getResultList();
            logger.info("znajdzPoZakresieDat() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoZakresieDat() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoZakresieDat() – EntityManager zamknięty");
        }
    }


    /**
     * Zwraca sesję Hibernate poprzez EntityManager
     */
    public Session getSession() {
        logger.debug("getSession() – start");
        EntityManager em = emf.createEntityManager();
        Session session = em.unwrap(Session.class);
        logger.info("getSession() – uzyskano sesję Hibernate: {}", session);
        return session;
    }

    /** Zamyka fabrykę EntityManagerFactory. */
    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}