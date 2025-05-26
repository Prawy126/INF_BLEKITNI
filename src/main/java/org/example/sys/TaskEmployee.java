/*
 * Classname: OrderRepositoryTest
 * Version information: 1.1
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

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
    }

    /** Zwraca złożony identyfikator (taskId + employeeId). */
    public TaskEmployeeId getId() {
        return id;
    }

    /** Ustawia złożony identyfikator (taskId + employeeId). */
    public void setId(TaskEmployeeId id) {
        this.id = id;
    }

    /** Zwraca powiązane zadanie. */
    public EmpTask getTask() {
        return task;
    }

    /** Ustawia powiązane zadanie. */
    public void setTask(EmpTask task) {
        this.task = task;
    }

    /** Zwraca powiązanego pracownika. */
    public Employee getEmployee() {
        return employee;
    }

    /** Ustawia powiązanego pracownika. */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
