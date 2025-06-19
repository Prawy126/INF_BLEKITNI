/*
 * Classname: TransactionRepository
 * Version information: 2.0
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TemporalType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.EMFProvider;
import org.example.sys.Product;
import org.example.sys.Transaction;
import org.example.sys.TransactionProduct;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Repozytorium do zarządzania transakcjami w systemie.
 * Zapewnia operacje CRUD oraz metody wyszukiwania transakcji
 * według różnych kryteriów. Wykorzystuje EntityManager
 * do komunikacji z bazą danych.
 */
public class TransactionRepository {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą
     * TransactionRepository.
     */
    private static final Logger logger = LogManager.getLogger(
            TransactionRepository.class);

    /**
     * Konstruktor domyślny – korzysta ze wspólnego EMF z EMFProvider.
     * Nie tworzy nowej fabryki EntityManager.
     */
    public TransactionRepository() {
        // już nie tworzymy EMF tutaj
        logger.info("Używam wspólnego EMF z EMFProvider");
    }

    /**
     * Dodaje nową transakcję do bazy.
     * Operacja jest wykonywana w transakcji bazodanowej.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param transaction obiekt Transaction do zapisania
     */
    public void addTransaction(Transaction transaction) {
        logger.debug("addTransaction() – start, transaction={}",
                transaction);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transaction);
            tx.commit();
            logger.info("addTransaction() – transaction dodana: {}",
                    transaction);
        } catch (Exception e) {
            logger.error("addTransaction() " +
                    "– błąd podczas dodawania transakcji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addTransaction() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje transakcję o podanym identyfikatorze.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest wartość null.
     *
     * @param id identyfikator transakcji
     * @return obiekt Transaction lub null, jeśli nie istnieje
     */
    public Transaction findTransactionById(int id) {
        logger.debug("findTransactionById() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Transaction t = em.find(Transaction.class, id);
            logger.info("findTransactionById() – znaleziono: {}", t);
            return t;
        } catch (Exception e) {
            logger.error("findTransactionById() " +
                    "– błąd podczas pobierania id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findTransactionById() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera listę wszystkich transakcji.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @return lista wszystkich transakcji lub pusta lista w przypadku błędu
     */
    public List<Transaction> getAllTransactions() {
        logger.debug("getAllTransactions() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t",
                            Transaction.class)
                    .getResultList();
            logger.info("getAllTransactions() " +
                    "– pobrano {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllTransactions() " +
                    "– błąd podczas pobierania transakcji", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllTransactions() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa transakcję o podanym identyfikatorze.
     * Operacja jest wykonywana w transakcji bazodanowej.
     * Jeśli transakcja nie istnieje, operacja jest logowana jako ostrzeżenie.
     *
     * @param id identyfikator transakcji do usunięcia
     */
    public void removeTransactions(int id) {
        logger.debug("removeTransactions() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction t = em.find(Transaction.class, id);
            if (t != null) {
                em.remove(t);
                logger.info("removeTransactions() " +
                        "– usunięto transakcję: {}", t);
            } else {
                logger.warn("removeTransactions() " +
                        "– brak transakcji o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeTransactions() " +
                    "– błąd podczas usuwania id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeTransactions() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje istniejącą transakcję.
     * Operacja jest wykonywana w transakcji bazodanowej.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param transaction zaktualizowany obiekt Transaction
     */
    public void updateTransaction(Transaction transaction) {
        logger.debug("updateTransaction() " +
                "– start, transaction={}", transaction);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(transaction);
            tx.commit();
            logger.info("updateTransaction() " +
                            "– transaction zaktualizowana: {}",
                    transaction);
        } catch (Exception e) {
            logger.error("updateTransaction() " +
                            "– błąd podczas aktualizacji transakcji",
                    e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateTransaction() – EntityManager zamknięty");
        }
    }

    /**
     * Oblicza ilość sprzedanych sztuk produktu w określonym dniu.
     * Wykorzystuje zapytanie agregujące SUM.
     *
     * @param product produkt, dla którego obliczana jest sprzedaż
     * @param date data, dla której obliczana jest sprzedaż
     * @return liczba sprzedanych sztuk produktu w danym dniu
     */
    public int getSoldQuantityForProductOnDate(
            Product product,
            LocalDate date
    ) {
        logger.debug("getSoldQuantityForProductOnDate() " +
                        "– start, product={}, date={}",
                product, date);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Date sqlDate = Date.from(date.atStartOfDay(
                    ZoneId.systemDefault()).toInstant());
            Long total = em.createQuery(
                            "SELECT COALESCE(SUM(tp.quantity), 0) " +
                                    "FROM TransactionProduct tp " +
                                    "JOIN tp.transaction t " +
                                    "WHERE tp.product = :product " +
                                    "AND t.date = :date",
                            Long.class)
                    .setParameter("product", product)
                    .setParameter("date", sqlDate, TemporalType.DATE)
                    .getSingleResult();
            int result = total.intValue();
            logger.info("getSoldQuantityForProductOnDate() " +
                    "– sprzedano {} sztuk "
                    + "produktu {} w dniu {}", result, product, date);
            return result;
        } catch (Exception e) {
            logger.error("getSoldQuantityForProductOnDate() " +
                    "– błąd liczenia sprzedaży "
                    + "produktu {} w dniu {}", product, date, e);
            return 0;
        } finally {
            em.close();
            logger.debug("getSoldQuantityForProductOnDate() " +
                    "– EntityManager zamknięty");
        }
    }

    /**
     * Pobiera transakcje z podanego zakresu dat.
     * Używa Criteria API z ładowaniem zachłannym produktów transakcji.
     *
     * @param startDate początek zakresu dat (włącznie)
     * @param endDate koniec zakresu dat (włącznie)
     * @return lista transakcji z podanego zakresu dat
     */
    public List<Transaction> getTransactionsBetweenDates(
            Date startDate,
            Date endDate
    ) {
        logger.debug("getTransactionsBetweenDates() " +
                        "– start, from={}, to={}",
                startDate, endDate);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
            Root<Transaction> root = cq.from(Transaction.class);
            root.fetch("transactionProducts", JoinType.LEFT);

            cq.select(root)
                    .where(cb.between(root.get("date"), startDate, endDate))
                    .orderBy(cb.asc(root.get("date")));

            List<Transaction> result = em.createQuery(cq).getResultList();
            logger.info("getTransactionsBetweenDates() " +
                            "– znaleziono {} transakcji",
                    result.size());
            return result;
        } catch (Exception e) {
            logger.error("getTransactionsBetweenDates() " +
                    "– błąd pobierania transakcji "
                    + "między {} a {}", startDate, endDate, e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getTransactionsBetweenDates() " +
                    "– EntityManager zamknięty");
        }
    }

    /**
     * Dodaje produkt do transakcji lub aktualizuje ilość,
     * jeśli już istnieje.
     * Operacja jest wykonywana w transakcji bazodanowej.
     *
     * @param transaction transakcja, do której dodawany jest produkt
     * @param product produkt dodawany do transakcji
     * @param quantity ilość produktu
     */
    public void addProductToTransaction(
            Transaction transaction,
            Product product,
            int quantity
    ) {
        logger.debug("addProductToTransaction() " +
                "– start, transactionId={}, productId={}, "
                + "quantity={}", transaction.getId(),
                product.getId(), quantity);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction managedTx
                    = em.find(Transaction.class, transaction.getId());
            Product managedPr = em.find(Product.class, product.getId());
            if (managedTx != null && managedPr != null) {
                String jpql = "SELECT tp FROM TransactionProduct tp " +
                        "WHERE tp.transaction.id " +
                        "= :txId AND tp.product.id = :prodId";
                List<TransactionProduct> existing = em.createQuery(
                                jpql, TransactionProduct.class)
                        .setParameter("txId", managedTx.getId())
                        .setParameter("prodId", managedPr.getId())
                        .getResultList();
                if (existing.isEmpty()) {
                    TransactionProduct tp = new TransactionProduct();
                    tp.setTransaction(managedTx);
                    tp.setProduct(managedPr);
                    tp.setQuantity(quantity);
                    em.persist(tp);
                    logger.info("addProductToTransaction() " +
                                    "– utworzono relację "
                                    + "(tx={}, prod={}, qty={})",
                            managedTx.getId(), managedPr.getId(), quantity);
                } else {
                    TransactionProduct tp = existing.get(0);
                    tp.setQuantity(quantity);
                    em.merge(tp);
                    logger.info("addProductToTransaction() " +
                                    "– zaktualizowano qty w relacji "
                                    + "(tx={}, prod={}, qty={})",
                            managedTx.getId(), managedPr.getId(), quantity);
                }
            } else {
                logger.warn("addProductToTransaction() " +
                                "– nie znaleziono transakcji "
                                + "({}) lub produktu ({})",
                        transaction.getId(), product.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("addProductToTransaction() " +
                    "– błąd podczas operacji relacji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addProductToTransaction() " +
                    "– EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje transakcje powiązane z pracownikiem o podanym ID.
     * W przypadku błędu, wyjątek jest logowany i zwracana
     * jest pusta lista.
     *
     * @param employeeId identyfikator pracownika
     * @return lista transakcji danego pracownika
     */
    public List<Transaction> findByEmployee(int employeeId) {
        logger.debug("findByEmployee() – employeeId={}", employeeId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t " +
                                    "WHERE t.employee.id = :pid",
                            Transaction.class)
                    .setParameter("pid", employeeId)
                    .getResultList();
            logger.info("findByEmployee() " +
                    "– znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByEmployee() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByEmployee() – EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje transakcje z określoną datą.
     * Wykorzystuje TemporalType.DATE do porównania samej daty.
     *
     * @param date data transakcji
     * @return lista transakcji z podaną datą
     */
    public List<Transaction> findByDate(Date date) {
        logger.debug("findByDate() – date={}", date);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t " +
                                    "WHERE t.date = :date",
                            Transaction.class)
                    .setParameter("date", date, TemporalType.DATE)
                    .getResultList();
            logger.info("findByDate() " +
                    "– znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByDate() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByDate() – EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje transakcje z zakresu dat.
     * Wykorzystuje TemporalType.DATE do porównania samych dat.
     *
     * @param fromDate początek zakresu dat (włącznie)
     * @param toDate koniec zakresu dat (włącznie)
     * @return lista transakcji z podanego zakresu dat
     */
    public List<Transaction> findByDateRange(Date fromDate, Date toDate) {
        logger.debug("findByDateRange() " +
                "– from={}, to={}", fromDate, toDate);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t " +
                                    "WHERE t.date BETWEEN :from AND :to",
                            Transaction.class)
                    .setParameter("from", fromDate, TemporalType.DATE)
                    .setParameter("to", toDate, TemporalType.DATE)
                    .getResultList();
            logger.info("findByDateRange() " +
                    "– znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByDateRange() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByDateRange() – EntityManager zamknięty");
        }
    }

    /**
     * Uzyskuje sesję Hibernate z EntityManager.
     * Uwaga: EntityManager nie jest zamykany,
     * ponieważ sesja może go potrzebować.
     *
     * @return sesja Hibernate
     */
    public Session getSession() {
        logger.debug("getSession() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Session session = em.unwrap(Session.class);
            logger.info("getSession() " +
                    "– uzyskano sesję Hibernate: {}", session);
            return session;
        } finally {
            // Uwaga: tu nie zamykamy EM, bo sesja może go potrzebować.
        }
    }

    /**
     * Zamyka wspólną fabrykę EMF (na zakończenie działania aplikacji).
     * Implementacja jest pusta, ponieważ korzystamy z EMFProvider.
     */
    public void close() {
    }
}