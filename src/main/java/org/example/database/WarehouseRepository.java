/*
 * Classname: WarehouseRepository
 * Version information: 1.5
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Warehouse;

import java.util.List;

/**
 * Repozytorium do zarządzania stanem magazynowym.
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz wyszukiwanie stanów magazynowych.
 */
public class WarehouseRepository {
    private static final Logger logger = LogManager.getLogger(WarehouseRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący fabrykę EntityManagerFactory dla persistence unit "myPU".
     */
    public WarehouseRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono WarehouseRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowy state magazynowy (pozycję) do bazy.
     *
     * @param state obiekt Warehouse reprezentujący state magazynowy do zapisania
     */
    public void addWarehouseState(Warehouse state) {
        logger.debug("addWarehouseState() – start, state={}", state);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(state);
            tx.commit();
            logger.info("addWarehouseState() – dodano state: {}", state);
        } catch (Exception ex) {
            logger.error("addWarehouseState() – błąd podczas dodawania stanu", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addWarehouseState() – EM zamknięty");
        }
    }

    /**
     * Pobiera stan magazynowy (pozycję) na podstawie identyfikatora produktu.
     *
     * @param productId identyfikator produktu
     * @return obiekt Warehouse lub null, jeśli nie znaleziono
     */
    public Warehouse findStateByProductId(int productId) {
        logger.debug("findStateByProductId() – start, productId={}", productId);
        EntityManager em = emf.createEntityManager();
        try {
            Warehouse stan = em.find(Warehouse.class, productId);
            logger.info("findStateByProductId() – znaleziono: {}", stan);
            return stan;
        } catch (Exception ex) {
            logger.error("findStateByProductId() – błąd podczas wyszukiwania productId={}", productId, ex);
            return null;
        } finally {
            em.close();
            logger.debug("findStateByProductId() – EM zamknięty");
        }
    }

    /**
     * Pobiera wszystkie stany magazynowe z bazy.
     *
     * @return lista obiektów Warehouse lub pusta lista w przypadku błędu
     */
    public List<Warehouse> getAllStates() {
        logger.debug("getAllStates() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery("SELECT w FROM Warehouse w", Warehouse.class)
                    .getResultList();
            logger.info("getAllStates() – pobrano {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("getAllStates() – błąd podczas pobierania wszystkich stanów", ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllStates() – EM zamknięty");
        }
    }

    /**
     * Usuwa stan magazynowy na podstawie identyfikatora produktu.
     *
     * @param productId identyfikator produktu
     */
    public void removeState(int productId) {
        logger.debug("removeState() – start, productId={}", productId);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Warehouse stan = em.find(Warehouse.class, productId);
            if (stan != null) {
                em.remove(stan);
                logger.info("removeState() – usunięto stan: {}", stan);
            } else {
                logger.warn("removeState() – brak rekordu dla productId={}", productId);
            }
            tx.commit();
        } catch (Exception ex) {
            logger.error("removeState() – błąd podczas usuwania productId={}", productId, ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeState() – EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejący state magazynowy.
     *
     * @param state obiekt Warehouse z zaktualizowanymi danymi
     */
    public void updateState(Warehouse state) {
        logger.debug("updateState() – start, state={}", state);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(state);
            tx.commit();
            logger.info("updateState() – zaktualizowano state: {}", state);
        } catch (Exception ex) {
            logger.error("updateState() – błąd podczas aktualizacji stanu", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateState() – EM zamknięty");
        }
    }

    /**
     * Ustawia nową ilość produktu w magazynie (soft update).
     *
     * @param productId identyfikator produktu
     * @param newQty    nowa ilość produktu
     */
    public void setProductQuantity(int productId, int newQty) {
        logger.debug("setProductQuantity() – start, productId={}, newQty={}", productId, newQty);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Warehouse w = em.find(Warehouse.class, productId);
            if (w != null) {
                w.setQuantity(newQty);
                em.merge(w);
                logger.info("setProductQuantity() – ilość zaktualizowana: {} → {}", productId, newQty);
            } else {
                logger.warn("setProductQuantity() – brak rekordu dla productId={}", productId);
            }
            tx.commit();
        } catch (Exception ex) {
            logger.error("setProductQuantity() – błąd podczas ustawiania ilości", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("setProductQuantity() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje stany magazynowe o dokładnie podanej ilości.
     *
     * @param quantity wartość ilości
     * @return lista rekordów Warehouse lub pusta lista
     */
    public List<Warehouse> findByQuantity(int quantity) {
        logger.debug("findByQuantity() – quantity={}", quantity);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.quantity = :quantity", Warehouse.class)
                    .setParameter("quantity", quantity)
                    .getResultList();
            logger.info("findByQuantity() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("findByQuantity() – błąd dla quantity={}", quantity, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByQuantity() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje stany magazynowe o ilości mniejszej niż podana.
     *
     * @param max maksymalna ilość
     * @return lista rekordów Warehouse lub pusta lista
     */
    public List<Warehouse> findByQuantityLowerThan(int max) {
        logger.debug("findByQuantityLowerThan() – max={}", max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.quantity < :max", Warehouse.class)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("findByQuantityLowerThan() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("findByQuantityLowerThan() – błąd dla max={}", max, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByQuantityLowerThan() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje stany magazynowe o ilości większej niż podana.
     *
     * @param min minimalna ilość
     * @return lista rekordów Warehouse lub pusta lista
     */
    public List<Warehouse> findByQuantityGreaterThan(int min) {
        logger.debug("findByQuantityGreaterThan() – min={}", min);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.quantity > :min", Warehouse.class)
                    .setParameter("min", min)
                    .getResultList();
            logger.info("findByQuantityGreaterThan() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("findByQuantityGreaterThan() – błąd dla min={}", min, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByQuantityGreaterThan() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje stany magazynowe o ilości mieszczącej się w podanym przedziale.
     *
     * @param min minimalna ilość
     * @param max maksymalna ilość
     * @return lista rekordów Warehouse lub pusta lista
     */
    public List<Warehouse> findByQuantityBetween(int min, int max) {
        logger.debug("findByQuantityBetween() – min={}, max={}", min, max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.quantity BETWEEN :min AND :max", Warehouse.class)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("findByQuantityBetween() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("findByQuantityBetween() – błąd dla min={}, max={}", min, max, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByQuantityBetween() – EM zamknięty");
        }
    }

    /**
     * Zamyka fabrykę EntityManagerFactory, zwalniając wszystkie zasoby.
     * Po wywołaniu tej metody instancja repozytorium nie może być używana.
     */
    public void close() {
        logger.debug("close() – start zamykania EMF");
        try {
            if (emf.isOpen()) {
                emf.close();
                logger.info("close() – EMF zamknięty");
            } else {
                logger.warn("close() – EMF był już zamknięty");
            }
        } catch (Exception ex) {
            logger.error("close() – błąd podczas zamykania EMF", ex);
        }
    }
}
