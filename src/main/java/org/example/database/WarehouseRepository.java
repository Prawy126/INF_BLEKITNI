package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Warehouse;

import java.util.List;

public class WarehouseRepository {
    private static final Logger logger = LogManager.getLogger(WarehouseRepository.class);

    public WarehouseRepository() {
        logger.info("Utworzono WarehouseRepository, korzysta z EMFProvider");
    }

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
            logger.error("addWarehouseState() – błąd podczas dodawania stanu", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addWarehouseState() – EM zamknięty");
        }
    }

    public Warehouse findStateByProductId(int productId) {
        logger.debug("findStateByProductId() – start, productId={}", productId);
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public List<Warehouse> getAllStates() {
        logger.debug("getAllStates() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public void updateState(Warehouse state) {
        logger.debug("updateState() – start, state={}", state);
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public void setProductQuantity(int productId, int newQty) {
        logger.debug("setProductQuantity() – start, productId={}, newQty={}", productId, newQty);
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public List<Warehouse> findByQuantity(int quantity) {
        logger.debug("findByQuantity() – quantity={}", quantity);
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public List<Warehouse> findByQuantityLowerThan(int max) {
        logger.debug("findByQuantityLowerThan() – max={}", max);
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public List<Warehouse> findByQuantityGreaterThan(int min) {
        logger.debug("findByQuantityGreaterThan() – min={}", min);
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public List<Warehouse> findByQuantityBetween(int min, int max) {
        logger.debug("findByQuantityBetween() – min={}, max={}", min, max);
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public void close() {
    }
}
