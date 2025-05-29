/*
 * Classname: TaskEmployeeId
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Klasa reprezentująca złożony klucz główny (EmbeddedId)
 * dla encji TaskEmployee (połączenie taskId + employeeId).
 */
@Embeddable
public class TaskEmployeeId implements Serializable {

    // Inicjalizacja logera
    private static final Logger logger = LogManager.getLogger(TaskEmployeeId.class);

    @Column(name = "Id_zadania")
    private int taskId;

    @Column(name = "Id_pracownika")
    private int employeeId;

    // Konstruktor bezparametrowy wymagany przez JPA
    public TaskEmployeeId() {
        logger.debug("Utworzono nową instancję TaskEmployeeId (domyślny konstruktor).");
    }

    // Konstruktor wygodny
    public TaskEmployeeId(int taskId, int employeeId) {
        this.taskId = taskId;
        this.employeeId = employeeId;
        logger.info("Utworzono złożony ID: Id_zadania={}, Id_pracownika={}", taskId, employeeId);
    }

    // Gettery i settery

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
        logger.debug("Zaktualizowano taskId na: {}", taskId);
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
        logger.debug("Zaktualizowano employeeId na: {}", employeeId);
    }

    // equals i hashCode – ważne, żeby JPA prawidłowo porównywało identyfikatory

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskEmployeeId)) return false;
        TaskEmployeeId that = (TaskEmployeeId) o;
        boolean result = taskId == that.taskId && employeeId == that.employeeId;
        if (!result) {
            logger.warn("TaskEmployeeId NIE jest równy: {} vs {}", this, that);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(taskId, employeeId);
        logger.debug("Obliczono hashCode: {}", hash);
        return hash;
    }

    @Override
    public String toString() {
        return "TaskEmployeeId{" +
                "taskId=" + taskId +
                ", employeeId=" + employeeId +
                '}';
    }
}