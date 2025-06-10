/*
 * Classname: AbsenceRequestRepository
 * Version information: 1.0
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.EMFProvider;
import org.example.sys.AbsenceRequest;
import org.example.sys.Employee;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Repozytorium do zarządzania wnioskami o nieobecność.
 * Zapewnia operacje CRUD oraz metody wyszukiwania wniosków o nieobecność
 * według różnych kryteriów. Wykorzystuje EntityManager do komunikacji
 * z bazą danych i obsługuje transakcje.
 */
public class AbsenceRequestRepository {

    private static final Logger logger
            = LogManager.getLogger(AbsenceRequestRepository.class);

    /**
     * Konstruktor domyślny.
     * Inicjalizuje repozytorium do zarządzania wnioskami o nieobecność.
     * Operacja jest logowana na poziomie INFO.
     */
    public AbsenceRequestRepository() {
        logger.info("Utworzono AbsenceRequestRepository");
    }

    /**
     * Dodaje nowy wniosek o nieobecność do bazy danych.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana, a wyjątek jest logowany.
     *
     * @param request obiekt wniosku o nieobecność do zapisania
     */
    public void addRequest(AbsenceRequest request) {
        logger.debug("addRequest() - start, request={}", request);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(request);
            tx.commit();
            logger.info("addRequest() - wniosek dodany:" +
                    " {}", request);
        } catch (Exception e) {
            logger.error("addRequest() " +
                    "- błąd podczas dodawania wniosku", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addRequest() - EM zamknięty");
        }
    }

    /**
     * Pobiera wniosek o nieobecność na podstawie jego ID.
     * W przypadku błędu, wyjątek jest logowany
     * i zwracana jest wartość null.
     *
     * @param id identyfikator wniosku
     * @return znaleziony obiekt wniosku lub null,
     * jeśli wniosek nie istnieje lub wystąpił błąd
     */
    public AbsenceRequest findRequestById(int id) {
        logger.debug("findRequestById() - start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            AbsenceRequest w = em.find(AbsenceRequest.class, id);
            logger.info("findRequestById() - znaleziono: {}", w);
            return w;
        } catch (Exception e) {
            logger.error("findRequestById() " +
                    "- błąd podczas pobierania wniosku o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findRequestById() - EM zamknięty");
        }
    }

    /**
     * Pobiera wszystkie wnioski
     * o nieobecność z bazy danych.
     * W przypadku błędu, wyjątek
     * jest logowany i zwracana jest pusta lista.
     *
     * @return lista wszystkich wniosków lub pusta lista w przypadku błędu
     */
    public List<AbsenceRequest> getAllRequests() {
        logger.debug("getAllRequests() - start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery("SELECT w FROM AbsenceRequest w",
                            AbsenceRequest.class)
                    .getResultList();
            logger.info("getAllRequests() - pobrano {} wniosków",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllRequests() " +
                    "- błąd podczas pobierania wszystkich wniosków", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("getAllRequests() - EM zamknięty");
        }
    }

    /**
     * Usuwa wniosek o nieobecność o podanym ID.
     * Operacja jest wykonywana w transakcji.
     * Jeśli wniosek nie istnieje,
     * operacja jest logowana jako ostrzeżenie.
     * W przypadku błędu, transakcja
     * jest wycofywana, a wyjątek jest logowany.
     *
     * @param id identyfikator wniosku do usunięcia
     */
    public void removeRequest(int id) {
        logger.debug("removeRequest() - start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AbsenceRequest w = em.find(AbsenceRequest.class, id);
            if (w != null) {
                em.remove(w);
                logger.info("removeRequest() " +
                        "- usunięto wniosek: {}", w);
            } else {
                logger.warn("removeRequest() " +
                        "- brak wniosku o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeRequest() " +
                    "- błąd podczas usuwania wniosku o id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeRequest() - EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejący wniosek o nieobecność.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja
     * jest wycofywana, a wyjątek jest logowany.
     *
     * @param request zaktualizowany obiekt wniosku o nieobecność
     */
    public void updateRequest(AbsenceRequest request) {
        logger.debug("updateRequest() - start, request={}", request);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(request);
            tx.commit();
            logger.info("updateRequest() " +
                    "- zaktualizowano wniosek: {}", request);
        } catch (Exception e) {
            logger.error("updateRequest() " +
                    "- błąd podczas aktualizacji wniosku", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateRequest() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje wnioski o nieobecność
     * powiązane z danym pracownikiem.
     * W przypadku błędu, wyjątek jest
     * logowany i zwracana jest pusta lista.
     *
     * @param employee pracownik, którego wnioski mają być wyszukane
     * @return lista wniosków pracownika lub pusta lista w przypadku błędu
     */
    public List<AbsenceRequest> findEmployeeRequests(Employee employee) {
        logger.debug("findEmployeeRequests() - employee={}", employee);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w " +
                                    "WHERE w.employee = :employee",
                            AbsenceRequest.class
                    )
                    .setParameter("employee", employee)
                    .getResultList();
            logger.info("findEmployeeRequests() " +
                    "- znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findEmployeeRequests() " +
                    "- błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findEmployeeRequests() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje wnioski o nieobecność
     * powiązane z pracownikiem o podanym ID.
     * W przypadku błędu, wyjątek
     * jest logowany i zwracana jest pusta lista.
     *
     * @param employeeId identyfikator pracownika
     * @return lista wniosków pracownika lub pusta lista w przypadku błędu
     */
    public List<AbsenceRequest> findEmployeeRequestsById(int employeeId) {
        logger.debug("findEmployeeRequestsById() " +
                "- employeeId={}", employeeId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w " +
                                    "WHERE w.employee.id = :id",
                            AbsenceRequest.class
                    )
                    .setParameter("id", employeeId)
                    .getResultList();
            logger.info("findEmployeeRequestsById() " +
                    "- znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findEmployeeRequestsById() " +
                    "- błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findEmployeeRequestsById() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje wnioski o nieobecność
     * określonego typu.
     * W przypadku błędu, wyjątek jest
     * logowany i zwracana jest pusta lista.
     *
     * @param requestType typ wniosku o nieobecność
     *                    (np. "urlop wypoczynkowy", "zwolnienie lekarskie")
     * @return lista wniosków danego typu lub pusta lista w przypadku błędu
     */
    public List<AbsenceRequest> findRequestsByType(String requestType) {
        logger.debug("findRequestsByType() - requestType={}",
                requestType);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w " +
                                    "WHERE w.type = :type",
                            AbsenceRequest.class
                    )
                    .setParameter("type", requestType)
                    .getResultList();
            logger.info("findRequestsByType() " +
                    "- znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsByType() " +
                    "- błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsByType() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje wnioski o nieobecność
     * o określonym statusie.
     * W przypadku błędu, wyjątek jest logowany
     * i zwracana jest pusta lista.
     *
     * @param status status wniosku (np. PENDING, APPROVED, REJECTED)
     * @return lista wniosków o danym statusie
     * lub pusta lista w przypadku błędu
     */
    public List<AbsenceRequest> findRequestsByStatus(
            AbsenceRequest.RequestStatus status
    ) {
        logger.debug("findRequestsByStatus() - status={}", status);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w " +
                                    "WHERE w.status = :status",
                            AbsenceRequest.class
                    )
                    .setParameter("status", status)
                    .getResultList();
            logger.info("findRequestsByStatus() " +
                    "- znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsByStatus() " +
                    "- błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsByStatus() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje wnioski o nieobecność, których data
     * początkowa jest większa lub równa podanej dacie.
     * W przypadku błędu, wyjątek jest logowany
     * i zwracana jest pusta lista.
     *
     * @param fromDate data początkowa do porównania
     * @return lista wniosków z datą od >= fromDate
     * lub pusta lista w przypadku błędu
     */
    public List<AbsenceRequest> findRequestsFromDate(Date fromDate) {
        logger.debug("findRequestsFromDate() - fromDate={}", fromDate);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w " +
                                    "WHERE w.fromDate >= :fromDate",
                            AbsenceRequest.class
                    )
                    .setParameter("fromDate", fromDate)
                    .getResultList();
            logger.info("findRequestsFromDate() " +
                    "- znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsFromDate() " +
                    "- błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsFromDate() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje wnioski o nieobecność, których data końcowa
     * jest mniejsza lub równa podanej dacie.
     * W przypadku błędu, wyjątek jest logowany i zwracana
     * jest pusta lista.
     *
     * @param toDate data końcowa do porównania
     * @return lista wniosków z datą do <= toDate lub pusta
     * lista w przypadku błędu
     */
    public List<AbsenceRequest> findRequestsToDate(Date toDate) {
        logger.debug("findRequestsToDate() - toDate={}", toDate);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w " +
                                    "WHERE w.toDate <= :toDate",
                            AbsenceRequest.class
                    )
                    .setParameter("toDate", toDate)
                    .getResultList();
            logger.info("findRequestsToDate() " +
                    "- znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsToDate() " +
                    "- błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsToDate() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje wnioski o nieobecność, które zawierają się całkowicie
     * w podanym zakresie dat.
     * Wyszukuje wnioski, dla których
     * fromDate >= podany fromDate i toDate <= podany toDate.
     * W przypadku błędu, wyjątek jest logowany
     * i zwracana jest pusta lista.
     *
     * @param fromDate początek okresu
     * @param toDate koniec okresu
     * @return lista wniosków zawierających się w podanym
     * zakresie dat lub pusta lista w przypadku błędu
     */
    public List<AbsenceRequest> findDateRangeRequests(
            Date fromDate,
            Date toDate
    ) {
        logger.debug("findDateRangeRequests() " +
                "- fromDate={}, toDate={}", fromDate, toDate);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w " +
                                    "WHERE w.fromDate >= :fromDate " +
                                    "AND w.toDate <= :toDate",
                            AbsenceRequest.class
                    )
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
            logger.info("findDateRangeRequests() " +
                    "- znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findDateRangeRequests() " +
                    "- błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findDateRangeRequests() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje wnioski o nieobecność, które nakładają się
     * na podany zakres dat.
     * Wyszukuje wnioski, gdzie najwcześniejsza
     * data końcowa jest >= podany fromDate
     * i najpóźniejsza data początkowa jest <= podany toDate.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @param fromDate początek okresu
     * @param toDate koniec okresu
     * @return lista wniosków nakładających się na podany zakres
     * dat lub pusta lista w przypadku błędu
     */
    public List<AbsenceRequest> findRequestsOverlappingDateRange(
            Date fromDate,
            Date toDate
    ) {
        logger.debug("findRequestsOverlappingDateRange() " +
                "- fromDate={}, toDate={}", fromDate, toDate);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<AbsenceRequest> list = em
                    .createQuery(
                            "SELECT w FROM AbsenceRequest w " +
                                    "WHERE w.fromDate <= :toDate " +
                                    "AND w.toDate >= :fromDate",
                            AbsenceRequest.class
                    )
                    .setParameter("toDate", toDate)
                    .setParameter("fromDate", fromDate)
                    .getResultList();
            logger.info("findRequestsOverlappingDateRange() " +
                    "- znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsOverlappingDateRange() " +
                    "- błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsOverlappingDateRange() - EM zamknięty");
        }
    }

    /**
     * Zamyka EntityManagerFactory (używając EMFProvider).
     * Metoda powinna być wywołana przy zamykaniu aplikacji
     * lub gdy repozytorium nie jest już potrzebne.
     */
    public void close() {
        // Implementacja pozostawiona pusta, ponieważ korzystamy z EMFProvider
    }
}