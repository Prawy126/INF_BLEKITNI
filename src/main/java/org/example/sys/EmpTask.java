/*
 * Classname: EmpTask
 * Version information: 1.3
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Reprezentuje zadanie w systemie.
 */
@Entity
@Table(name = "Zadania")
@Access(AccessType.FIELD)
public class EmpTask {

    private static final Logger logger = LogManager.getLogger(EmpTask.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Nazwa")
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "Data")
    private Date date;

    @Column(name = "Status")
    private String status;

    @Column(name = "Opis", columnDefinition = "TEXT")
    private String description;

    /**
     * Czas trwania zmiany pracownika przy zadaniu
     */
    @Column(name = "czas_trwania_zmiany")
    private LocalTime durationOfTheShift;

    @Column(name = "usuniety")
    private boolean usuniety = false;

    /** zamiast osobnego pola employee: lista rekordów z tabeli łączącej */
    @OneToMany(mappedBy = "task", fetch
            = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TaskEmployee> taskEmployees = new ArrayList<>();

    @Column(name = "Priorytet")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    public Priority getPriority() {
        logger.trace("Pobrano priorytet zadania: {}", priority);
        return priority;
    }

    public void setPriority(Priority priority) {
        logger.info("Zmieniono priorytet zadania na: {}", priority);
        this.priority = priority;
    }

    // Definicja enum Priority wewnątrz klasy EmpTask
    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    public List<TaskEmployee> getTaskEmployees() {
        logger.trace("Pobrano listę pracowników przypisanych" +
                " do zadania (liczba: {})", taskEmployees.size());
        return taskEmployees;
    }

    /** Konstruktor bezparametrowy wymagany przez JPA. */
    public EmpTask() {
        logger.debug("Utworzono nowe zadanie (konstruktor domyślny)");
    }

    public EmpTask(String name,
                   Date date,
                   String status,
                   String description,
                   LocalTime durationOfTheShift,
                   Priority priority)
    {
        this.name = name;
        this.date = date;
        this.status = status;
        this.description = description;
        this.durationOfTheShift = durationOfTheShift;
        this.priority = priority;
        logger.info("Utworzono zadanie: '{}'," +
                " termin: {}, czas zmiany: {}," +
                " priorytet: {}", name, date, durationOfTheShift, priority);
    }

    public EmpTask(String name,
                   Date date,
                   String status,
                   String description,
                   LocalTime durationOfTheShift
    ) {
        this.name = name;
        this.date = date;
        this.status = status;
        this.description = description;
        this.durationOfTheShift = durationOfTheShift;
        this.priority = Priority.MEDIUM; // Domyślny priorytet
        logger.info("Utworzono zadanie: '{}', termin: {}," +
                " czas zmiany: {}, priorytet: {}", name, date,
                durationOfTheShift, priority);
    }

    // ==================== Gettery i Settery z logowaniem ====================

    public int getId() {
        logger.trace("Pobrano ID zadania: {}", id);
        return id;
    }

    /**
     * Ustawia identyfikator zadania.
     * (Potrzebne do testów jednostkowych)
     */
    public void setId(int id) {
        logger.debug("Ustawiono ID zadania: {}", id);
        this.id = id;
    }

    public String getName() {
        logger.trace("Pobrano nazwę zadania: {}", name);
        return name;
    }

    public void setName(String name) {
        logger.info("Zmieniono nazwę zadania na: {}", name);
        this.name = name;
    }

    public Date getDate() {
        logger.trace("Pobrano datę zadania: {}",
                date != null ? date.toString() : "null");
        return date;
    }

    public void setDate(Date date) {
        logger.info("Zmieniono datę zadania na: {}",
                date != null ? date.toString() : "null");
        this.date = date;
    }

    public String getStatus() {
        logger.trace("Pobrano status zadania: {}", status);
        return status;
    }

    public void setStatus(String status) {
        logger.info("Zmieniono status zadania na: {}", status);
        this.status = status;
    }

    public String getDescription() {
        logger.trace("Pobrano opis zadania");
        return description;
    }

    public void setDescription(String description) {
        logger.info("Zaktualizowano opis zadania");
        this.description = description;
    }

    public LocalTime getDurationOfTheShift() {
        logger.trace("Pobrano czas trwania zmiany: {}",
                durationOfTheShift);
        return durationOfTheShift;
    }

    public void setDurationOfTheShift(LocalTime durationOfTheShift) {
        logger.info("Zmieniono czas trwania zmiany na: {}",
                durationOfTheShift);
        this.durationOfTheShift = durationOfTheShift;
    }

    /**
     * Zwraca reprezentację tekstową zadania.
     */
    @Override
    public String toString() {
        String result = String.format(
                "Zadanie: %s, Termin: %s, Czas zmiany: %s",
                name,
                date != null ? date.toString() : "brak daty",
                durationOfTheShift != null
                        ? durationOfTheShift.toString() : "brak"
        );
        logger.trace("Wygenerowano toString(): {}", result);
        return result;
    }

    /** Wygodna do użycia metoda, gdy zawsze jest dokładnie jeden assignee: */
    public Employee getSingleAssignee() {
        if (taskEmployees.isEmpty()) {
            logger.warn("Brak przypisanego pracownika do zadania");
            return null;
        }
        logger.debug("Pobrano pracownika przypisanego do zadania: {}",
                taskEmployees.get(0).getEmployee().getName());
        return taskEmployees.get(0).getEmployee();
    }

    public boolean isUsuniety() {
        logger.trace("Pobrano status usunięcia zadania: {}", usuniety);
        return usuniety;
    }

    public void setUsuniety(boolean usuniety) {
        logger.info("Zmieniono status usunięcia zadania na: {}", usuniety);
        this.usuniety = usuniety;
    }

    /**
     * Usuwa pracownika z zadania.
     */
    public void unassignEmployee(TaskEmployee taskEmployee) {
        if (taskEmployee != null && taskEmployees.remove(taskEmployee)) {
            logger.info("Usunięto przypisanie pracownika do zadania");
        } else {
            logger.warn("Nie można usunąć przypisania" +
                    " – brak takiego TaskEmployee");
        }
    }
}