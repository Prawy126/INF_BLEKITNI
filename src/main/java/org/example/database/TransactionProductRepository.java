/*
 * Classname: TransactionProductRepository
 * Version information: 1.3
 * Date: 2025-05-21
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.TransactionProduct;
import org.example.sys.TransactionProductId;

import java.util.List;

public class TransactionProductRepository {

    private static final Logger logger = LogManager.getLogger(TransactionProductRepository.class);
    private final EntityManagerFactory emf;

    public TransactionProductRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono TransactionProductRepository, EMF={}", emf);
    }

    /** 1. Dodaje nową pozycję transakcji. */
    public void dodajTransactionProduct(TransactionProduct tp) {
        logger.debug("dodajTransactionProduct() – start, tp={}", tp);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(tp);
            tx.commit();
            logger.info("dodajTransactionProduct() – dodano: {}", tp);
        } catch (Exception e) {
            logger.error("dodajTransactionProduct() – błąd podczas dodawania tp", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /** 2. Znajduje pozycję po kluczu złożonym (transactionId + productId). */
    public TransactionProduct znajdzPoId(int transactionId, int productId) {
        logger.debug("znajdzPoId() – start, txId={}, prodId={}", transactionId, productId);
        EntityManager em = emf.createEntityManager();
        try {
            TransactionProductId id = new TransactionProductId(transactionId, productId);
            TransactionProduct tp = em.find(TransactionProduct.class, id);
            logger.info("znajdzPoId() – znaleziono: {}", tp);
            return tp;
        } catch (Exception e) {
            logger.error("znajdzPoId() – błąd przy find()", e);
            return null;
        } finally {
            em.close();
        }
    }

    /** 3. Pobiera wszystkie pozycje dla danej transakcji. */
    public List<TransactionProduct> pobierzPoTransakcji(int transactionId) {
        logger.debug("pobierzPoTransakcji() – start, txId={}", transactionId);
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<TransactionProduct> q = em.createQuery(
                    "SELECT tp FROM TransactionProduct tp WHERE tp.transaction.id = :txId",
                    TransactionProduct.class);
            q.setParameter("txId", transactionId);
            List<TransactionProduct> list = q.getResultList();
            logger.info("pobierzPoTransakcji() – zwrócono {} pozycji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzPoTransakcji() – błąd podczas zapytania", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /** 4. Pobiera wszystkie pozycje dla danego produktu. */
    public List<TransactionProduct> pobierzPoProdukcie(int productId) {
        logger.debug("pobierzPoProdukcie() – start, prodId={}", productId);
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<TransactionProduct> q = em.createQuery(
                    "SELECT tp FROM TransactionProduct tp WHERE tp.product.id = :prodId",
                    TransactionProduct.class);
            q.setParameter("prodId", productId);
            List<TransactionProduct> list = q.getResultList();
            logger.info("pobierzPoProdukcie() – zwrócono {} pozycji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzPoProdukcie() – błąd podczas zapytania", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /** 5. Pobiera wszystkie wpisy w tabeli. */
    public List<TransactionProduct> pobierzWszystkie() {
        logger.debug("pobierzWszystkie() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<TransactionProduct> list = em.createQuery(
                            "SELECT tp FROM TransactionProduct tp", TransactionProduct.class)
                    .getResultList();
            logger.info("pobierzWszystkie() – zwrócono {} pozycji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkie() – błąd podczas zapytania", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /** 6. Aktualizuje istniejącą pozycję transakcji. */
    public void aktualizujTransactionProduct(TransactionProduct tp) {
        logger.debug("aktualizujTransactionProduct() – start, tp={}", tp);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(tp);
            tx.commit();
            logger.info("aktualizujTransactionProduct() – zaktualizowano: {}", tp);
        } catch (Exception e) {
            logger.error("aktualizujTransactionProduct() – błąd podczas aktualizacji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /** 7. Usuwa pozycję transakcji. */
    public void usunTransactionProduct(TransactionProduct tp) {
        logger.debug("usunTransactionProduct() – start, tp={}", tp);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TransactionProduct managed = em.find(TransactionProduct.class, tp.getId());
            if (managed != null) {
                em.remove(managed);
                logger.info("usunTransactionProduct() – usunięto: {}", tp);
            } else {
                logger.warn("usunTransactionProduct() – nie znaleziono: {}", tp);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunTransactionProduct() – błąd podczas usuwania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /** 8. Zamyka EntityManagerFactory. */
    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}
