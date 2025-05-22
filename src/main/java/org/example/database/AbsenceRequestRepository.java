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
    public void addRequest(AbsenceRequest request) {
        logger.debug("addRequest() - start, wniosek={}", request);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(request);
            tx.commit();
            logger.info("addRequest() - wniosek dodany: {}", request);
        } catch (Exception e) {
            logger.error("addRequest() - błąd podczas dodawania wniosku", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addRequest() - EM zamknięty");
        }
    }

    /**
     * Pobiera wniosek o nieobecność po jego ID.
     *
     * @param id identyfikator wniosku
     * @return znaleziony wniosek lub null
     */
    public AbsenceRequest findRequestById(int id) {
        logger.debug("findRequestById() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            AbsenceRequest w = em.find(AbsenceRequest.class, id);
            logger.info("findRequestById() - znaleziono: {}", w);
            return w;
        } catch (Exception e) {
            logger.error("findRequestById() - błąd podczas pobierania wniosku o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findRequestById() - EM zamknięty");
        }
    }

    /**
     * Pobiera wszystkie wnioski o nieobecność.
     *
     * @return lista wniosków (może być pusta)
     */
    public List<AbsenceRequest> downloadAllRequests() {
        logger.debug("downloadAllRequests() - start");
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery("SELECT w FROM AbsenceRequest w", AbsenceRequest.class)
                    .getResultList();
            logger.info("downloadAllRequests() - pobrano {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("downloadAllRequests() - błąd podczas pobierania wszystkich wniosków", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("downloadAllRequests() - EM zamknięty");
        }
    }

    /**
     * Usuwa wniosek o nieobecność o podanym ID.
     *
     * @param id identyfikator wniosku do usunięcia
     */
    public void deleteRequest(int id) {
        logger.debug("deleteRequest() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AbsenceRequest w = em.find(AbsenceRequest.class, id);
            if (w != null) {
                em.remove(w);
                logger.info("deleteRequest() - usunięto wniosek: {}", w);
            } else {
                logger.warn("deleteRequest() - brak wniosku o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("deleteRequest() - błąd podczas usuwania wniosku o id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("deleteRequest() - EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejący wniosek o nieobecność.
     *
     * @param request obiekt wniosku z zmienionymi danymi
     */
    public void updateRequest(AbsenceRequest request) {
        logger.debug("updateRequest() - start, wniosek={}", request);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(request);
            tx.commit();
            logger.info("updateRequest() - zaktualizowano wniosek: {}", request);
        } catch (Exception e) {
            logger.error("updateRequest() - błąd podczas aktualizacji wniosku", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateRequest() - EM zamknięty");
        }
    }

    // --- metody wyszukiwania ---

    /**
     * Pobiera wszystkie wnioski danego pracownika.
     *
     * @param employee obiekt pracownika
     * @return lista wniosków przypisanych do pracownika
     */
    public List<AbsenceRequest> findEmployeeRequests(Employee employee) {
        logger.debug("findEmployeeRequests() - employee={}", employee);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.pracownik = :employee",
                            AbsenceRequest.class
                    )
                    .setParameter("employee", employee)
                    .getResultList();
            logger.info("findEmployeeRequests() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findEmployeeRequests() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findEmployeeRequests() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski pracownika na podstawie jego ID.
     *
     * @param employeeId identyfikator pracownika
     * @return lista wniosków
     */
    public List<AbsenceRequest> findEmployeeRequestsById(int employeeId) {
        logger.debug("findEmployeeRequestsById() - employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.pracownik.id = :id",
                            AbsenceRequest.class
                    )
                    .setParameter("id", employeeId)
                    .getResultList();
            logger.info("findEmployeeRequestsById() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findEmployeeRequestsById() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findEmployeeRequestsById() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski o danym typie.
     *
     * @param requestType typ wniosku (np. "Urlop wypoczynkowy")
     * @return lista wniosków
     */
    public List<AbsenceRequest> findRequestsByType(String requestType) {
        logger.debug("findRequestsByType() - requestType={}", requestType);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.Typ_wniosku = :type",
                            AbsenceRequest.class
                    )
                    .setParameter("type", requestType)
                    .getResultList();
            logger.info("findRequestsByType() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsByType() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsByType() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski o danym statusie.
     *
     * @param status status wniosku (enum AbsenceRequest.StatusWniosku)
     * @return lista wniosków
     */
    public List<AbsenceRequest> findRequestsByStatus(AbsenceRequest.RequestStatus status) {
        logger.debug("findRequestsByStatus() - status={}", status);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.status = :status",
                            AbsenceRequest.class
                    )
                    .setParameter("status", status)
                    .getResultList();
            logger.info("findRequestsByStatus() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsByStatus() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsByStatus() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski rozpoczynające się w lub po podanej dacie.
     *
     * @param fromDate data początkowa
     * @return lista wniosków
     */
    public List<AbsenceRequest> findRequestsFromDate(Date fromDate) {
        logger.debug("findRequestsFromDate() - fromDate={}", fromDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.Data_rozpoczecia >= :fromDate",
                            AbsenceRequest.class
                    )
                    .setParameter("fromDate", fromDate)
                    .getResultList();
            logger.info("findRequestsFromDate() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsFromDate() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsFromDate() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski kończące się w lub przed podaną datą.
     *
     * @param toDate data końcowa
     * @return lista wniosków
     */
    public List<AbsenceRequest> findRequestsToDate(Date toDate) {
        logger.debug("findRequestsToDate() - toDate={}", toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.Data_zakonczenia <= :toDate",
                            AbsenceRequest.class
                    )
                    .setParameter("toDate", toDate)
                    .getResultList();
            logger.info("findRequestsToDate() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsToDate() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsToDate() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski mieszczące się w przedziale dat.
     *
     * @param fromDate data początkowa
     * @param toDate data końcowa
     * @return lista wniosków
     */
    public List<AbsenceRequest> findDateRangeRequests(Date fromDate, Date toDate) {
        logger.debug("findDateRangeRequests() - fromDate={}, toDate={}", fromDate, toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.Data_rozpoczecia >= :fromDate AND w.Data_zakonczenia <= :toDate",
                            AbsenceRequest.class
                    )
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
            logger.info("findDateRangeRequests() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findDateRangeRequests() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findDateRangeRequests() - EM zamknięty");
        }
    }

    /**
     * Pobiera wnioski nachodzące na podany przedział dat.
     *
     * @param fromDate data początkowa
     * @param toDate data końcowa
     * @return lista wniosków
     */
    public List<AbsenceRequest> findRequestsOverlappingDateRange(Date fromDate, Date toDate) {
        logger.debug("findRequestsOverlappingDateRange() - fromDate={}, toDate={}", fromDate, toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.Data_rozpoczecia <= :toDate AND w.Data_zakonczenia >= :fromDate",
                            AbsenceRequest.class
                    )
                    .setParameter("toDate", toDate)
                    .setParameter("fromDate", fromDate)
                    .getResultList();
            logger.info("findRequestsOverlappingDateRange() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsOverlappingDateRange() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsOverlappingDateRange() - EM zamknięty");
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
