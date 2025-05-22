/*
 * Classname: TechnicalIssueRepository
 * Version information: 1.2
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
import org.example.sys.TechnicalIssue;

import java.time.LocalDate;
import java.util.List;

/**
 * Repozytorium do obsługi zgłoszeń technicznych.
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz wyszukiwanie zgłoszeń.
 */
public class TechnicalIssueRepository {
    private static final Logger logger = LogManager.getLogger(TechnicalIssueRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory dla persistence unit "myPU".
     */
    public TechnicalIssueRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono TechnicalIssueRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowe zgłoszenie techniczne do bazy.
     *
     * @param issue obiekt TechnicalIssue do zapisania
     */
    public void dodajZgloszenie(TechnicalIssue issue) {
        logger.debug("dodajZgloszenie() – start, issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(issue);
            tx.commit();
            logger.info("dodajZgloszenie() – zgłoszenie dodane: {}", issue);
        } catch (Exception e) {
            logger.error("dodajZgloszenie() – błąd podczas dodawania zgłoszenia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajZgloszenie() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera zgłoszenie techniczne o podanym identyfikatorze.
     *
     * @param id identyfikator zgłoszenia
     * @return obiekt TechnicalIssue lub null, jeśli nie znaleziono
     */
    public TechnicalIssue znajdzZgloszeniePoId(int id) {
        logger.debug("znajdzZgloszeniePoId() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            TechnicalIssue t = em.find(TechnicalIssue.class, id);
            logger.info("znajdzZgloszeniePoId() – znaleziono: {}", t);
            return t;
        } catch (Exception e) {
            logger.error("znajdzZgloszeniePoId() – błąd podczas pobierania zgłoszenia id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzZgloszeniePoId() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie zgłoszenia techniczne z bazy.
     *
     * @return lista obiektów TechnicalIssue lub pusta lista w przypadku błędu
     */
    public List<TechnicalIssue> pobierzWszystkieZgloszenia() {
        logger.debug("pobierzWszystkieZgloszenia() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> list = em.createQuery(
                            "SELECT t FROM TechnicalIssue t", TechnicalIssue.class)
                    .getResultList();
            logger.info("pobierzWszystkieZgloszenia() – pobrano {} zgłoszeń", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkieZgloszenia() – błąd podczas pobierania zgłoszeń", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieZgloszenia() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje istniejące zgłoszenie techniczne (np. zmienia status lub opis).
     *
     * @param issue obiekt TechnicalIssue do zaktualizowania
     */
    public void aktualizujZgloszenie(TechnicalIssue issue) {
        logger.debug("aktualizujZgloszenie() – start, issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(issue);
            tx.commit();
            logger.info("aktualizujZgloszenie() – zgłoszenie zaktualizowane: {}", issue);
        } catch (Exception e) {
            logger.error("aktualizujZgloszenie() – błąd podczas aktualizacji zgłoszenia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujZgloszenie() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa zgłoszenie techniczne z bazy.
     *
     * @param issue obiekt TechnicalIssue do usunięcia
     */
    public void usunZgloszenie(TechnicalIssue issue) {
        logger.debug("usunZgloszenie() – start, issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TechnicalIssue managed = em.find(TechnicalIssue.class, issue.getId());
            if (managed != null) {
                em.remove(managed);
                logger.info("usunZgloszenie() – usunięto zgłoszenie: {}", managed);
            } else {
                logger.warn("usunZgloszenie() – brak zgłoszenia o id={}", issue.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunZgloszenie() – błąd podczas usuwania zgłoszenia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunZgloszenie() – EntityManager zamknięty");
        }
    }

    // =========================================================
    // === Poniżej metody wyszukiwania po różnych kryteriach ===
    // =========================================================

    /**
     * Wyszukuje zgłoszenia, których typ zawiera podany fragment (bez uwzględniania wielkości liter).
     *
     * @param typFragment fragment tekstu pola type
     * @return lista dopasowanych zgłoszeń lub pusta lista
     */
    public List<TechnicalIssue> znajdzPoTypie(String typFragment) {
        logger.debug("znajdzPoTypie() – typFragment={}", typFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> list = em.createQuery(
                            "SELECT t FROM TechnicalIssue t WHERE LOWER(t.type) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            TechnicalIssue.class)
                    .setParameter("frag", typFragment)
                    .getResultList();
            logger.info("znajdzPoTypie() – znaleziono {} zgłoszeń", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoTypie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoTypie() – EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje zgłoszenia z datą zgłoszenia w podanym przedziale.
     *
     * @param start początek przedziału (inclusive)
     * @param end   koniec przedziału (inclusive)
     * @return lista dopasowanych zgłoszeń lub pusta lista
     */
    public List<TechnicalIssue> znajdzPoDacie(LocalDate start, LocalDate end) {
        logger.debug("znajdzPoDacie() – start={}, end={}", start, end);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> list = em.createQuery(
                            "SELECT t FROM TechnicalIssue t WHERE t.dateSubmitted BETWEEN :start AND :end",
                            TechnicalIssue.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
            logger.info("znajdzPoDacie() – znaleziono {} zgłoszeń", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoDacie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoDacie() – EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje zgłoszenia o dokładnie podanym statusie.
     *
     * @param status status zgłoszenia
     * @return lista dopasowanych zgłoszeń lub pusta lista
     */
    public List<TechnicalIssue> znajdzPoStatusie(String status) {
        logger.debug("znajdzPoStatusie() – status={}", status);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> list = em.createQuery(
                            "SELECT t FROM TechnicalIssue t WHERE t.status = :status",
                            TechnicalIssue.class)
                    .setParameter("status", status)
                    .getResultList();
            logger.info("znajdzPoStatusie() – znaleziono {} zgłoszeń", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoStatusie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoStatusie() – EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje zgłoszenia zgłoszone przez konkretnego pracownika.
     *
     * @param pracownikId identyfikator pracownika
     * @return lista dopasowanych zgłoszeń lub pusta lista
     */
    public List<TechnicalIssue> znajdzPoPracowniku(int pracownikId) {
        logger.debug("znajdzPoPracowniku() – pracownikId={}", pracownikId);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> list = em.createQuery(
                            "SELECT t FROM TechnicalIssue t WHERE t.employee.id = :pid",
                            TechnicalIssue.class)
                    .setParameter("pid", pracownikId)
                    .getResultList();
            logger.info("znajdzPoPracowniku() – znaleziono {} zgłoszeń", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoPracowniku() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoPracowniku() – EntityManager zamknięty");
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