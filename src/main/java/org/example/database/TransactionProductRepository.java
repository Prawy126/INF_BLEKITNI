/*
 * Classname: TransactionProductRepository
 * Version information: 1.4
 * Date: 2025-05-22
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

/**
 * Repozytorium do zarządzania pozycjami produktów w ramach transakcji.
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz wyszukiwanie rekordów TransactionProduct.
 */
public class TransactionProductRepository {

    private static final Logger logger = LogManager.getLogger(TransactionProductRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory dla persistence unit "myPU".
     */
    public TransactionProductRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono TransactionProductRepository, EMF={}", emf);
    }

    /**
     * Dodaje nową pozycję produktu do transakcji.
     *
     * @param tp obiekt TransactionProduct do zapisania
     */
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

    /**
     * Znajduje pozycję transakcji po kluczu złożonym (transactionId + productId).
     *
     * @param transactionId identyfikator transakcji
     * @param productId     identyfikator produktu
     * @return obiekt TransactionProduct lub null, jeśli nie znaleziono
     */
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

    /**
     * Pobiera wszystkie pozycje produktów dla danej transakcji.
     *
     * @param transactionId identyfikator transakcji
     * @return lista obiektów TransactionProduct lub pusta lista
     */
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

    /**
     * Pobiera wszystkie pozycje transakcji powiązane z danym produktem.
     *
     * @param productId identyfikator produktu
     * @return lista obiektów TransactionProduct lub pusta lista
     */
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

    /**
     * Pobiera wszystkie wpisy w tabeli TransactionProduct.
     *
     * @return lista wszystkich obiektów TransactionProduct lub pusta lista
     */
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

    /**
     * Aktualizuje istniejącą pozycję transakcji.
     *
     * @param tp obiekt TransactionProduct do zaktualizowania
     */
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

    /**
     * Usuwa pozycję transakcji z bazy.
     *
     * @param tp obiekt TransactionProduct do usunięcia
     */
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

    /**
     * Zamyka fabrykę EntityManagerFactory, zwalniając wszystkie zasoby.
     * Po wywołaniu tej metody instancja nie może być używana do dalszych operacji.
     */
    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}
