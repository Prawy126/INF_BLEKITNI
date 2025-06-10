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

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Encja reprezentująca powiązanie między zadaniami a pracownikami.
 * Odpowiada tabeli Zadania_Pracownicy z bazy danych.
 * Przechowuje informacje o przypisaniu pracownika do zadania,
 * czasie rozpoczęcia i zakończenia pracy nad zadaniem, statusie
 * oraz czasie trwania zmiany.
 */
@Entity
@Table(name = "Zadania_Pracownicy")
public class TaskEmployee {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą TaskEmployee.
     */
    private static final Logger logger
            = LogManager.getLogger(TaskEmployee.class);

    /**
     * Złożony identyfikator encji, składający się z ID zadania
     * i ID pracownika.
     */
    @EmbeddedId
    private TaskEmployeeId id;

    /**
     * Zadanie powiązane z encją.
     * Relacja wiele-do-jednego, wiele przypisań może dotyczyć jednego
     * zadania.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "Id_zadania")
    private EmpTask task;

    /**
     * Pracownik powiązany z encją.
     * Relacja wiele-do-jednego, wiele przypisań może dotyczyć jednego
     * pracownika.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "Id_pracownika")
    private Employee employee;

    /**
     * Czas rozpoczęcia pracy nad zadaniem.
     */
    private LocalDateTime startTime;

    /**
     * Czas zakończenia pracy nad zadaniem.
     */
    private LocalDateTime endTime;

    /**
     * Status przypisania pracownika do zadania.
     * Może określać np. "w trakcie", "zakończone", "oczekujące".
     */
    private String status;

    /**
     * Czas trwania zmiany przypisanej do zadania.
     */
    private Duration shiftDuration;

    /**
     * Konstruktor bezparametrowy wymagany przez JPA.
     * Operacja jest logowana na poziomie DEBUG.
     */
    public TaskEmployee() {
        logger.debug("Utworzono nową instancję TaskEmployee" +
                " (domyślny konstruktor).");
    }

    /**
     * Konstruktor wygodny.
     * Tworzy powiązanie między zadaniem a pracownikiem z automatycznym
     * generowaniem złożonego identyfikatora.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param task     encja Zadanie
     * @param employee encja Pracownik
     */
    public TaskEmployee(EmpTask task, Employee employee) {
        this.id = new TaskEmployeeId(task.getId(), employee.getId());
        this.task = task;
        this.employee = employee;

        logger.info("Utworzono powiązanie zadania ID:" +
                " {} z pracownikiem ID: {}", task.getId(), employee.getId());
    }

    /**
     * @return złożony identyfikator (taskId + employeeId)
     */
    public TaskEmployeeId getId() {
        return id;
    }

    /**
     * @param id nowy złożony identyfikator
     */
    public void setId(TaskEmployeeId id) {
        this.id = id;
        logger.debug("Zaktualizowano ID powiązania" +
                " zadania-pracownika na: {}", id);
    }

    /**
     * @return powiązane zadanie
     */
    public EmpTask getTask() {
        return task;
    }

    /**
     * @param task nowe zadanie do powiązania
     */
    public void setTask(EmpTask task) {
        this.task = task;
        logger.debug("Zaktualizowano powiązane zadanie na ID:" +
                " {}", task != null ? task.getId() : "null");
    }

    /**
     * @return powiązany pracownik
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * @param employee nowy pracownik do powiązania
     */
    public void setEmployee(Employee employee) {
        if (employee != null) {
            this.employee = employee;
            logger.debug("Zaktualizowano powiązanego pracownika" +
                    " na ID: {}", employee.getId());
        } else {
            logger.warn("Próba ustawienia pracownika na wartość null.");
        }
    }

    /**
     * @return czas rozpoczęcia pracy nad zadaniem
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * @param startTime nowy czas rozpoczęcia pracy
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * @return czas zakończenia pracy nad zadaniem
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * @param endTime nowy czas zakończenia pracy
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * @return status przypisania do zadania
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status nowy status przypisania
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return czas trwania zmiany
     */
    public Duration getShiftDuration() {
        return shiftDuration;
    }

    /**
     * @param shiftDuration nowy czas trwania zmiany
     */
    public void setShiftDuration(Duration shiftDuration) {
        this.shiftDuration = shiftDuration;
    }
}