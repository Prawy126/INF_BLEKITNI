/*
 * Classname: Task
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Temporal;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Reprezentuje zadanie w systemie.
 */
@Entity(name = "Task")
@Table(name = "Zadania")
@Access(AccessType.FIELD)
public class EmpTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Czas trwania zmiany pracownika przy zadaniu
     */
    @Column(name = "czas_trwania_zmiany")
    private LocalTime durationOfTheShift;

    /** zamiast osobnego pola employee: lista rekordów z tabeli łączącej */
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TaskEmployee> taskEmployees = new ArrayList<>();

    public List<TaskEmployee> getTaskEmployees() {
        return taskEmployees;
    }

    /** Konstruktor bezparametrowy wymagany przez JPA. */
    public EmpTask() {
    }

    /**
     * Konstruktor pełny (z czasem zmiany).
     *
     * @param name               name zadania
     * @param date                termin wykonania
     * @param status              status zadania
     * @param description                description zadania
     * @param durationOfTheShift   czas trwania zmiany przy zadaniu
     */
    public EmpTask(String name, Date date, String status, String description, LocalTime durationOfTheShift) {
        this.name = name;
        this.date = date;
        this.status = status;
        this.description = description;
        this.durationOfTheShift = durationOfTheShift;
    }

    // ==================== Gettery i Settery ====================

    public int getId() {
        return id;
    }

    /**
     * Ustawia identyfikator zadania.
     * (Potrzebne do testów jednostkowych)
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getDurationOfTheShift() {
        return durationOfTheShift;
    }

    public void setDurationOfTheShift(LocalTime durationOfTheShift) {
        this.durationOfTheShift = durationOfTheShift;
    }

    /**
     * Zwraca reprezentację tekstową zadania.
     */
    @Override
    public String toString() {
        return String.format(
                "Zadanie: %s, Termin: %s, Czas zmiany: %s",
                name,
                date != null ? date.toString() : "brak daty",
                durationOfTheShift != null ? durationOfTheShift.toString() : "brak"
        );
    }

    /** Wygodna do użycia metoda, gdy zawsze jest dokładnie jeden assignee: */
    public Employee getSingleAssignee() {
        return taskEmployees.isEmpty()
                ? null
                : taskEmployees.get(0).getEmployee();
    }
}
