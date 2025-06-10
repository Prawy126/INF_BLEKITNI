/*
 * Classname: ReportRepository
 * Version information: 1.5
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TemporalType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.EMFProvider;
import org.example.sys.Report;

import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Repozytorium zarządzające encjami Report w bazie danych.
 * Używa wspólnego EntityManagerFactory z EMFProvider.
 * Zapewnia operacje CRUD oraz wyszukiwanie raportów według różnych
 * kryteriów.
 */
public class ReportRepository {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą ReportRepository.
     */
    private static final Logger logger = LogManager.getLogger(
            ReportRepository.class);

    /**
     * Fabryka EntityManager współdzielona z EMFProvider.
     */
    private final EntityManagerFactory emf = EMFProvider.get();

    /**
     * Dodaje nowy raport do bazy.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param report obiekt raportu do zapisania
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
            logger.error("addReport() – błąd podczas dodawania raportu",
                    e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera raport o podanym identyfikatorze.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest wartość null.
     *
     * @param id identyfikator raportu
     * @return obiekt Report lub null, jeśli nie istnieje
     */
    public Report findReportById(int id) {
        logger.debug("findReportById() – id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Report report = em.find(Report.class, id);
            logger.info("findReportById() – znaleziono: {}", report);
            return report;
        } catch (Exception e) {
            logger.error("findReportById() – błąd id={}", id, e);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie raporty.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @return lista wszystkich raportów lub pusta lista w przypadku błędu
     */
    public List<Report> getAllReports() {
        logger.debug("getAllReports() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> reports = em.createQuery(
                            "SELECT r FROM Report r",
                            Report.class)
                    .getResultList();
            logger.info("getAllReports() – pobrano {} raportów",
                    reports.size());
            return reports;
        } catch (Exception e) {
            logger.error("getAllReports() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera raporty danego pracownika przypisane na konkretny dzień.
     * Wyszukuje raporty po identyfikatorze pracownika i dacie rozpoczęcia.
     *
     * @param employeeId identyfikator pracownika
     * @param day data raportu
     * @return lista raportów pracownika na dany dzień
     */
    public List<Report> getEmployeeDayReport(int employeeId, LocalDate day) {
        logger.debug("getEmployeeDayReport() – employeeId={}, day={}",
                employeeId, day);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> reports = em.createQuery(
                            "SELECT r FROM Report r " +
                                    "WHERE r.employee.id = :pid "
                                    + "AND r.startDate = :day",
                            Report.class)
                    .setParameter("pid", employeeId)
                    .setParameter("day", day)
                    .getResultList();
            logger.info("getEmployeeDayReport() " +
                            "– znaleziono {} raportów",
                    reports.size());
            return reports;
        } catch (Exception e) {
            logger.error("getEmployeeDayReport() " +
                            "– błąd dla employeeId={}, day={}",
                    employeeId, day, e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    /**
     * Aktualizuje istniejący raport.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param report zaktualizowany obiekt raportu
     */
    public void updateReport(Report report) {
        logger.debug("updateReport() – start, report={}", report);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(report);
            tx.commit();
            logger.info("updateReport() – raport zaktualizowany: {}",
                    report);
        } catch (Exception e) {
            logger.error("updateReport() " +
                    "– błąd podczas aktualizacji raportu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Usuwa raport i powiązany plik.
     * Operacja jest wykonywana w transakcji.
     * Jeśli istnieje plik powiązany z raportem, próbuje go usunąć.
     *
     * @param id identyfikator raportu do usunięcia
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
                    logger.warn("removeReport() " +
                                    "– nie udało się usunąć pliku: {}",
                            r.getFilePath());
                }
                em.remove(r);
                logger.info("removeReport() " +
                        "– raport usunięty: {}", id);
            } else {
                logger.warn("removeReport() " +
                        "– brak raportu o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeReport() " +
                    "– błąd przy usuwaniu id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje raporty po fragmencie typu raportu.
     * Wyszukiwanie jest wykonywane bez rozróżniania wielkości liter.
     *
     * @param typeFragment fragment typu raportu
     * @return lista pasujących raportów lub pusta lista
     */
    public List<Report> findByType(String typeFragment) {
        logger.debug("findByType() – typeFragment={}", typeFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> reports = em.createQuery(
                            "SELECT r FROM Report r " +
                                    "WHERE LOWER(r.reportType) "
                                    + "LIKE LOWER(CONCAT('%',:frag,'%'))",
                            Report.class)
                    .setParameter("frag", typeFragment)
                    .getResultList();
            logger.info("findByType() – znaleziono {} raportów",
                    reports.size());
            return reports;
        } catch (Exception e) {
            logger.error("findByType() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje raporty po dacie rozpoczęcia w podanym zakresie dat.
     * Wykorzystuje TemporalType.DATE do porównania samych dat.
     *
     * @param startDate początek zakresu dat (włącznie)
     * @param endDate koniec zakresu dat (włącznie)
     * @return lista raportów z datą rozpoczęcia w podanym zakresie
     */
    public List<Report> findByStartDate(Date startDate, Date endDate) {
        logger.debug("findByStartDate() " +
                        "– startDate={}, endDate={}",
                startDate, endDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> reports = em.createQuery(
                            "SELECT r FROM Report r WHERE r.startDate " +
                                    "BETWEEN :start AND :end",
                            Report.class)
                    .setParameter("start", startDate, TemporalType.DATE)
                    .setParameter("end", endDate, TemporalType.DATE)
                    .getResultList();
            logger.info("findByStartDate() " +
                    "– znaleziono {} raportów", reports.size());
            return reports;
        } catch (Exception e) {
            logger.error("findByStartDate() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje raporty po dacie zakończenia w podanym zakresie dat.
     * Wykorzystuje TemporalType.DATE do porównania samych dat.
     *
     * @param startDate początek zakresu dat (włącznie)
     * @param endDate koniec zakresu dat (włącznie)
     * @return lista raportów z datą zakończenia w podanym zakresie
     */
    public List<Report> findByEndDate(Date startDate, Date endDate) {
        logger.debug("findByEndDate() – startDate={}, endDate={}",
                startDate, endDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> reports = em.createQuery(
                            "SELECT r FROM Report r WHERE r.endDate " +
                                    "BETWEEN :start AND :end",
                            Report.class)
                    .setParameter("start", startDate, TemporalType.DATE)
                    .setParameter("end", endDate, TemporalType.DATE)
                    .getResultList();
            logger.info("findByEndDate() – znaleziono {} raportów",
                    reports.size());
            return reports;
        } catch (Exception e) {
            logger.error("findByEndDate() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje raporty powiązane z pracownikiem o podanym ID.
     * Zwraca wszystkie raporty utworzone przez danego pracownika.
     *
     * @param employeeId identyfikator pracownika
     * @return lista raportów pracownika
     */
    public List<Report> findByEmployee(int employeeId) {
        logger.debug("findByEmployee() – employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> reports = em.createQuery(
                            "SELECT r FROM Report r WHERE r.employee.id=:pid",
                            Report.class)
                    .setParameter("pid", employeeId)
                    .getResultList();
            logger.info("findByEmployee() " +
                    "– znaleziono {} raportów", reports.size());
            return reports;
        } catch (Exception e) {
            logger.error("findByEmployee() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje raporty po fragmencie ścieżki pliku.
     * Wyszukiwanie jest wykonywane bez rozróżniania wielkości liter.
     *
     * @param fragment fragment ścieżki pliku
     * @return lista raportów z pasującą ścieżką pliku
     */
    public List<Report> findByFilePath(String fragment) {
        logger.debug("findByFilePath() – fragment={}", fragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> reports = em.createQuery(
                            "SELECT r FROM Report r WHERE LOWER(r.filePath) "
                                    + "LIKE LOWER(CONCAT('%',:frag,'%'))",
                            Report.class)
                    .setParameter("frag", fragment)
                    .getResultList();
            logger.info("findByFilePath() " +
                    "– znaleziono {} raportów", reports.size());
            return reports;
        } catch (Exception e) {
            logger.error("findByFilePath() – błąd", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    /**
     * Zamyka wspólną fabrykę EMF (na zakończenie działania aplikacji).
     * Implementacja jest pusta, ponieważ korzystamy z EMFProvider.
     */
    public void close() {
    }
}