/*
 * Classname: OrderRepository
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

import org.example.sys.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderRepository {
    private static final Logger logger = LogManager.getLogger(OrderRepository.class);
    private final EntityManagerFactory emf;

    /** Konstruktor inicjalizujący EntityManagerFactory. */
    public OrderRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono OrderRepository, EMF={}", emf);
    }

    /** Dodaje nowe zamówienie. */
    public void addOrder(Order order) {
        logger.debug("addOrder() - start, order={}", order);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(order);
            tx.commit();
            logger.info("addOrder() - zamówienie dodane: {}", order);
        } catch (Exception e) {
            logger.error("addOrder() - błąd podczas dodawania zamówienia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addOrder() - EM zamknięty");
        }
    }

    /** Pobiera zamówienie po ID. */
    public Order findOrderById(int id) {
        logger.debug("findOrderById() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Order o = em.find(Order.class, id);
            logger.info("findOrderById() - znaleziono: {}", o);
            return o;
        } catch (Exception e) {
            logger.error("findOrderById() - błąd podczas pobierania zamówienia o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findOrderById() - EM zamknięty");
        }
    }

    /** Pobiera wszystkie zamówienia. */
    public List<Order> getAllOrders() {
        logger.debug("getAllOrders() - start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery("SELECT o FROM Order o", Order.class)
                    .getResultList();
            logger.info("getAllOrders() - pobrano {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllOrders() - błąd podczas pobierania zamówień", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllOrders() - EM zamknięty");
        }
    }

    /** Usuwa zamówienie o podanym ID. */
    public void removeOrders(int id) {
        logger.debug("removeOrders() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Order o = em.find(Order.class, id);
            if (o != null) {
                em.remove(o);
                logger.info("removeOrders() - usunięto zamówienie: {}", o);
            } else {
                logger.warn("removeOrders() - brak zamówienia o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeOrders() - błąd podczas usuwania zamówienia o id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeOrders() - EM zamknięty");
        }
    }

    /** Aktualizuje istniejące zamówienie. */
    public void updateOrder(Order zamowienie) {
        logger.debug("updateOrder() - start, zamowienie={}", zamowienie);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(zamowienie);
            tx.commit();
            logger.info("updateOrder() - zamówienie zaktualizowane: {}", zamowienie);
        } catch (Exception e) {
            logger.error("updateOrder() - błąd podczas aktualizacji zamówienia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateOrder() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia po ID produktu. */
    public List<Order> findOrdersByProductId(int productId) {
        logger.debug("findOrdersByProductId() - productId={}", productId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.productId = :productId", Order.class)
                    .setParameter("productId", productId)
                    .getResultList();
            logger.info("findOrdersByProductId() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findOrdersByProductId() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findOrdersByProductId() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia po ID pracownika. */
    public List<Order> findOrdersByEmployeeId(int employeeId) {
        logger.debug("findOrdersByEmployeeId() - employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.employeeId = :employeeId", Order.class)
                    .setParameter("employeeId", employeeId)
                    .getResultList();
            logger.info("findOrdersByEmployeeId() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findOrdersByEmployeeId() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findOrdersByEmployeeId() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia dokonane w danym dniu. */
    public List<Order> findOrdersByDate(LocalDate date) {
        logger.debug("findOrdersByDate() - date={}", date);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.date = :date", Order.class)
                    .setParameter("date", date)
                    .getResultList();
            logger.info("findOrdersByDate() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findOrdersByDate() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findOrdersByDate() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia w podanym przedziale dat. */
    public List<Order> findDateRangeOrders(LocalDate fromDate, LocalDate toDate) {
        logger.debug("findDateRangeOrder() - fromDate={}, toDate={}", fromDate, toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.data BETWEEN :fromDate AND :toDate", Order.class)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
            logger.info("findDateRangeOrder() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findDateRangeOrder() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findDateRangeOrder() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia z minimalną ilością większą lub równą podanej. */
    public List<Order> findOrdersWithMinimalQuantity(int minimalQuantity) {
        logger.debug("findOrdersWithMinimalQuantity() - minimalQuantity={}", minimalQuantity);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.quantity >= :minimalQuantity", Order.class)
                    .setParameter("minimalQuantity", minimalQuantity)
                    .getResultList();
            logger.info("findOrdersWithMinimalQuantity() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findOrdersWithMinimalQuantity() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findOrdersWithMinimalQuantity() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia o cenie mieszczącej się w podanym przedziale. */
    public List<Order> findPriceRangeOrders(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.debug("findPriceRangeOrders() - minPrice={}, maxPrice={}", minPrice, maxPrice);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.cena BETWEEN :minPrice AND :maxPrice", Order.class)
                    .setParameter("minPrice", minPrice)
                    .setParameter("maxPrice", maxPrice)
                    .getResultList();
            logger.info("findPriceRangeOrders() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findPriceRangeOrders() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findPriceRangeOrders() - EM zamknięty");
        }
    }

    /** Zamyka fabrykę EntityManagerFactory. */
    public void close() {
        logger.debug("close() - start");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() - EMF zamknięty");
        } else {
            logger.warn("close() - EMF już zamknięty");
        }
    }
}
