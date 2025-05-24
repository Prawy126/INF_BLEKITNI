/*
 * Classname: OrderRepository
 * Version information: 1.6
 * Date: 2025-05-23
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

/**
 * Repozytorium obsługujące operacje CRUD oraz zapytania na klasie Order.
 */
public class OrderRepository {
    private static final Logger logger = LogManager.getLogger(OrderRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący fabrykę EntityManagerFactory
     * na podstawie konfiguracji persistence unit "myPU".
     */
    public OrderRepository() {
        logger.debug("Konstruktor OrderRepository – tworzenie EMF");
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("OrderRepository – utworzono EMF = {}", emf);
    }

    /**
     * Zapisuje nowe zamówienie w bazie.
     *
     * @param order obiekt Order do dodania
     */
    public void addOrder(Order order) {
        logger.debug("addOrder() – start, order={}", order);
        EntityManager em = emf.createEntityManager();
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

    /**
     * Pobiera zamówienie o podanym identyfikatorze.
     *
     * @param id identyfikator zamówienia
     * @return obiekt Order lub null, jeśli nie znaleziono
     */
    public Order findOrderById(int id) {
        logger.debug("findOrderById() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
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

    /**
     * Pobiera wszystkie zamówienia z tabeli Zamowienia.
     *
     * @return lista obiektów Order
     */
    public List<Order> getAllOrders() {
        logger.debug("getAllOrders() – start");
        EntityManager em = emf.createEntityManager();
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

    /**
     * Usuwa zamówienie o danym identyfikatorze.
     *
     * @param id identyfikator zamówienia do usunięcia
     */
    public void removeOrders(int id) {
        logger.debug("removeOrders() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Order o = em.find(Order.class, id);
            if (o != null) {
                em.remove(o);
                logger.info("removeOrders() – usunięto zamówienie: {}", o);
            } else {
                logger.warn("removeOrders() – brak zamówienia o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeOrders() – błąd podczas usuwania zamówienia id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeOrders() – EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejące zamówienie.
     *
     * @param zamowienie obiekt Order do zaktualizowania
     */
    public void updateOrder(Order zamowienie) {
        logger.debug("updateOrder() – start, zamowienie={}", zamowienie);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(zamowienie);
            tx.commit();
            logger.info("updateOrder() – zamówienie zaktualizowane: {}", zamowienie);
        } catch (Exception e) {
            logger.error("updateOrder() – błąd podczas aktualizacji zamówienia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateOrder() – EM zamknięty");
        }
    }

    /**
     * Pobiera zamówienia dla danego produktu.
     *
     * @param productId identyfikator produktu
     * @return lista obiektów Order związanych z tym produktem
     */
    public List<Order> findOrdersByProductId(int productId) {
        logger.debug("findOrdersByProductId() – productId={}", productId);
        EntityManager em = emf.createEntityManager();
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

    /**
     * Pobiera zamówienia dla danego pracownika.
     *
     * @param employeeId identyfikator pracownika
     * @return lista obiektów Order z podanym pracownikiem
     */
    public List<Order> findOrdersByEmployeeId(int employeeId) {
        logger.debug("findOrdersByEmployeeId() – employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
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

    /**
     * Pobiera zamówienia dokonane w danym dniu.
     *
     * @param date data zamówienia
     * @return lista obiektów Order z tą datą
     */
    public List<Order> findOrdersByDate(LocalDate date) {
        logger.debug("findOrdersByDate() – date={}", date);
        EntityManager em = emf.createEntityManager();
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

    /**
     * Pobiera zamówienia w zadanym przedziale dat.
     *
     * @param from początkowa data zakresu (inclusive)
     * @param to   końcowa data zakresu (inclusive)
     * @return lista obiektów Order z datami pomiędzy from i to
     */
    public List<Order> findDateRangeOrders(LocalDate from, LocalDate to) {
        logger.debug("findDateRangeOrders() – from={}, to={}", from, to);
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Order o WHERE o.date BETWEEN :from AND :to",
                            Order.class)
                    .setParameter("from", from)
                    .setParameter("to",   to)
                    .getResultList();
        } finally {
            em.close();
            logger.debug("findDateRangeOrders() – EM zamknięty");
        }
    }

    /**
     * Pobiera zamówienia o minimalnej ilości większej lub równej podanej.
     *
     * @param minimalQuantity minimalna ilość
     * @return lista obiektów Order z ilością ≥ minimalQuantity
     */
    public List<Order> findOrdersWithMinimalQuantity(int minimalQuantity) {
        logger.debug("findOrdersWithMinimalQuantity() – minimalQuantity={}", minimalQuantity);
        EntityManager em = emf.createEntityManager();
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

    /**
     * Pobiera zamówienia w zadanym przedziale cen.
     *
     * @param min minimalna cena (inclusive)
     * @param max maksymalna cena (inclusive)
     * @return lista obiektów Order z ceną pomiędzy min i max
     */
    public List<Order> findPriceRangeOrders(BigDecimal min, BigDecimal max) {
        logger.debug("findPriceRangeOrders() – min={}, max={}", min, max);
        EntityManager em = emf.createEntityManager();
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

    /**
     * Zamyka fabrykę EntityManagerFactory.
     */
    public void close() {
        logger.debug("close() – start");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        } else {
            logger.warn("close() – EMF już zamknięty");
        }
    }
}
