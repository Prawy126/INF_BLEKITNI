/*
 * Classname: EmpTaskRepository
 * Version information: 1.3
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TemporalType;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.EmpTask;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * Repozytorium zarządzające zadaniami w bazie danych.
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz wyszukiwanie zadań.
 */
public class EmpTaskRepository {
    private static final Logger logger = LogManager.getLogger(EmpTaskRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory dla persistence unit "myPU".
     */
    public EmpTaskRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono TaskRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowe zadanie do bazy.
     *
     * @param task obiekt Task do zapisania
     */
    public void addTask(EmpTask task) {
        logger.debug("addTask() – start, task={}", task);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(task);
            tx.commit();
            logger.info("addTask() – zadanie dodane: {}", task);
        } catch (Exception e) {
            logger.error("addTask() – błąd podczas dodawania zadania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addTask() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadanie o podanym identyfikatorze.
     *
     * @param id identyfikator zadania
     * @return obiekt Task lub null, jeśli nie istnieje
     */
    public EmpTask findTaskById(int id) {
        logger.debug("findTaskById() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            EmpTask t = em.find(EmpTask.class, id);
            logger.info("findTaskById() – znaleziono: {}", t);
            return t;
        } catch (Exception e) {
            logger.error("findTaskById() – błąd podczas wyszukiwania zadania id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findTaskById() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie zadania z bazy.
     *
     * @return lista wszystkich zadań lub pusta lista w przypadku błędu
     */
    public List<EmpTask> getAllTasks() {
        logger.debug("getAllTasks() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<EmpTask> list = em.createQuery("SELECT t FROM Task t", EmpTask.class)
                    .getResultList();
            logger.info("getAllTasks() – pobrano {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllTasks() – błąd podczas pobierania zadań", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllTasks() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje istniejące zadanie w bazie.
     *
     * @param task obiekt Task do zaktualizowania
     */
    public void updateTask(EmpTask task) {
        logger.debug("updateTask() – start, task={}", task);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(task);
            tx.commit();
            logger.info("updateTask() – zadanie zaktualizowane: {}", task);
        } catch (Exception e) {
            logger.error("updateTask() – błąd podczas aktualizacji zadania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateTask() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa zadanie z bazy.
     * TODO: Obsłużyć usuwanie przypisań do pracowników w kontekście więzów integralności.
     *
     * @param task obiekt Task do usunięcia
     */
    public void removeTask(EmpTask task) {
        logger.debug("removeTask() – start, task={}", task);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            EmpTask managed = em.find(EmpTask.class, task.getId());
            if (managed != null) {
                em.remove(managed);
                logger.info("removeTask() – usunięto zadanie: {}", managed);
            } else {
                logger.warn("removeTask() – brak zadania o id={}", task.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeTask() – błąd podczas usuwania zadania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeTask() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadania, których nazwa zawiera podany fragment (bez uwzględniania wielkości liter).
     *
     * @param nameFragment fragment tekstu nazwy
     * @return lista obiektów Task lub pusta lista w przypadku błędu lub braków
     */
    public List<EmpTask> findByName(String nameFragment) {
        logger.debug("findByName() – nameFragment={}", nameFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM Task t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            EmpTask.class)
                    .setParameter("frag", nameFragment)
                    .getResultList();
            logger.info("findByName() – znaleziono {} zadań", list.size());
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
     * Znajduje zadania o dokładnie podanej dacie.
     *
     * @param date date zadania (bez czasu)
     * @return lista obiektów Task lub pusta lista w przypadku błędu lub braków
     */
    public List<EmpTask> findByDate(Date date) {
        logger.debug("findByDate() – date={}", date);
        EntityManager em = emf.createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM Task t WHERE t.date = :date", EmpTask.class)
                    .setParameter("date", date, TemporalType.DATE)
                    .getResultList();
            logger.info("findByDate() – znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByDate() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByDate() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadania o podanym statusie.
     *
     * @param status status zadania
     * @return lista obiektów Task lub pusta lista w przypadku błędu lub braków
     */
    public List<EmpTask> findByStatus(String status) {
        logger.debug("findByStatus() – status={}", status);
        EntityManager em = emf.createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM Task t WHERE t.status = :status", EmpTask.class)
                    .setParameter("status", status)
                    .getResultList();
            logger.info("findByStatus() – znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByStatus() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByStatus() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadania, których opis zawiera podany fragment (bez uwzględniania wielkości liter).
     *
     * @param descriptionFragment fragment tekstu opisu
     * @return lista obiektów Task lub pusta lista w przypadku błędu lub braków
     */
    public List<EmpTask> findByDescription(String descriptionFragment) {
        logger.debug("findByDescription() – descriptionFragment={}", descriptionFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM Task t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            EmpTask.class)
                    .setParameter("frag", descriptionFragment)
                    .getResultList();
            logger.info("findByDescription() – znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByDescription() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByDescription() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadania, których czas trwania zmiany mieści się w podanym przedziale.
     *
     * @param from     początek przedziału czasu (inclusive)
     * @param toTime   koniec przedziału czasu (inclusive)
     * @return lista obiektów Task lub pusta lista w przypadku błędu lub braków
     */
    public List<EmpTask> findByTimeShiftDuration(LocalTime from, LocalTime toTime) {
        logger.debug("findByTimeShiftDuration() – from={}, toTime={}", from, toTime);
        EntityManager em = emf.createEntityManager();
        try {
            List<EmpTask> list = em.createQuery(
                            "SELECT t FROM Task t WHERE t.durationOfTheShift BETWEEN :from AND :toTime",
                            EmpTask.class)
                    .setParameter("from", from)
                    .setParameter("toTime", toTime)
                    .getResultList();
            logger.info("findByTimeShiftDuration() – znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByTimeShiftDuration() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByTimeShiftDuration() – EntityManager zamknięty");
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
