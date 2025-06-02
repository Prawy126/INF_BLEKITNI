/*
 * Classname: TaskEmployeeRepository
 * Version information: 1.3
 * Date: 2025-06-02
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.EmpTask;
import org.example.sys.Employee;
import org.example.sys.TaskEmployee;
import org.example.sys.TaskEmployeeId;

import java.time.LocalDate;
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

            // Pobieramy zarządzane (attached) instancje EmpTask i Employee
            int taskId = te.getTask().getId();
            int empId  = te.getEmployee().getId();
            EmpTask managedTask = em.getReference(EmpTask.class, taskId);
            Employee managedEmp  = em.getReference(Employee.class, empId);

            // Ustawiamy te zarządzane instancje w obiekcie TaskEmployee
            te.setTask(managedTask);
            te.setEmployee(managedEmp);

            // Teraz możemy persystować TaskEmployee – obie referencje są w stanie „managed”
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

    /**
     * Aktualizuje istniejące przypisanie pracownika do zadania.
     */
    public void update(TaskEmployee updatedTe) {
        logger.debug("update() – start, updatedTe={}", updatedTe);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TaskEmployee managed = em.merge(updatedTe);   // scal i zachowaj zmiany

            // upewniamy się, że ew. zmiana statusu zadania trafi do bazy
            if (managed.getTask() != null) {
                em.merge(managed.getTask());
            }
            tx.commit();
            logger.info("update() – zakończono pomyślnie");
        } catch (Exception e) {
            logger.error("update() – błąd", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }


    /**
     * Zwraca wszystkie przypisania zadań danego pracownika z konkretnego dnia
     * (porównuje pole task.date z datą przekazaną w argumencie).
     */
    public List<TaskEmployee> findEmployeeTasksForDate(int employeeId, LocalDate day) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("""
                SELECT te
                FROM TaskEmployee te
                JOIN FETCH te.task t
                WHERE te.id.employeeId = :eid
                  AND DATE(t.date) = :day
                """, TaskEmployee.class)
                    .setParameter("eid", employeeId)
                    .setParameter("day", day)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void updateTaskStatus(int taskId, String newStatus) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            EmpTask t = em.createQuery(
                            "SELECT t FROM EmpTask t JOIN FETCH t.taskEmployees WHERE t.id = :taskId",
                            EmpTask.class)
                    .setParameter("taskId", taskId)
                    .getSingleResult();
            if (t != null) {
                t.setStatus(newStatus);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public EmpTask findTaskWithEmployees(int taskId) {
        logger.debug("findTaskWithEmployees() – start, taskId={}", taskId);
        EntityManager em = emf.createEntityManager();
        try {
            EmpTask task = em.createQuery(
                            "SELECT t FROM EmpTask t JOIN FETCH t.taskEmployees WHERE t.id = :taskId",
                            EmpTask.class)
                    .setParameter("taskId", taskId)
                    .getSingleResult();
            logger.info("findTaskWithEmployees() – pobrano zadanie z {} przypisaniami", task.getTaskEmployees().size());
            return task;
        } catch (Exception e) {
            logger.error("findTaskWithEmployees() – błąd podczas pobierania zadania o id={}", taskId, e);
            return null;
        } finally {
            em.close();
        }
    }


    @Override
    public void close() {
        // brak implementacji; EMF zamyka się w EMFProvider.close() podczas shutdown
    }
}
