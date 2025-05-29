/*
 * Classname: TaskEmployee
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Encja reprezentująca powiązanie między zadaniami a pracownikami.
 * Odpowiada tabeli Zadania_Pracownicy z bazy danych.
 */
@Entity
@Table(name = "Zadania_Pracownicy")
public class TaskEmployee {

    // Inicjalizacja logera
    private static final Logger logger = LogManager.getLogger(TaskEmployee.class);

    @EmbeddedId
    private TaskEmployeeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "Id_zadania")
    private EmpTask task;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "Id_pracownika")
    private Employee employee;

    /** Konstruktor bezparametrowy wymagany przez JPA */
    public TaskEmployee() {
        logger.debug("Utworzono nową instancję TaskEmployee (domyślny konstruktor).");
    }

    /**
     * Konstruktor wygodny.
     *
     * @param task     encja Zadanie
     * @param employee encja Pracownik
     */
    public TaskEmployee(EmpTask task, Employee employee) {
        this.id = new TaskEmployeeId(task.getId(), employee.getId());
        this.task = task;
        this.employee = employee;

        logger.info("Utworzono powiązanie zadania ID: {} z pracownikiem ID: {}", task.getId(), employee.getId());
    }

    /** Zwraca złożony identyfikator (taskId + employeeId). */
    public TaskEmployeeId getId() {
        return id;
    }

    /** Ustawia złożony identyfikator (taskId + employeeId). */
    public void setId(TaskEmployeeId id) {
        this.id = id;
        logger.debug("Zaktualizowano ID powiązania zadania-pracownika na: {}", id);
    }

    /** Zwraca powiązane zadanie. */
    public EmpTask getTask() {
        return task;
    }

    /** Ustawia powiązane zadanie. */
    public void setTask(EmpTask task) {
        this.task = task;
        logger.debug("Zaktualizowano powiązane zadanie na ID: {}", task != null ? task.getId() : "null");
    }

    /** Zwraca powiązanego pracownika. */
    public Employee getEmployee() {
        return employee;
    }

    /** Ustawia powiązanego pracownika. */
    public void setEmployee(Employee employee) {
        if (employee != null) {
            this.employee = employee;
            logger.debug("Zaktualizowano powiązanego pracownika na ID: {}", employee.getId());
        } else {
            logger.warn("Próba ustawienia pracownika na wartość null.");
        }
    }
}