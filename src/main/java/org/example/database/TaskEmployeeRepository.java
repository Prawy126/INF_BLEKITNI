/*
 * Classname: TaskEmployeeRepository
 * Version information: 1.2
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.TaskEmployee;
import org.example.sys.TaskEmployeeId;

import java.util.List;

/**
 * Repozytorium zarządzające powiązaniami zadań z pracownikami.
 * Używa współdzielonego EntityManagerFactory z EMFProvider.
 */
public class TaskEmployeeRepository implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(TaskEmployeeRepository.class);
    private final EntityManagerFactory emf = EMFProvider.get();

    /**
     * Dodaje nowe przypisanie zadania do pracownika.
     */
    public void add(TaskEmployee te) {
        logger.debug("add() – start, te={}", te);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(te);
            tx.commit();
            logger.info("add() – przypisanie zapisane: {}", te);
        } catch (Exception e) {
            logger.error("add() – błąd podczas zapisywania przypisania: {}", te, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Usuwa istniejące przypisanie zadania do pracownika.
     */
    public void remove(TaskEmployee te) {
        logger.debug("remove() – start, te={}", te);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TaskEmployee managed = em.merge(te);
            em.remove(managed);
            tx.commit();
            logger.info("remove() – przypisanie usunięte: {}", te);
        } catch (Exception e) {
            logger.error("remove() – błąd podczas usuwania przypisania: {}", te, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje przypisanie po kluczu złożonym.
     */
    public TaskEmployee findById(int taskId, int employeeId) {
        logger.debug("findById() – start, taskId={}, employeeId={}", taskId, employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            TaskEmployee te = em.find(TaskEmployee.class, new TaskEmployeeId(taskId, employeeId));
            logger.info("findById() – wynik {}", te);
            return te;
        } catch (Exception e) {
            logger.error("findById() – błąd podczas wyszukiwania dla ({},{})", taskId, employeeId, e);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie przypisania danego pracownika.
     */
    public List<TaskEmployee> findByEmployee(int employeeId) {
        logger.debug("findByEmployee() – start, employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<TaskEmployee> list = em.createQuery(
                            "SELECT te FROM TaskEmployee te WHERE te.id.employeeId = :eid", TaskEmployee.class)
                    .setParameter("eid", employeeId)
                    .getResultList();
            logger.info("findByEmployee() – znaleziono {} przypisań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByEmployee() – błąd podczas wyszukiwania dla employeeId={}", employeeId, e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie przypisania dla danego zadania.
     */
    public List<TaskEmployee> findByTask(int taskId) {
        logger.debug("findByTask() – start, taskId={}", taskId);
        EntityManager em = emf.createEntityManager();
        try {
            List<TaskEmployee> list = em.createQuery(
                            "SELECT te FROM TaskEmployee te WHERE te.id.taskId = :tid", TaskEmployee.class)
                    .setParameter("tid", taskId)
                    .getResultList();
            logger.info("findByTask() – znaleziono {} przypisań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByTask() – błąd podczas wyszukiwania dla taskId={}", taskId, e);
            return List.of();
        } finally {
            em.close();
        }
    }

    @Override
    public void close() {
        // brak implementacji; EMF zamyka się w EMFProvider.close() podczas shutdown
    }
}
