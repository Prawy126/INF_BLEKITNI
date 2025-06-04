/*
 * Classname: WarehouseRepository
 * Version information: 1.0
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Warehouse;

import java.util.List;

/**
 * Repozytorium do zarządzania stanem magazynowym produktów.
 * Zapewnia operacje CRUD oraz metody wyszukiwania stanów magazynowych
 * według różnych kryteriów ilościowych. Wykorzystuje EntityManager
 * do komunikacji z bazą danych.
 */
public class WarehouseRepository {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą WarehouseRepository.
     */
    private static final Logger logger = LogManager.getLogger(
            WarehouseRepository.class);

    /**
     * Domyślny konstruktor – korzysta ze wspólnego EMF z EMFProvider.
     * Operacja jest logowana na poziomie INFO.
     */
    public WarehouseRepository() {
        logger.info("Utworzono WarehouseRepository, korzysta z EMFProvider");
    }

    /**
     * Dodaje nowy stan magazynowy produktu do bazy.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param state obiekt stanu magazynowego do zapisania
     */
    public void addWarehouseState(Warehouse state) {
        logger.debug("addWarehouseState() – start, state={}", state);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(state);
            tx.commit();
            logger.info("addWarehouseState() – dodano state: {}", state);
        } catch (Exception ex) {
            logger.error("addWarehouseState() " +
                    "– błąd podczas dodawania stanu", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addWarehouseState() – EM zamknięty");
        }
    }

    /**
     * Znajduje stan magazynowy dla produktu o podanym identyfikatorze.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest wartość null.
     *
     * @param productId identyfikator produktu
     * @return obiekt stanu magazynowego lub null, jeśli nie istnieje
     */
    public Warehouse findStateByProductId(int productId) {
        logger.debug("findStateByProductId() " +
                "– start, productId={}", productId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Warehouse stan = em.find(Warehouse.class, productId);
            logger.info("findStateByProductId() " +
                    "– znaleziono: {}", stan);
            return stan;
        } catch (Exception ex) {
            logger.error("findStateByProductId() " +
                            "– błąd podczas wyszukiwania productId={}",
                    productId, ex);
            return null;
        } finally {
            em.close();
            logger.debug("findStateByProductId() – EM zamknięty");
        }
    }

    /**
     * Pobiera listę wszystkich stanów magazynowych.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @return lista wszystkich stanów magazynowych
     * lub pusta lista w przypadku błędu
     */
    public List<Warehouse> getAllStates() {
        logger.debug("getAllStates() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w",
                            Warehouse.class)
                    .getResultList();
            logger.info("getAllStates() " +
                    "– pobrano {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("getAllStates() " +
                    "– błąd podczas pobierania wszystkich stanów", ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllStates() – EM zamknięty");
        }
    }

    /**
     * Usuwa stan magazynowy dla produktu o podanym identyfikatorze.
     * Operacja jest wykonywana w transakcji.
     * Jeśli stan nie istnieje, operacja jest logowana jako ostrzeżenie.
     *
     * @param productId identyfikator produktu
     */
    public void removeState(int productId) {
        logger.debug("removeState() – start, productId={}", productId);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Warehouse stan = em.find(Warehouse.class, productId);
            if (stan != null) {
                em.remove(stan);
                logger.info("removeState() – usunięto stan: {}", stan);
            } else {
                logger.warn("removeState() " +
                        "– brak rekordu dla productId={}", productId);
            }
            tx.commit();
        } catch (Exception ex) {
            logger.error("removeState() " +
                            "– błąd podczas usuwania productId={}",
                    productId, ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeState() – EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejący stan magazynowy.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param state zaktualizowany obiekt stanu magazynowego
     */
    public void updateState(Warehouse state) {
        logger.debug("updateState() – start, state={}", state);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(state);
            tx.commit();
            logger.info("updateState() " +
                    "– zaktualizowano state: {}", state);
        } catch (Exception ex) {
            logger.error("updateState() " +
                    "– błąd podczas aktualizacji stanu", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateState() – EM zamknięty");
        }
    }

    /**
     * Bezpośrednio ustawia ilość produktu w magazynie.
     * Operacja jest wykonywana w transakcji.
     * Jeśli produkt nie istnieje, operacja jest logowana jako ostrzeżenie.
     *
     * @param productId identyfikator produktu
     * @param newQty nowa ilość produktu
     */
    public void setProductQuantity(int productId, int newQty) {
        logger.debug("setProductQuantity() " +
                        "– start, productId={}, newQty={}",
                productId, newQty);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Warehouse w = em.find(Warehouse.class, productId);
            if (w != null) {
                w.setQuantity(newQty);
                em.merge(w);
                logger.info("setProductQuantity() " +
                                "– ilość zaktualizowana: {} → {}",
                        productId, newQty);
            } else {
                logger.warn("setProductQuantity() " +
                                "– brak rekordu dla productId={}",
                        productId);
            }
            tx.commit();
        } catch (Exception ex) {
            logger.error("setProductQuantity() " +
                    "– błąd podczas ustawiania ilości", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("setProductQuantity() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje produkty o dokładnie określonej ilości w magazynie.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @param quantity szukana ilość produktu
     * @return lista stanów magazynowych z podaną ilością
     */
    public List<Warehouse> findByQuantity(int quantity) {
        logger.debug("findByQuantity() – quantity={}", quantity);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w " +
                                    "WHERE w.quantity = :quantity",
                            Warehouse.class)
                    .setParameter("quantity", quantity)
                    .getResultList();
            logger.info("findByQuantity() " +
                    "– znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("findByQuantity() " +
                    "– błąd dla quantity={}", quantity, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByQuantity() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje produkty, których ilość w magazynie jest mniejsza od podanej.
     * Przydatne do identyfikacji produktów wymagających uzupełnienia.
     *
     * @param max maksymalna ilość (wyłącznie)
     * @return lista stanów magazynowych z ilością mniejszą od podanej
     */
    public List<Warehouse> findByQuantityLowerThan(int max) {
        logger.debug("findByQuantityLowerThan() – max={}", max);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w " +
                                    "WHERE w.quantity < :max",
                            Warehouse.class)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("findByQuantityLowerThan() " +
                            "– znaleziono {} rekordów",
                    list.size());
            return list;
        } catch (Exception ex) {
            logger.error("findByQuantityLowerThan() " +
                    "– błąd dla max={}", max, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByQuantityLowerThan() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje produkty, których ilość w magazynie jest większa od podanej.
     * Przydatne do identyfikacji nadmiarów magazynowych.
     *
     * @param min minimalna ilość (wyłącznie)
     * @return lista stanów magazynowych z ilością większą od podanej
     */
    public List<Warehouse> findByQuantityGreaterThan(int min) {
        logger.debug("findByQuantityGreaterThan() – min={}", min);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w " +
                                    "WHERE w.quantity > :min",
                            Warehouse.class)
                    .setParameter("min", min)
                    .getResultList();
            logger.info("findByQuantityGreaterThan() " +
                            "– znaleziono {} rekordów",
                    list.size());
            return list;
        } catch (Exception ex) {
            logger.error("findByQuantityGreaterThan() " +
                    "– błąd dla min={}", min, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByQuantityGreaterThan() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje produkty, których ilość w magazynie mieści się
     * w podanym przedziale.
     *
     * @param min minimalna ilość (włącznie)
     * @param max maksymalna ilość (włącznie)
     * @return lista stanów magazynowych z ilością w podanym przedziale
     */
    public List<Warehouse> findByQuantityBetween(int min, int max) {
        logger.debug("findByQuantityBetween() " +
                "– min={}, max={}", min, max);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w " +
                                    "WHERE w.quantity BETWEEN :min AND :max",
                            Warehouse.class)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("findByQuantityBetween() " +
                    "– znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("findByQuantityBetween() " +
                            "– błąd dla min={}, max={}",
                    min, max, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByQuantityBetween() – EM zamknięty");
        }
    }

    /**
     * Zamyka wspólną fabrykę EMF (na zakończenie działania aplikacji).
     * Implementacja jest pusta, ponieważ korzystamy z EMFProvider.
     */
    public void close() {
    }
}