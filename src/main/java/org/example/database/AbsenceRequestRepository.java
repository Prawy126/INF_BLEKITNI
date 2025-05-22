/*
 * Classname: AbsenceRequestRepository
 * Version information: 1.5
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

import org.example.sys.AbsenceRequest;
import org.example.sys.Employee;

import java.util.Date;
import java.util.List;
import java.util.Collections;

public class AbsenceRequestRepository {
    private static final Logger logger = LogManager.getLogger(AbsenceRequestRepository.class);
    private final EntityManagerFactory emf;

    /** Konstruktor inicjalizujący EntityManagerFactory. */
    public AbsenceRequestRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono AbsenceRequestRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowy wniosek o nieobecność.
     *
     * @param request obiekt wniosku do dodania
     */
    public void addApplication(AbsenceRequest request) {
        logger.debug("addApplication() - start, request={}", request);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(request);
            tx.commit();
            logger.info("addApplication() - wniosek dodany: {}", request);
        } catch (Exception e) {
            logger.error("addApplication() - błąd podczas dodawania wniosku", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addApplication() - EM zamknięty");
        }
    }

    /**
     * Pobiera wniosek o nieobecność po jego ID.
     *
     * @param id identyfikator wniosku
     * @return znaleziony wniosek lub null
     */
    public AbsenceRequest findApplicationById(int id) {
        logger.debug("findApplicationById() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            AbsenceRequest w = em.find(AbsenceRequest.class, id);
            logger.info("findApplicationById() - znaleziono: {}", w);
            return w;
        } catch (Exception e) {
            logger.error("findApplicationById() - błąd podczas pobierania wniosku o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findApplicationById() - EM zamknięty");
        }
    }

    /**
     * Pobiera wszystkie wnioski o nieobecność.
     *
     * @return lista wniosków (może być pusta)
     */
    public List<AbsenceRequest> downloadAllApplications() {
        logger.debug("downloadAllApplications() - start");
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery("SELECT w FROM AbsenceRequest w", AbsenceRequest.class)
                    .getResultList();
            logger.info("downloadAllApplications() - pobrano {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("downloadAllApplications() - błąd podczas pobierania wszystkich wniosków", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("downloadAllApplications() - EM zamknięty");
        }
    }

    /**
     * Usuwa wniosek o nieobecność o podanym ID.
     *
     * @param id identyfikator wniosku do usunięcia
     */
    public void deleteApplication(int id) {
        logger.debug("deleteApplication() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AbsenceRequest w = em.find(AbsenceRequest.class, id);
            if (w != null) {
                em.remove(w);
                logger.info("deleteApplication() - usunięto wniosek: {}", w);
            } else {
                logger.warn("deleteApplication() - brak wniosku o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("deleteApplication() - błąd podczas usuwania wniosku o id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("deleteApplication() - EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejący wniosek o nieobecność.
     *
     * @param request obiekt wniosku z zmienionymi danymi
     */
    public void updateApplication(AbsenceRequest request) {
        logger.debug("updateApplication() - start, request={}", request);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(request);
            tx.commit();
            logger.info("updateApplication() - zaktualizowano wniosek: {}", request);
        } catch (Exception e) {
            logger.error("updateApplication() - błąd podczas aktualizacji wniosku", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateApplication() - EM zamknięty");
        }
    }

    // --- metody wyszukiwania ---

    /**
     * Pobiera wszystkie wnioski danego pracownika.
     *
     * @param employee obiekt pracownika
     * @return lista wniosków przypisanych do pracownika
     */
    public List<AbsenceRequest> findEmployeeApplications(Employee employee) {
        logger.debug("findEmployeeApplications() - employee={}", employee);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.pracownik = :employee",
                            AbsenceRequest.class
                    )
                    .setParameter("employee", employee)
                    .getResultList();
            logger.info("findEmployeeApplications() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findEmployeeApplications() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findEmployeeApplications() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski pracownika na podstawie jego ID.
     *
     * @param employeeId identyfikator pracownika
     * @return lista wniosków
     */
    public List<AbsenceRequest> findEmployeeApplicationsById(int employeeId) {
        logger.debug("findEmployeeApplicationsById() - employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.pracownik.id = :id",
                            AbsenceRequest.class
                    )
                    .setParameter("id", employeeId)
                    .getResultList();
            logger.info("findEmployeeApplicationsById() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findEmployeeApplicationsById() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findEmployeeApplicationsById() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski o danym typie.
     *
     * @param applicationType typ wniosku (np. "Urlop wypoczynkowy")
     * @return lista wniosków
     */
    public List<AbsenceRequest> findApplicationsByType(String applicationType) {
        logger.debug("findApplicationsByType() - applicationType={}", applicationType);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.typWniosku = :type",
                            AbsenceRequest.class
                    )
                    .setParameter("type", applicationType)
                    .getResultList();
            logger.info("findApplicationsByType() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findApplicationsByType() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findApplicationsByType() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski o danym statusie.
     *
     * @param status status wniosku (enum AbsenceRequest.StatusWniosku)
     * @return lista wniosków
     */
    public List<AbsenceRequest> findApplicationsByStatus(AbsenceRequest.ApplicationStatus status) {
        logger.debug("findApplicationsByStatus() - status={}", status);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.status = :status",
                            AbsenceRequest.class
                    )
                    .setParameter("status", status)
                    .getResultList();
            logger.info("findApplicationsByStatus() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findApplicationsByStatus() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findApplicationsByStatus() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski rozpoczynające się w lub po podanej dacie.
     *
     * @param fromDate data początkowa
     * @return lista wniosków
     */
    public List<AbsenceRequest> findApplicationsFromDate(Date fromDate) {
        logger.debug("findApplicationsFromDate() - fromDate={}", fromDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.dataRozpoczecia >= :fromDate",
                            AbsenceRequest.class
                    )
                    .setParameter("fromDate", fromDate)
                    .getResultList();
            logger.info("findApplicationsFromDate() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findApplicationsFromDate() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findApplicationsFromDate() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski kończące się w lub przed podaną datą.
     *
     * @param toDate data końcowa
     * @return lista wniosków
     */
    public List<AbsenceRequest> findApplicationsToDate(Date toDate) {
        logger.debug("findApplicationsToDate() - toDate={}", toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.dataZakonczenia <= :toDate",
                            AbsenceRequest.class
                    )
                    .setParameter("toDate", toDate)
                    .getResultList();
            logger.info("findApplicationsToDate() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findApplicationsToDate() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findApplicationsToDate() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski mieszczące się w przedziale dat.
     *
     * @param fromDate data początkowa
     * @param toDate data końcowa
     * @return lista wniosków
     */
    public List<AbsenceRequest> findDateRangeApplications(Date fromDate, Date toDate) {
        logger.debug("findDateRangeApplications() - fromDate={}, toDate={}", fromDate, toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.dataRozpoczecia >= :fromDate AND w.dataZakonczenia <= :toDate",
                            AbsenceRequest.class
                    )
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
            logger.info("findDateRangeApplications() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findDateRangeApplications() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findDateRangeApplications() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski nachodzące na podany przedział dat.
     *
     * @param fromDate data początkowa
     * @param toDate data końcowa
     * @return lista wniosków
     */
    public List<AbsenceRequest> findApplicationsOverlappingDateRange(Date fromDate, Date toDate) {
        logger.debug("findApplicationsOverlappingDateRange() - fromDate={}, toDate={}", fromDate, toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.dataRozpoczecia <= :toDate AND w.dataZakonczenia >= :fromDate",
                            AbsenceRequest.class
                    )
                    .setParameter("toDate", toDate)
                    .setParameter("fromDate", fromDate)
                    .getResultList();
            logger.info("findApplicationsOverlappingDateRange() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findApplicationsOverlappingDateRange() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findApplicationsOverlappingDateRange() - EM zamknięty");
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
