/*
 * Classname: TransactionProductRepository
 * Version information: 2.0
 * Date: 2025-05-30
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Product;
import org.example.sys.Transaction;
import org.example.sys.TransactionProduct;
import org.example.sys.TransactionProductId;

import java.util.List;

public class TransactionProductRepository {

    private static final Logger logger = LogManager.getLogger(TransactionProductRepository.class);

    /**
     * Dodaje nową pozycję produktu do transakcji.
     *
     * @param tp obiekt TransactionProduct do zapisania
     */
    public void addTransactionProduct(TransactionProduct tp) {
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // uzyskujemy odwołania do już zarządzanych encji
            Transaction managedTx = em.getReference(
                    Transaction.class, tp.getTransaction().getId()
            );
            Product managedPr = em.getReference(
                    Product.class, tp.getProduct().getId()
            );

            tp.setTransaction(managedTx);
            tp.setProduct(managedPr);
            tp.setId(new TransactionProductId(
                    managedTx.getId(), managedPr.getId()
            ));

            em.persist(tp);
            tx.commit();
            logger.info("addTransactionProduct() – zapisano: {}", tp);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("addTransactionProduct() – błąd podczas zapisu {}", tp, e);
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Znajduje pozycję transakcji po kluczu złożonym (transactionId + productId).
     */
    public TransactionProduct findById(int transactionId, int productId) {
        logger.debug("findById() – start, txId={}, prodId={}", transactionId, productId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            TransactionProductId id = new TransactionProductId(transactionId, productId);
            TransactionProduct tp = em.find(TransactionProduct.class, id);
            logger.info("findById() – znaleziono: {}", tp);
            return tp;
        } catch (Exception e) {
            logger.error("findById() – błąd przy find()", e);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie pozycje produktów dla danej transakcji.
     */
    public List<TransactionProduct> getByTransaction(int transactionId) {
        logger.debug("getByTransaction() – start, txId={}", transactionId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            TypedQuery<TransactionProduct> q = em.createQuery(
                    "SELECT tp FROM TransactionProduct tp WHERE tp.transaction.id = :txId",
                    TransactionProduct.class);
            q.setParameter("txId", transactionId);
            List<TransactionProduct> list = q.getResultList();
            logger.info("getByTransaction() – zwrócono {} pozycji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getByTransaction() – błąd podczas zapytania", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie pozycje transakcji powiązane z danym produktem.
     */
    public List<TransactionProduct> getByProduct(int productId) {
        logger.debug("getByProduct() – start, prodId={}", productId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            TypedQuery<TransactionProduct> q = em.createQuery(
                    "SELECT tp FROM TransactionProduct tp WHERE tp.product.id = :prodId",
                    TransactionProduct.class);
            q.setParameter("prodId", productId);
            List<TransactionProduct> list = q.getResultList();
            logger.info("getByProduct() – zwrócono {} pozycji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getByProduct() – błąd podczas zapytania", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie wpisy w tabeli TransactionProduct.
     */
    public List<TransactionProduct> getAllTransactionProducts() {
        logger.debug("getAllTransactionProducts() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<TransactionProduct> list = em.createQuery(
                            "SELECT tp FROM TransactionProduct tp", TransactionProduct.class)
                    .getResultList();
            logger.info("getAllTransactionProducts() – zwrócono {} pozycji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllTransactionProducts() – błąd podczas zapytania", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Aktualizuje istniejącą pozycję transakcji.
     */
    public void updateTransactionProduct(TransactionProduct tp) {
        logger.debug("updateTransactionProduct() – start, tp={}", tp);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(tp);
            tx.commit();
            logger.info("updateTransactionProduct() – zaktualizowano: {}", tp);
        } catch (Exception e) {
            logger.error("updateTransactionProduct() – błąd podczas aktualizacji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Usuwa pozycję transakcji z bazy.
     */
    public void removeTransactionProduct(TransactionProduct tp) {
        logger.debug("removeTransactionProduct() – start, tp={}", tp);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TransactionProduct managed = em.find(TransactionProduct.class, tp.getId());
            if (managed != null) {
                em.remove(managed);
                logger.info("removeTransactionProduct() – usunięto: {}", tp);
            } else {
                logger.warn("removeTransactionProduct() – nie znaleziono: {}", tp);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeTransactionProduct() – błąd podczas usuwania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }
}
