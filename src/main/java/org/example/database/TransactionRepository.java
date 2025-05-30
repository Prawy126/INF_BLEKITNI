/*
 * Classname: TransactionRepository
 * Version information: 2.0
 * Date: 2025-05-30
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TemporalType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Product;
import org.example.sys.Transaction;
import org.example.sys.TransactionProduct;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TransactionRepository {
    private static final Logger logger = LogManager.getLogger(TransactionRepository.class);

    public TransactionRepository() {
        // już nie tworzymy EMF tutaj
        logger.info("Używam wspólnego EMF z EMFProvider");
    }

    public void addTransaction(Transaction transaction) {
        logger.debug("addTransaction() – start, transaction={}", transaction);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transaction);
            tx.commit();
            logger.info("addTransaction() – transaction dodana: {}", transaction);
        } catch (Exception e) {
            logger.error("addTransaction() – błąd podczas dodawania transakcji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addTransaction() – EntityManager zamknięty");
        }
    }

    public Transaction findTransactionById(int id) {
        logger.debug("findTransactionById() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Transaction t = em.find(Transaction.class, id);
            logger.info("findTransactionById() – znaleziono: {}", t);
            return t;
        } catch (Exception e) {
            logger.error("findTransactionById() – błąd podczas pobierania id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findTransactionById() – EntityManager zamknięty");
        }
    }

    public List<Transaction> getAllTransactions() {
        logger.debug("getAllTransactions() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Transaction> list = em.createQuery("SELECT t FROM Transaction t", Transaction.class)
                    .getResultList();
            logger.info("getAllTransactions() – pobrano {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllTransactions() – błąd podczas pobierania transakcji", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllTransactions() – EntityManager zamknięty");
        }
    }

    public void removeTransactions(int id) {
        logger.debug("removeTransactions() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction t = em.find(Transaction.class, id);
            if (t != null) {
                em.remove(t);
                logger.info("removeTransactions() – usunięto transakcję: {}", t);
            } else {
                logger.warn("removeTransactions() – brak transakcji o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeTransactions() – błąd podczas usuwania id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeTransactions() – EntityManager zamknięty");
        }
    }

    public void updateTransaction(Transaction transaction) {
        logger.debug("updateTransaction() – start, transaction={}", transaction);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(transaction);
            tx.commit();
            logger.info("updateTransaction() – transaction zaktualizowana: {}", transaction);
        } catch (Exception e) {
            logger.error("updateTransaction() – błąd podczas aktualizacji transakcji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateTransaction() – EntityManager zamknięty");
        }
    }

    // =============== GŁÓWNE ZMIANY ===============
    public int getSoldQuantityForProductOnDate(Product product, LocalDate date) {
        logger.debug("getSoldQuantityForProductOnDate() – start, product={}, date={}", product, date);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Date sqlDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Long total = em.createQuery(
                            "SELECT COALESCE(SUM(tp.quantity), 0) " +
                                    "FROM TransactionProduct tp " +
                                    "JOIN tp.transaction t " +
                                    "WHERE tp.product = :product AND t.date = :date", Long.class)
                    .setParameter("product", product)
                    .setParameter("date", sqlDate, TemporalType.DATE)
                    .getSingleResult();
            int result = total.intValue();
            logger.info("getSoldQuantityForProductOnDate() – sprzedano {} sztuk produktu {} w dniu {}",
                    result, product, date);
            return result;
        } catch (Exception e) {
            logger.error("getSoldQuantityForProductOnDate() – błąd liczenia sprzedaży produktu {} w dniu {}",
                    product, date, e);
            return 0;
        } finally {
            em.close();
            logger.debug("getSoldQuantityForProductOnDate() – EntityManager zamknięty");
        }
    }

    public List<Transaction> getTransactionsBetweenDates(Date startDate, Date endDate) {
        logger.debug("getTransactionsBetweenDates() – start, from={}, to={}", startDate, endDate);
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
            logger.info("getTransactionsBetweenDates() – znaleziono {} transakcji", result.size());
            return result;
        } catch (Exception e) {
            logger.error("getTransactionsBetweenDates() – błąd pobierania transakcji między {} a {}",
                    startDate, endDate, e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getTransactionsBetweenDates() – EntityManager zamknięty");
        }
    }

    public void addProductToTransaction(Transaction transaction, Product product, int quantity) {
        logger.debug("addProductToTransaction() – start, transactionId={}, productId={}, quantity={}",
                transaction.getId(), product.getId(), quantity);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction managedTx = em.find(Transaction.class, transaction.getId());
            Product    managedPr = em.find(Product.class, product.getId());
            if (managedTx != null && managedPr != null) {
                String jpql = "SELECT tp FROM TransactionProduct tp " +
                        "WHERE tp.transaction.id = :txId AND tp.product.id = :prodId";
                List<TransactionProduct> existing = em.createQuery(jpql, TransactionProduct.class)
                        .setParameter("txId", managedTx.getId())
                        .setParameter("prodId", managedPr.getId())
                        .getResultList();
                if (existing.isEmpty()) {
                    TransactionProduct tp = new TransactionProduct();
                    tp.setTransaction(managedTx);
                    tp.setProduct(managedPr);
                    tp.setQuantity(quantity);
                    em.persist(tp);
                    logger.info("addProductToTransaction() – utworzono relację (tx={}, prod={}, qty={})",
                            managedTx.getId(), managedPr.getId(), quantity);
                } else {
                    TransactionProduct tp = existing.get(0);
                    tp.setQuantity(quantity);
                    em.merge(tp);
                    logger.info("addProductToTransaction() – zaktualizowano qty w relacji (tx={}, prod={}, qty={})",
                            managedTx.getId(), managedPr.getId(), quantity);
                }
            } else {
                logger.warn("addProductToTransaction() – nie znaleziono transakcji ({}) lub produktu ({})",
                        transaction.getId(), product.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("addProductToTransaction() – błąd podczas operacji relacji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addProductToTransaction() – EntityManager zamknięty");
        }
    }

    public List<Transaction> findByEmployee(int employeeId) {
        logger.debug("findByEmployee() – employeeId={}", employeeId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.employee.id = :pid", Transaction.class)
                    .setParameter("pid", employeeId)
                    .getResultList();
            logger.info("findByEmployee() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByEmployee() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByEmployee() – EntityManager zamknięty");
        }
    }

    public List<Transaction> findByDate(Date date) {
        logger.debug("findByDate() – date={}", date);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.date = :date", Transaction.class)
                    .setParameter("date", date, TemporalType.DATE)
                    .getResultList();
            logger.info("findByDate() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByDate() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByDate() – EntityManager zamknięty");
        }
    }

    public List<Transaction> findByDateRange(Date fromDate, Date toDate) {
        logger.debug("findByDateRange() – from={}, to={}", fromDate, toDate);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.date BETWEEN :from AND :to", Transaction.class)
                    .setParameter("from", fromDate, TemporalType.DATE)
                    .setParameter("to",   toDate,   TemporalType.DATE)
                    .getResultList();
            logger.info("findByDateRange() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByDateRange() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByDateRange() – EntityManager zamknięty");
        }
    }

    public Session getSession() {
        logger.debug("getSession() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Session session = em.unwrap(Session.class);
            logger.info("getSession() – uzyskano sesję Hibernate: {}", session);
            return session;
        } finally {
            // Uwaga: tu nie zamykamy EM, bo sesja może go potrzebować.
        }
    }
}
