/*
 * Classname: RaportRepository
 * Version information: 1.5
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TemporalType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Report;

import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Repozytorium zarządzające encjami Report w bazie danych.
 * Używa wspólnego EntityManagerFactory z EMFProvider.
 */
public class ReportRepository {
    private static final Logger logger = LogManager.getLogger(ReportRepository.class);
    private final EntityManagerFactory emf = EMFProvider.get();

    /**
     * Dodaje nowy raport do bazy.
     */
    public void addReport(Report report) {
        logger.debug("addReport() – start, report={}", report);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(report);
            tx.commit();
            logger.info("addReport() – raport dodany: {}", report);
        } catch (Exception e) {
            logger.error("addReport() – błąd podczas dodawania raportu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera raport o podanym identyfikatorze.
     */
    public Report findReportById(int id) {
        logger.debug("findReportById() – id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Report.class, id);
        } catch (Exception e) {
            logger.error("findReportById() – błąd id={}", id, e);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie raporty.
     */
    public List<Report> getAllReports() {
        logger.debug("getAllReports() – start");
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Report r", Report.class)
                    .getResultList();
        } catch (Exception e) {
            logger.error("getAllReports() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera raporty danego pracownika przypisane na konkretny dzień.
     */
    public List<Report> getEmployeeDayReport(int employeeId, LocalDate day) {
        logger.debug("getEmployeeDayReport() – employeeId={}, day={}", employeeId, day);
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Report r WHERE r.employee.id = :pid AND r.startDate = :day", Report.class)
                    .setParameter("pid", employeeId)
                    .setParameter("day", day)
                    .getResultList();
        } catch (Exception e) {
            logger.error("getEmployeeDayReport() – błąd dla employeeId={}, day={}", employeeId, day, e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    /**
     * Aktualizuje istniejący raport.
     */
    public void updateReport(Report report) {
        logger.debug("updateReport() – start, report={}", report);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(report);
            tx.commit();
            logger.info("updateReport() – raport zaktualizowany: {}", report);
        } catch (Exception e) {
            logger.error("updateReport() – błąd podczas aktualizacji raportu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Usuwa raport i powiązany plik.
     */
    public void removeReport(int id) {
        logger.debug("removeReport() – id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Report r = em.find(Report.class, id);
            if (r != null) {
                File file = new File(r.getFilePath());
                if (file.exists() && file.isFile() && !file.delete()) {
                    logger.warn("removeReport() – nie udało się usunąć pliku: {}", r.getFilePath());
                }
                em.remove(r);
                logger.info("removeReport() – raport usunięty: {}", id);
            } else {
                logger.warn("removeReport() – brak raportu o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeReport() – błąd przy usuwaniu id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    // =====================
    // Metody wyszukiwania
    // =====================

    public List<Report> findByType(String typeFragment) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Report r WHERE LOWER(r.reportType) LIKE LOWER(CONCAT('%',:frag,'%'))", Report.class)
                    .setParameter("frag", typeFragment)
                    .getResultList();
        } catch (Exception e) {
            logger.error("findByType() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Report> findByStartDate(Date startDate, Date endDate) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Report r WHERE r.startDate BETWEEN :start AND :end", Report.class)
                    .setParameter("start", startDate, TemporalType.DATE)
                    .setParameter("end", endDate, TemporalType.DATE)
                    .getResultList();
        } catch (Exception e) {
            logger.error("findByStartDate() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Report> findByEndDate(Date startDate, Date endDate) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Report r WHERE r.endDate BETWEEN :start AND :end", Report.class)
                    .setParameter("start", startDate, TemporalType.DATE)
                    .setParameter("end", endDate, TemporalType.DATE)
                    .getResultList();
        } catch (Exception e) {
            logger.error("findByEndDate() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Report> findByEmployee(int employeeId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Report r WHERE r.employee.id=:pid", Report.class)
                    .setParameter("pid", employeeId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("findByEmployee() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Report> findByFilePath(String fragment) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Report r WHERE LOWER(r.filePath) LIKE LOWER(CONCAT('%',:frag,'%'))", Report.class)
                    .setParameter("frag", fragment)
                    .getResultList();
        } catch (Exception e) {
            logger.error("findByFilePath() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public void close() {
    }
}


