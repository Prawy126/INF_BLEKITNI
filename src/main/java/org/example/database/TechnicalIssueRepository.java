/*
 * Classname: TechnicalIssueRepository
 * Version information: 2.0
 * Date: 2025-05-30
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
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz wyszukiwanie zgłoszeń.
 *
 * Korzysta z wspólnego EntityManagerFactory dostarczanego przez {@link EMFProvider}.
 */
public class TechnicalIssueRepository {

    private static final Logger logger = LogManager.getLogger(TechnicalIssueRepository.class);

    /** Wspólna (singleton) fabryka pozyskiwana z EMFProvider. */
    private static final EntityManagerFactory emf = EMFProvider.get();

    /* =========================  OPERACJE CRUD  ========================= */

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
            logger.error("addIssue() – błąd podczas dodawania zgłoszenia", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    public TechnicalIssue findIssueById(int id) {
        logger.debug("findIssueById() – id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(TechnicalIssue.class, id);
        } finally {
            em.close();
        }
    }

    public List<TechnicalIssue> getAllIssues() {
        logger.debug("getAllIssues()");
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM TechnicalIssue t", TechnicalIssue.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void updateIssue(TechnicalIssue issue) {
        logger.debug("updateIssue() – issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(issue);
            tx.commit();
        } catch (Exception e) {
            logger.error("updateIssue() – błąd podczas aktualizacji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    public void removeIssue(TechnicalIssue issue) {
        logger.debug("removeIssue() – issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TechnicalIssue managed = em.find(TechnicalIssue.class, issue.getId());
            if (managed != null) {
                em.remove(managed);
            } else {
                logger.warn("removeIssue() – brak rekordu o id={}", issue.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeIssue() – błąd podczas usuwania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /* =========================  WYSZUKIWANIE  ========================= */

    public List<TechnicalIssue> findByType(String typeFragment) {
        logger.debug("findByType() – fragment={}", typeFragment);
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM TechnicalIssue t " +
                                    "WHERE LOWER(t.type) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            TechnicalIssue.class)
                    .setParameter("frag", typeFragment)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<TechnicalIssue> findByDate(LocalDate start, LocalDate end) {
        logger.debug("findByDate() – {} – {}", start, end);
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM TechnicalIssue t " +
                                    "WHERE t.dateSubmitted BETWEEN :start AND :end",
                            TechnicalIssue.class)
                    .setParameter("start", start)
                    .setParameter("end",   end)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<TechnicalIssue> findByStatus(String status) {
        logger.debug("findByStatus() – status={}", status);
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM TechnicalIssue t WHERE t.status = :status",
                            TechnicalIssue.class)
                    .setParameter("status", status)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<TechnicalIssue> findByEmployee(int employeeId) {
        logger.debug("findByEmployee() – employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM TechnicalIssue t WHERE t.employee.id = :pid",
                            TechnicalIssue.class)
                    .setParameter("pid", employeeId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /* =================================================================== */

    /**
     * Metoda zostaje, by spełnić konwencję, ale **nie** zamyka wspólnej fabryki.
     * Fabrykę zamykamy raz ― w `EMFProvider.close()` przy wyłączaniu aplikacji.
     */
    public void close() {
        logger.debug("TechnicalIssueRepository.close() – nic do zamknięcia (EMF współdzielone)");
    }
}
