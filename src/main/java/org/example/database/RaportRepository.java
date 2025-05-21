/*
 * Classname: RaportRepository
 * Version information: 1.4
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Raport;

import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RaportRepository {
    private static final Logger logger = LogManager.getLogger(RaportRepository.class);
    private final EntityManagerFactory emf;

    public RaportRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono RaportRepository, EMF={}", emf);
    }

    /** Dodaje nowy raport. */
    public void dodajRaport(Raport raport) {
        logger.debug("dodajRaport() – start, raport={}", raport);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(raport);
            tx.commit();
            logger.info("dodajRaport() – raport dodany: {}", raport);
        } catch (Exception e) {
            logger.error("dodajRaport() – błąd podczas dodawania raportu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajRaport() – EntityManager zamknięty");
        }
    }

    /** Znajduje raport po ID. */
    public Raport znajdzRaportPoId(int id) {
        logger.debug("znajdzRaportPoId() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Raport r = em.find(Raport.class, id);
            logger.info("znajdzRaportPoId() – znaleziono: {}", r);
            return r;
        } catch (Exception e) {
            logger.error("znajdzRaportPoId() – błąd podczas wyszukiwania raportu o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzRaportPoId() – EntityManager zamknięty");
        }
    }

    /** Pobiera wszystkie raporty. */
    public List<Raport> pobierzWszystkieRaporty() {
        logger.debug("pobierzWszystkieRaporty() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Raport> list = em.createQuery("SELECT r FROM Raport r", Raport.class)
                    .getResultList();
            logger.info("pobierzWszystkieRaporty() – pobrano {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkieRaporty() – błąd podczas pobierania raportów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieRaporty() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera raporty danego pracownika przypisane na konkretny dzień.
     *
     * @param pracownikId identyfikator pracownika
     * @param dzien       dzień do sprawdzenia (bez czasu)
     * @return lista raportów z tego dnia lub pusta lista
     */
    public List<Raport> pobierzRaportyPracownikaDzien(int pracownikId, LocalDate dzien) {
        logger.debug("pobierzRaportyPracownikaDzien() – start, pracownikId={}, dzien={}", pracownikId, dzien);
        EntityManager em = emf.createEntityManager();
        try {
            List<Raport> list = em.createQuery(
                            "SELECT r FROM Raport r " +
                                    "WHERE r.pracownik.id = :pid " +
                                    "  AND r.dataPoczatku = :data", Raport.class)
                    .setParameter("pid",  pracownikId)
                    // użycie java.sql.Date.valueOf dla LocalDate
                    .setParameter("data", java.sql.Date.valueOf(dzien), TemporalType.DATE)
                    .getResultList();
            logger.info("pobierzRaportyPracownikaDzien() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("pobierzRaportyPracownikaDzien() – błąd dla pracownikId={}, dzien={}", pracownikId, dzien, ex);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("pobierzRaportyPracownikaDzien() – EntityManager zamknięty");
        }
    }

    /** Aktualizuje raport. */
    public void aktualizujRaport(Raport raport) {
        logger.debug("aktualizujRaport() – start, raport={}", raport);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(raport);
            tx.commit();
            logger.info("aktualizujRaport() – raport zaktualizowany: {}", raport);
        } catch (Exception e) {
            logger.error("aktualizujRaport() – błąd podczas aktualizacji raportu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujRaport() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa raport z bazy oraz (jeśli istnieje) plik na dysku.
     */
    public void usunRaport(int id) {
        logger.debug("usunRaport() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Raport r = em.find(Raport.class, id);
            if (r != null) {
                File file = new File(r.getSciezkaPliku());
                if (file.exists() && file.isFile()) {
                    if (file.delete()) {
                        logger.info("usunRaport() – plik skojarzony usunięty: {}", r.getSciezkaPliku());
                    } else {
                        logger.warn("usunRaport() – nie udało się usunąć pliku: {}", r.getSciezkaPliku());
                    }
                }
                em.remove(r);
                logger.info("usunRaport() – raport usunięty z bazy: {}", r);
            } else {
                logger.warn("usunRaport() – brak raportu o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunRaport() – błąd podczas usuwania raportu o id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunRaport() – EntityManager zamknięty");
        }
    }

    // =========================================================
    // === Poniżej metody wyszukiwania raportów po kryteriach ===
    // =========================================================

    public List<Raport> znajdzPoTypie(String typFragment) {
        logger.debug("znajdzPoTypie() – typFragment={}", typFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Raport> list = em.createQuery(
                            "SELECT r FROM Raport r WHERE LOWER(r.typRaportu) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            Raport.class)
                    .setParameter("frag", typFragment)
                    .getResultList();
            logger.info("znajdzPoTypie() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoTypie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoTypie() – EntityManager zamknięty");
        }
    }

    public List<Raport> znajdzPoDaciePoczatku(Date startDate, Date endDate) {
        logger.debug("znajdzPoDaciePoczatku() – startDate={}, endDate={}", startDate, endDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<Raport> list = em.createQuery(
                            "SELECT r FROM Raport r WHERE r.dataPoczatku BETWEEN :startDate AND :endDate",
                            Raport.class)
                    .setParameter("startDate", startDate, TemporalType.DATE)
                    .setParameter("endDate", endDate,   TemporalType.DATE)
                    .getResultList();
            logger.info("znajdzPoDaciePoczatku() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoDaciePoczatku() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoDaciePoczatku() – EntityManager zamknięty");
        }
    }

    public List<Raport> znajdzPoDacieZakonczenia(Date startDate, Date endDate) {
        logger.debug("znajdzPoDacieZakonczenia() – startDate={}, endDate={}", startDate, endDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<Raport> list = em.createQuery(
                            "SELECT r FROM Raport r WHERE r.dataZakonczenia BETWEEN :startDate AND :endDate",
                            Raport.class)
                    .setParameter("startDate", startDate, TemporalType.DATE)
                    .setParameter("endDate", endDate,   TemporalType.DATE)
                    .getResultList();
            logger.info("znajdzPoDacieZakonczenia() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoDacieZakonczenia() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoDacieZakonczenia() – EntityManager zamknięty");
        }
    }

    public List<Raport> znajdzPoPracowniku(int pracownikId) {
        logger.debug("znajdzPoPracowniku() – pracownikId={}", pracownikId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Raport> list = em.createQuery(
                            "SELECT r FROM Raport r WHERE r.pracownik.id = :pid",
                            Raport.class)
                    .setParameter("pid", pracownikId)
                    .getResultList();
            logger.info("znajdzPoPracowniku() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoPracowniku() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoPracowniku() – EntityManager zamknięty");
        }
    }

    public List<Raport> znajdzPoSciezcePliku(String fileFragment) {
        logger.debug("znajdzPoSciezcePliku() – fileFragment={}", fileFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Raport> list = em.createQuery(
                            "SELECT r FROM Raport r WHERE LOWER(r.sciezkaPliku) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            Raport.class)
                    .setParameter("frag", fileFragment)
                    .getResultList();
            logger.info("znajdzPoSciezcePliku() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoSciezcePliku() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoSciezcePliku() – EntityManager zamknięty");
        }
    }

    /** Zamyka EntityManagerFactory. */
    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}
