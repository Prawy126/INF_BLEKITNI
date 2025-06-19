/*
 * Classname: OrderRepository
 * Version information: 1.6
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.EMFProvider;
import org.example.sys.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repozytorium obsługujące operacje CRUD oraz zapytania na klasie Order.
 * Współdzieli EntityManagerFactory z EMFProvider.
 * Umożliwia zarządzanie zamówieniami oraz ich wyszukiwanie
 * według różnych kryteriów.
 */
public class OrderRepository implements AutoCloseable {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą OrderRepository.
     */
    private static final Logger logger = LogManager.getLogger(
            OrderRepository.class);

    /**
     * Domyślny konstruktor – korzysta ze wspólnego EMF z EMFProvider.
     * Operacja jest logowana na poziomie INFO.
     */
    public OrderRepository() {
        logger.info("Utworzono OrderRepository," +
                " EMF={}", EMFProvider.get());
    }

    /**
     * Dodaje nowe zamówienie do bazy.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param order obiekt Order do zapisania
     */
    public void addOrder(Order order) {
        logger.debug("addOrder() – start, order={}", order);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(order);
            tx.commit();
            logger.info("addOrder() " +
                    "– zamówienie dodane: {}", order);
        } catch (Exception e) {
            logger.error("addOrder() " +
                    "– błąd podczas dodawania zamówienia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addOrder() – EM zamknięty");
        }
    }

    /**
     * Znajduje zamówienie o podanym identyfikatorze.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest wartość null.
     *
     * @param id identyfikator zamówienia
     * @return obiekt Order lub null, jeśli nie istnieje
     */
    public Order findOrderById(int id) {
        logger.debug("findOrderById() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Order o = em.find(Order.class, id);
            logger.info("findOrderById() " +
                    "– znaleziono: {}", o);
            return o;
        } catch (Exception e) {
            logger.error("findOrderById() " +
                            "– błąd podczas pobierania zamówienia id={}",
                    id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findOrderById() – EM zamknięty");
        }
    }

    /**
     * Pobiera listę wszystkich zamówień.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @return lista wszystkich zamówień lub pusta lista w przypadku błędu
     */
    public List<Order> getAllOrders() {
        logger.debug("getAllOrders() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o",
                            Order.class)
                    .getResultList();
            logger.info("getAllOrders() " +
                    "– pobrano {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllOrders() " +
                    "– błąd podczas pobierania zamówień", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllOrders() – EM zamknięty");
        }
    }

    /**
     * Usuwa zamówienie o podanym identyfikatorze.
     * Operacja jest wykonywana w transakcji.
     * Jeśli zamówienie nie istnieje, operacja jest logowana jako ostrzeżenie.
     *
     * @param id identyfikator zamówienia do usunięcia
     */
    public void removeOrder(int id) {
        logger.debug("removeOrder() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Order o = em.find(Order.class, id);
            if (o != null) {
                em.remove(o);
                logger.info("removeOrder() " +
                        "– usunięto zamówienie: {}", o);
            } else {
                logger.warn("removeOrder() " +
                        "– brak zamówienia o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeOrder() " +
                            "– błąd podczas usuwania zamówienia id={}",
                    id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeOrder() – EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejące zamówienie.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param order zaktualizowany obiekt Order
     */
    public void updateOrder(Order order) {
        logger.debug("updateOrder() – start, order={}", order);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(order);
            tx.commit();
            logger.info("updateOrder() " +
                    "– zamówienie zaktualizowane: {}", order);
        } catch (Exception e) {
            logger.error("updateOrder() " +
                    "– błąd podczas aktualizacji zamówienia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateOrder() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje zamówienia dla podanego produktu.
     * Zwraca listę zamówień, które zawierają produkt o podanym ID.
     *
     * @param productId identyfikator produktu
     * @return lista zamówień dla produktu lub pusta lista
     */
    public List<Order> findOrdersByProductId(int productId) {
        logger.debug("findOrdersByProductId() " +
                "– productId={}", productId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o " +
                                    "WHERE o.product.id = :pid",
                            Order.class)
                    .setParameter("pid", productId)
                    .getResultList();
            logger.info("findOrdersByProductId() " +
                            "– znaleziono {} zamówień",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("findOrdersByProductId()" +
                    " – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findOrdersByProductId() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje zamówienia złożone przez pracownika.
     * Zwraca listę zamówień powiązanych z pracownikiem o podanym ID.
     *
     * @param employeeId identyfikator pracownika
     * @return lista zamówień pracownika lub pusta lista
     */
    public List<Order> findOrdersByEmployeeId(int employeeId) {
        logger.debug("findOrdersByEmployeeId() " +
                "– employeeId={}", employeeId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o " +
                                    "WHERE o.employee.id = :eid",
                            Order.class)
                    .setParameter("eid", employeeId)
                    .getResultList();
            logger.info("findOrdersByEmployeeId() " +
                            "– znaleziono {} zamówień",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("findOrdersByEmployeeId() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findOrdersByEmployeeId() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje zamówienia złożone w konkretnym dniu.
     * Zwraca listę zamówień z dokładnie podaną datą.
     *
     * @param date data zamówienia
     * @return lista zamówień z podaną datą lub pusta lista
     */
    public List<Order> findOrdersByDate(LocalDate date) {
        logger.debug("findOrdersByDate() – date={}", date);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.date = :d",
                            Order.class)
                    .setParameter("d", date)
                    .getResultList();
            logger.info("findOrdersByDate() " +
                    "– znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findOrdersByDate() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findOrdersByDate() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje zamówienia z podanego zakresu dat.
     * Zwraca listę zamówień, których data mieści się w przedziale.
     *
     * @param from początek zakresu dat (włącznie)
     * @param to koniec zakresu dat (włącznie)
     * @return lista zamówień z podanego zakresu dat lub pusta lista
     */
    public List<Order> findDateRangeOrders(LocalDate from, LocalDate to) {
        logger.debug("findDateRangeOrders() – from={}, to={}",
                from, to);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.date " +
                                    "BETWEEN :from AND :to",
                            Order.class)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList();
            logger.info("findDateRangeOrders() " +
                            "– znaleziono {} zamówień",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("findDateRangeOrders() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findDateRangeOrders() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje zamówienia z minimalną ilością produktów.
     * Zwraca zamówienia, w których liczba sztuk jest większa lub równa
     * podanej wartości.
     *
     * @param minimalQuantity minimalna ilość produktów
     * @return lista zamówień spełniających warunek lub pusta lista
     */
    public List<Order> findOrdersWithMinimalQuantity(int minimalQuantity) {
        logger.debug("findOrdersWithMinimalQuantity() " +
                        "– minimalQuantity={}",
                minimalQuantity);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.quantity >= :mq",
                            Order.class)
                    .setParameter("mq", minimalQuantity)
                    .getResultList();
            logger.info("findOrdersWithMinimalQuantity() " +
                            "– znaleziono {} zamówień",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("findOrdersWithMinimalQuantity() " +
                    "– błąd wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findOrdersWithMinimalQuantity() " +
                    "– EM zamknięty");
        }
    }

    /**
     * Wyszukuje zamówienia z ceną w podanym przedziale.
     * Zwraca listę zamówień, których cena mieści się
     * w podanym przedziale.
     *
     * @param min dolna granica ceny (włącznie)
     * @param max górna granica ceny (włącznie)
     * @return lista zamówień spełniających warunek lub pusta lista
     */
    public List<Order> findPriceRangeOrders(
            BigDecimal min,
            BigDecimal max
    ) {
        logger.debug("findPriceRangeOrders() " +
                "– min={}, max={}", min, max);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.price " +
                                    "BETWEEN :min AND :max",
                            Order.class)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("findPriceRangeOrders() " +
                            "– znaleziono {} zamówień",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("findPriceRangeOrders() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findPriceRangeOrders() – EM zamknięty");
        }
    }

    /**
     * Zamyka wspólną fabrykę EMF (na zakończenie działania aplikacji).
     * Implementacja jest pusta, ponieważ korzystamy z EMFProvider.
     */
    @Override
    public void close() {
    }
}