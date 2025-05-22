/*
 * Classname: OrderRepository
 * Version information: 1.4
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
    public void dodajZamowienie(Order zamowienie) {
        logger.debug("dodajZamowienie() - start, zamowienie={}", zamowienie);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(zamowienie);
            tx.commit();
            logger.info("dodajZamowienie() - zamówienie dodane: {}", zamowienie);
        } catch (Exception e) {
            logger.error("dodajZamowienie() - błąd podczas dodawania zamówienia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajZamowienie() - EM zamknięty");
        }
    }

    /** Pobiera zamówienie po ID. */
    public Order znajdzZamowieniePoId(int id) {
        logger.debug("znajdzZamowieniePoId() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Order o = em.find(Order.class, id);
            logger.info("znajdzZamowieniePoId() - znaleziono: {}", o);
            return o;
        } catch (Exception e) {
            logger.error("znajdzZamowieniePoId() - błąd podczas pobierania zamówienia o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzZamowieniePoId() - EM zamknięty");
        }
    }

    /** Pobiera wszystkie zamówienia. */
    public List<Order> pobierzWszystkieZamowienia() {
        logger.debug("pobierzWszystkieZamowienia() - start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery("SELECT o FROM Order o", Order.class)
                    .getResultList();
            logger.info("pobierzWszystkieZamowienia() - pobrano {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkieZamowienia() - błąd podczas pobierania zamówień", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieZamowienia() - EM zamknięty");
        }
    }

    /** Usuwa zamówienie o podanym ID. */
    public void usunZamowienie(int id) {
        logger.debug("usunZamowienie() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Order o = em.find(Order.class, id);
            if (o != null) {
                em.remove(o);
                logger.info("usunZamowienie() - usunięto zamówienie: {}", o);
            } else {
                logger.warn("usunZamowienie() - brak zamówienia o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunZamowienie() - błąd podczas usuwania zamówienia o id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunZamowienie() - EM zamknięty");
        }
    }

    /** Aktualizuje istniejące zamówienie. */
    public void aktualizujZamowienie(Order zamowienie) {
        logger.debug("aktualizujZamowienie() - start, zamowienie={}", zamowienie);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(zamowienie);
            tx.commit();
            logger.info("aktualizujZamowienie() - zamówienie zaktualizowane: {}", zamowienie);
        } catch (Exception e) {
            logger.error("aktualizujZamowienie() - błąd podczas aktualizacji zamówienia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujZamowienie() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia po ID produktu. */
    public List<Order> znajdzZamowieniaPoIdProduktu(int idProduktu) {
        logger.debug("znajdzZamowieniaPoIdProduktu() - idProduktu={}", idProduktu);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.idProduktu = :idProduktu", Order.class)
                    .setParameter("idProduktu", idProduktu)
                    .getResultList();
            logger.info("znajdzZamowieniaPoIdProduktu() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzZamowieniaPoIdProduktu() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzZamowieniaPoIdProduktu() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia po ID pracownika. */
    public List<Order> znajdzZamowieniaPoIdPracownika(int idPracownika) {
        logger.debug("znajdzZamowieniaPoIdPracownika() - idPracownika={}", idPracownika);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.idPracownika = :idPracownika", Order.class)
                    .setParameter("idPracownika", idPracownika)
                    .getResultList();
            logger.info("znajdzZamowieniaPoIdPracownika() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzZamowieniaPoIdPracownika() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzZamowieniaPoIdPracownika() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia dokonane w danym dniu. */
    public List<Order> znajdzZamowieniaPoDacie(LocalDate data) {
        logger.debug("znajdzZamowieniaPoDacie() - data={}", data);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.data = :data", Order.class)
                    .setParameter("data", data)
                    .getResultList();
            logger.info("znajdzZamowieniaPoDacie() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzZamowieniaPoDacie() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzZamowieniaPoDacie() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia w podanym przedziale dat. */
    public List<Order> znajdzZamowieniaWZakresieDat(LocalDate dataOd, LocalDate dataDo) {
        logger.debug("znajdzZamowieniaWZakresieDat() - dataOd={}, dataDo={}", dataOd, dataDo);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.data BETWEEN :od AND :do", Order.class)
                    .setParameter("od", dataOd)
                    .setParameter("do", dataDo)
                    .getResultList();
            logger.info("znajdzZamowieniaWZakresieDat() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzZamowieniaWZakresieDat() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzZamowieniaWZakresieDat() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia z minimalną ilością większą lub równą podanej. */
    public List<Order> znajdzZamowieniaZMinimalnaIloscia(int minimalnaIlosc) {
        logger.debug("znajdzZamowieniaZMinimalnaIloscia() - minimalnaIlosc={}", minimalnaIlosc);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.ilosc >= :minimalnaIlosc", Order.class)
                    .setParameter("minimalnaIloscia", minimalnaIlosc)
                    .getResultList();
            logger.info("znajdzZamowieniaZMinimalnaIloscia() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzZamowieniaZMinimalnaIloscia() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzZamowieniaZMinimalnaIloscia() - EM zamknięty");
        }
    }

    /** Pobiera zamówienia o cenie mieszczącej się w podanym przedziale. */
    public List<Order> znajdzZamowieniaWPrzedzialeCenowym(BigDecimal minCena, BigDecimal maxCena) {
        logger.debug("znajdzZamowieniaWPrzedzialeCenowym() - minCena={}, maxCena={}", minCena, maxCena);
        EntityManager em = emf.createEntityManager();
        try {
            List<Order> list = em.createQuery(
                            "SELECT o FROM Order o WHERE o.cena BETWEEN :minCena AND :maxCena", Order.class)
                    .setParameter("minCena", minCena)
                    .setParameter("maxCena", maxCena)
                    .getResultList();
            logger.info("znajdzZamowieniaWPrzedzialeCenowym() - znaleziono {} zamówień", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzZamowieniaWPrzedzialeCenowym() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzZamowieniaWPrzedzialeCenowym() - EM zamknięty");
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
