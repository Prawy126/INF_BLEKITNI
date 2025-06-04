/*
 * Classname: TechnicalIssueRepository
 * Version information: 2.0
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.TechnicalIssue;

import java.time.LocalDate;
import java.util.List;

/**
 * Repozytorium do obsługi zgłoszeń technicznych.
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz wyszukiwanie
 * zgłoszeń według różnych kryteriów.
 *
 * Korzysta z wspólnego EntityManagerFactory dostarczanego przez
 * {@link EMFProvider}.
 */
public class TechnicalIssueRepository {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą.
     */
    private static final Logger logger = LogManager.getLogger(
            TechnicalIssueRepository.class);

    /**
     * Wspólna (singleton) fabryka pozyskiwana z EMFProvider.
     */
    private static final EntityManagerFactory emf = EMFProvider.get();

    /**
     * Dodaje nowe zgłoszenie techniczne do bazy.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param issue obiekt zgłoszenia do zapisania
     */
    public void addIssue(TechnicalIssue issue) {
        logger.debug("addIssue() – start, issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(issue);
            tx.commit();
            logger.info("addIssue() – zgłoszenie dodane: {}", issue);
        } catch (Exception e) {
            logger.error("addIssue() " +
                    "– błąd podczas dodawania zgłoszenia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Znajduje zgłoszenie o podanym identyfikatorze.
     *
     * @param id identyfikator zgłoszenia
     * @return obiekt zgłoszenia lub null, jeśli nie istnieje
     */
    public TechnicalIssue findIssueById(int id) {
        logger.debug("findIssueById() – id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            TechnicalIssue issue = em.find(TechnicalIssue.class, id);
            logger.info("findIssueById() – znaleziono: {}", issue);
            return issue;
        } catch (Exception e) {
            logger.error("findIssueById() " +
                    "– błąd wyszukiwania id={}", id, e);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie zgłoszenia techniczne.
     *
     * @return lista wszystkich zgłoszeń lub pusta lista w przypadku błędu
     */
    public List<TechnicalIssue> getAllIssues() {
        logger.debug("getAllIssues()");
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> issues = em.createQuery(
                            "SELECT t FROM TechnicalIssue t",
                            TechnicalIssue.class)
                    .getResultList();
            logger.info("getAllIssues() " +
                    "– pobrano {} zgłoszeń", issues.size());
            return issues;
        } catch (Exception e) {
            logger.error("getAllIssues() " +
                    "– błąd pobierania zgłoszeń", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Aktualizuje istniejące zgłoszenie techniczne.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param issue zaktualizowany obiekt zgłoszenia
     */
    public void updateIssue(TechnicalIssue issue) {
        logger.debug("updateIssue() – issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(issue);
            tx.commit();
            logger.info("updateIssue() " +
                    "– zgłoszenie zaktualizowane: {}", issue);
        } catch (Exception e) {
            logger.error("updateIssue() " +
                    "– błąd podczas aktualizacji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Usuwa istniejące zgłoszenie techniczne.
     * Operacja jest wykonywana w transakcji.
     * Jeśli zgłoszenie nie istnieje, operacja jest logowana jako ostrzeżenie.
     *
     * @param issue obiekt zgłoszenia do usunięcia
     */
    public void removeIssue(TechnicalIssue issue) {
        logger.debug("removeIssue() – issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TechnicalIssue managed
                    = em.find(TechnicalIssue.class, issue.getId());
            if (managed != null) {
                em.remove(managed);
                logger.info("removeIssue() " +
                        "– zgłoszenie usunięte: {}", issue);
            } else {
                logger.warn("removeIssue() " +
                        "– brak rekordu o id={}", issue.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeIssue() – błąd podczas usuwania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje zgłoszenia według fragmentu typu.
     * Wyszukiwanie jest wykonywane bez rozróżniania wielkości liter.
     *
     * @param typeFragment fragment typu zgłoszenia
     * @return lista pasujących zgłoszeń lub pusta lista
     */
    public List<TechnicalIssue> findByType(String typeFragment) {
        logger.debug("findByType() – fragment={}", typeFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> issues = em.createQuery(
                            "SELECT t FROM TechnicalIssue t "
                                    + "WHERE LOWER(t.type) " +
                                    "LIKE LOWER(CONCAT('%', :frag, '%'))",
                            TechnicalIssue.class)
                    .setParameter("frag", typeFragment)
                    .getResultList();
            logger.info("findByType() " +
                    "– znaleziono {} zgłoszeń", issues.size());
            return issues;
        } catch (Exception e) {
            logger.error("findByType() – błąd wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje zgłoszenia według zakresu dat.
     * Zwraca zgłoszenia z datą złożenia w podanym przedziale.
     *
     * @param start początek zakresu dat (włącznie)
     * @param end koniec zakresu dat (włącznie)
     * @return lista zgłoszeń w podanym zakresie dat lub pusta lista
     */
    public List<TechnicalIssue> findByDate(
            LocalDate start,
            LocalDate end
    ) {
        logger.debug("findByDate() – {} – {}", start, end);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> issues = em.createQuery(
                            "SELECT t FROM TechnicalIssue t "
                                    + "WHERE t.dateSubmitted " +
                                    "BETWEEN :start AND :end",
                            TechnicalIssue.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
            logger.info("findByDate() " +
                    "– znaleziono {} zgłoszeń", issues.size());
            return issues;
        } catch (Exception e) {
            logger.error("findByDate() – błąd wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje zgłoszenia o podanym statusie.
     *
     * @param status status zgłoszenia
     * @return lista zgłoszeń o podanym statusie lub pusta lista
     */
    public List<TechnicalIssue> findByStatus(String status) {
        logger.debug("findByStatus() – status={}", status);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> issues = em.createQuery(
                            "SELECT t FROM TechnicalIssue t " +
                                    "WHERE t.status = :status",
                            TechnicalIssue.class)
                    .setParameter("status", status)
                    .getResultList();
            logger.info("findByStatus() " +
                    "– znaleziono {} zgłoszeń", issues.size());
            return issues;
        } catch (Exception e) {
            logger.error("findByStatus() – błąd wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje zgłoszenia złożone przez pracownika o podanym ID.
     *
     * @param employeeId identyfikator pracownika
     * @return lista zgłoszeń pracownika lub pusta lista
     */
    public List<TechnicalIssue> findByEmployee(int employeeId) {
        logger.debug("findByEmployee() – employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> issues = em.createQuery(
                            "SELECT t FROM TechnicalIssue t " +
                                    "WHERE t.employee.id = :pid",
                            TechnicalIssue.class)
                    .setParameter("pid", employeeId)
                    .getResultList();
            logger.info("findByEmployee() " +
                    "– znaleziono {} zgłoszeń", issues.size());
            return issues;
        } catch (Exception e) {
            logger.error("findByEmployee() – błąd wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Metoda zostaje, by spełnić konwencję, ale **nie** zamyka wspólnej
     * fabryki. Fabrykę zamykamy raz ― w `EMFProvider.close()` przy
     * wyłączaniu aplikacji.
     */
    public void close() {
        // brak implementacji; EMF zamyka się
        // w EMFProvider.close() podczas shutdown
    }
}