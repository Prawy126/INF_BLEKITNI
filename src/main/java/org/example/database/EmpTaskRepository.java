/*
 * Classname: EmpTaskRepository
 * Version information: 1.2
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.EmpTask;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * Repozytorium zarządzające zadaniami w bazie danych.
 * Umożliwia operacje CRUD na zadaniach oraz ich wyszukiwanie
 * według różnych kryteriów.
 */
public class EmpTaskRepository implements AutoCloseable {
    /**
     * Logger do rejestrowania zdarzeń związanych z klasą EmpTaskRepository.
     */
    private static final Logger logger = LogManager.getLogger(
            EmpTaskRepository.class);

    /**
     * Domyślny konstruktor – korzysta ze wspólnego EMF z EMFProvider.
     * Operacja jest logowana na poziomie INFO.
     */
    public EmpTaskRepository() {
        logger.info("Utworzono EmpTaskRepository," +
                " EMF={}", EMFProvider.get());
    }

    /**
     * Dodaje nowe zadanie do bazy.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param task obiekt EmpTask do zapisania
     */
    public void addTask(EmpTask task) {
        logger.debug("addTask() – start, task={}", task);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(task);
            tx.commit();
            logger.info("addTask() – zadanie dodane: {}", task);
        } catch (Exception e) {
            logger.error("addTask() " +
                    "– błąd podczas dodawania zadania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addTask() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadanie o podanym identyfikatorze.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest wartość null.
     *
     * @param id identyfikator zadania
     * @return obiekt EmpTask lub null, jeśli nie istnieje
     */
    public EmpTask findTaskById(int id) {
        logger.debug("findTaskById() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            EmpTask t = em.find(EmpTask.class, id);
            logger.info("findTaskById() – znaleziono: {}", t);
            return t;
        } catch (Exception e) {
            logger.error("findTaskById() " +
                            "– błąd podczas wyszukiwania zadania id={}",
                    id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findTaskById() – EntityManager zamknięty");
        }
    }

    /**
     * Zwraca wszystkie zadania wraz z przypisanymi pracownikami i ich danymi.
     * Zapobiega wystąpieniu LazyInitializationException przy dostępie
     * do powiązanych obiektów.
     *
     * @return lista wszystkich zadań lub pusta lista w przypadku błędu
     */
    public List<EmpTask> getAllTasks() {
        logger.debug("getAllTasks() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT DISTINCT t " +
                                    "FROM EmpTask t " +
                                    "LEFT JOIN FETCH t.taskEmployees te " +
                                    "LEFT JOIN FETCH te.employee",
                            EmpTask.class)
                    .getResultList();
            logger.info("getAllTasks() " +
                            "– pobrano {} zadań z przypisaniami",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllTasks() " +
                    "– błąd podczas pobierania zadań", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllTasks() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje istniejące zadanie w bazie.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param task obiekt EmpTask do zaktualizowania
     */
    public void updateTask(EmpTask task) {
        logger.debug("updateTask() – start, task={}", task);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(task);
            tx.commit();
            logger.info("updateTask() " +
                    "– zadanie zaktualizowane: {}", task);
        } catch (Exception e) {
            logger.error("updateTask() " +
                    "– błąd podczas aktualizacji zadania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateTask() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa zadanie z bazy.
     * Operacja jest wykonywana w transakcji.
     * Jeśli zadanie nie istnieje, operacja jest logowana jako ostrzeżenie.
     *
     * @param task obiekt EmpTask do usunięcia
     */
    public void removeTask(EmpTask task) {
        logger.debug("removeTask() – start, task={}", task);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            EmpTask managed = em.find(EmpTask.class, task.getId());
            if (managed != null) {
                em.remove(managed);
                logger.info("removeTask() " +
                        "– usunięto zadanie: {}", managed);
            } else {
                logger.warn("removeTask() " +
                        "– brak zadania o id={}", task.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeTask() " +
                    "– błąd podczas usuwania zadania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeTask() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadania, których nazwa zawiera podany fragment.
     * Wyszukiwanie jest wykonywane bez rozróżniania wielkości liter.
     *
     * @param nameFragment fragment tekstu nazwy
     * @return lista obiektów EmpTask lub pusta lista
     */
    public List<EmpTask> findByName(String nameFragment) {
        logger.debug("findByName() – nameFragment={}", nameFragment);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM EmpTask t " +
                                    "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            EmpTask.class)
                    .setParameter("frag", nameFragment)
                    .getResultList();
            logger.info("findByName() " +
                    "– znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByName() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByName() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadania o dokładnie podanej dacie (bez czasu).
     * Wyszukiwanie wykorzystuje TemporalType.DATE do porównania samej daty.
     *
     * @param date data zadania
     * @return lista obiektów EmpTask lub pusta lista
     */
    public List<EmpTask> findByDate(Date date) {
        logger.debug("findByDate() – date={}", date);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM EmpTask t WHERE t.date = :date",
                            EmpTask.class)
                    .setParameter("date", date, TemporalType.DATE)
                    .getResultList();
            logger.info("findByDate() " +
                    "– znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByDate() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByDate() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadania o podanym statusie.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @param status status zadania
     * @return lista obiektów EmpTask lub pusta lista
     */
    public List<EmpTask> findByStatus(String status) {
        logger.debug("findByStatus() – status={}", status);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM EmpTask t " +
                                    "WHERE t.status = :status",
                            EmpTask.class)
                    .setParameter("status", status)
                    .getResultList();
            logger.info("findByStatus() " +
                    "– znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByStatus() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByStatus() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadania, których opis zawiera podany fragment.
     * Wyszukiwanie jest wykonywane bez rozróżniania wielkości liter.
     *
     * @param descriptionFragment fragment tekstu opisu
     * @return lista obiektów EmpTask lub pusta lista
     */
    public List<EmpTask> findByDescription(String descriptionFragment) {
        logger.debug("findByDescription() – descriptionFragment={}",
                descriptionFragment);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM EmpTask t " +
                                    "WHERE LOWER(t.description) " +
                                    "LIKE LOWER(CONCAT('%', :frag, '%'))",
                            EmpTask.class)
                    .setParameter("frag", descriptionFragment)
                    .getResultList();
            logger.info("findByDescription() " +
                    "– znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByDescription() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByDescription() " +
                    "– EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadania, których czas trwania zmiany mieści się
     * w podanym przedziale czasowym.
     *
     * @param from    początek przedziału czasu (inclusive)
     * @param toTime  koniec przedziału czasu (inclusive)
     * @return lista obiektów EmpTask lub pusta lista
     */
    public List<EmpTask> findByTimeShiftDuration(
            LocalTime from,
            LocalTime toTime
    ) {
        logger.debug("findByTimeShiftDuration() – from={}, toTime={}",
                from, toTime);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM EmpTask t " +
                                    "WHERE t.durationOfTheShift " +
                                    "BETWEEN :from AND :toTime",
                            EmpTask.class)
                    .setParameter("from", from)
                    .setParameter("toTime", toTime)
                    .getResultList();
            logger.info("findByTimeShiftDuration() " +
                            "– znaleziono {} zadań",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByTimeShiftDuration() " +
                    "– błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByTimeShiftDuration() " +
                    "– EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie zadania wraz z przypisanymi pracownikami.
     * Metoda zoptymalizowana pod kątem wydajności poprzez użycie JOIN FETCH.
     *
     * @return lista zadań z załadowanymi pracownikami
     */
    public List<EmpTask> getAllTasksWithEmployees() {
        logger.debug("getAllTasksWithEmployees() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM EmpTask t " +
                                    "JOIN FETCH t.taskEmployees",
                            EmpTask.class)
                    .getResultList();
            logger.info("getAllTasksWithEmployees() " +
                    "– pobrano {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllTasksWithEmployees() " +
                    "– błąd podczas pobierania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllTasksWithEmployees() " +
                    "– EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie zadania wraz z przypisanymi pracownikami i ich danymi.
     * Metoda używana w panelu kierownika dla pełnego widoku zadań.
     * Zapobiega wystąpieniu LazyInitializationException.
     *
     * @return lista zadań z pełnymi danymi lub pusta lista w przypadku błędu
     */
    public List<EmpTask> getAllTasksWithEmployeesAndAssignees() {
        logger.debug("getAllTasksWithEmployeesAndAssignees() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT DISTINCT t " +
                                    "FROM EmpTask t " +
                                    "LEFT JOIN FETCH t.taskEmployees te " +
                                    "LEFT JOIN FETCH te.employee",
                            EmpTask.class)
                    .getResultList();
            logger.info("getAllTasksWithEmployeesAndAssignees() " +
                            "– pobrano {} zadań",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllTasksWithEmployeesAndAssignees() " +
                    "– błąd pobierania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllTasksWithEmployeesAndAssignees() " +
                    "– EM zamknięty");
        }
    }

    /**
     * Zamyka wspólną fabrykę EMF (na zakończenie działania aplikacji).
     * Implementacja jest pusta, ponieważ korzystamy z EMFProvider.
     */
    @Override
    public void close() {
    }
}