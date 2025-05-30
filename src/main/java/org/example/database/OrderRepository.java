/*
 * Classname: OrderRepository
 * Version information: 1.6
 * Date: 2025-05-23
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repozytorium obsługujące operacje CRUD oraz zapytania na klasie Order.
 * Współdzieli EntityManagerFactory z EMFProvider.
 */
public class OrderRepository implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(OrderRepository.class);

    public OrderRepository() {
        logger.info("Utworzono OrderRepository, EMF={}", EMFProvider.get());
    }

    public void addOrder(Order order) {
        logger.debug("addOrder() – start, order={}", order);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(order);
            tx.commit();
            logger.info("addOrder() – zamówienie dodane: {}", order);
        } catch (Exception e) {
            logger.error("addOrder() – błąd podczas dodawania zamówienia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addOrder() – EM zamknięty");
        }
    }

    public Order findOrderById(int id) {
        logger.debug("findOrderById() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Order o = em.find(Order.class, id);
            logger.info("findOrderById() – znaleziono: {}", o);
            return o;
        } catch (Exception e) {
            logger.error("findOrderById() – błąd podczas pobierania zamówienia id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findOrderById() – EM zamknięty");
        }
    }

    public List<Order> getAllOrders() {
        logger.debug("getAllOrders() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Order> list = em.createQuery("SELECT o FROM Order o", Order.class)
                    .getResultList();
            logger.info("getAllOrders() – pobrano {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllOrders() – błąd podczas pobierania zamówień", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllOrders() – EM zamknięty");
        }
    }

    public void removeOrder(int id) {
        logger.debug("removeOrder() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Order o = em.find(Order.class, id);
            if (o != null) {
                em.remove(o);
                logger.info("removeOrder() – usunięto zamówienie: {}", o);
            } else {
                logger.warn("removeOrder() – brak zamówienia o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeOrder() – błąd podczas usuwania zamówienia id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeOrder() – EM zamknięty");
        }
    }

    public void updateOrder(Order order) {
        logger.debug("updateOrder() – start, order={}", order);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(order);
            tx.commit();
            logger.info("updateOrder() – zamówienie zaktualizowane: {}", order);
        } catch (Exception e) {
            logger.error("updateOrder() – błąd podczas aktualizacji zamówienia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateOrder() – EM zamknięty");
        }
    }

    public List<Order> findOrdersByProductId(int productId) {
        logger.debug("findOrdersByProductId() – productId={}", productId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Order o WHERE o.product.id = :pid", Order.class)
                    .setParameter("pid", productId)
                    .getResultList();
        } finally {
            em.close();
            logger.debug("findOrdersByProductId() – EM zamknięty");
        }
    }

    public List<Order> findOrdersByEmployeeId(int employeeId) {
        logger.debug("findOrdersByEmployeeId() – employeeId={}", employeeId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Order o WHERE o.employee.id = :eid", Order.class)
                    .setParameter("eid", employeeId)
                    .getResultList();
        } finally {
            em.close();
            logger.debug("findOrdersByEmployeeId() – EM zamknięty");
        }
    }

    public List<Order> findOrdersByDate(LocalDate date) {
        logger.debug("findOrdersByDate() – date={}", date);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Order o WHERE o.date = :d", Order.class)
                    .setParameter("d", date)
                    .getResultList();
        } finally {
            em.close();
            logger.debug("findOrdersByDate() – EM zamknięty");
        }
    }

    public List<Order> findDateRangeOrders(LocalDate from, LocalDate to) {
        logger.debug("findDateRangeOrders() – from={}, to={}", from, to);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Order o WHERE o.date BETWEEN :from AND :to", Order.class)
                    .setParameter("from", from)
                    .setParameter("to",   to)
                    .getResultList();
        } finally {
            em.close();
            logger.debug("findDateRangeOrders() – EM zamknięty");
        }
    }

    public List<Order> findOrdersWithMinimalQuantity(int minimalQuantity) {
        logger.debug("findOrdersWithMinimalQuantity() – minimalQuantity={}", minimalQuantity);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.quantity >= :mq", Order.class)
                    .setParameter("mq", minimalQuantity)
                    .getResultList();
            logger.info("findOrdersWithMinimalQuantity() – znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findOrdersWithMinimalQuantity() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findOrdersWithMinimalQuantity() – EM zamknięty");
        }
    }

    public List<Order> findPriceRangeOrders(BigDecimal min, BigDecimal max) {
        logger.debug("findPriceRangeOrders() – min={}, max={}", min, max);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Order o WHERE o.price BETWEEN :min AND :max", Order.class)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
        } finally {
            em.close();
            logger.debug("findPriceRangeOrders() – EM zamknięty");
        }
    }

    @Override
    public void close() {
    }
}

