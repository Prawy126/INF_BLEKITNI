/*
 * Classname: TechnicalIssue
 * Version information: 1.1
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

/**
 * Klasa reprezentująca zgłoszenie techniczne w systemie.
 * Zawiera informacje o typie zgłoszenia, opisie, dacie zgłoszenia, pracowniku oraz statusie.
 */
@Entity
@Table(name = "Zgloszenia_techniczne")
public class TechnicalIssue {

    // Inicjalizacja logera
    private static final Logger logger = LogManager.getLogger(TechnicalIssue.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Typ", nullable = false)
    private String type; // "Awaria sprzętu", "Błąd oprogramowania", "Inne"

    @Column(name = "Opis", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Data_zgłoszenia")
    private LocalDate dateSubmitted;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", referencedColumnName = "Id")
    private Employee employee;

    @Column(name = "Status", length = 50)
    private String status = "Nowe"; // Domyślnie "Nowe"

    // Konstruktor bez ID (dla nowych zgłoszeń)
    public TechnicalIssue(String type, String description, LocalDate dateSubmitted, Employee employee, String status) {
        this.type = type;
        this.description = description;
        this.dateSubmitted = dateSubmitted;
        this.employee = employee;
        this.status = status;

        logger.info("Utworzono nowe zgłoszenie techniczne: typ={}, pracownik={}, data={}",
                type, employee.getLogin(), dateSubmitted);
    }

    // Konstruktor z ID (dla istniejących zgłoszeń)
    public TechnicalIssue(int id, String type, String description, LocalDate dateSubmitted, Employee employee, String status) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.dateSubmitted = dateSubmitted;
        this.employee = employee;
        this.status = status;

        logger.info("Wczytano istniejące zgłoszenie techniczne: ID={}, typ={}, status={}", id, type, status);
    }

    // Konstruktor domyślny (wymagany przez JPA)
    public TechnicalIssue() {
        logger.debug("Utworzono nową instancję TechnicalIssue (domyślny konstruktor).");
    }

    // Gettery i settery

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        logger.debug("Zaktualizowano ID zgłoszenia na: {}", id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        logger.debug("Zaktualizowano typ zgłoszenia na: {}", type);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        logger.debug("Zaktualizowano opis zgłoszenia.");
    }

    public LocalDate getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(LocalDate dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
        logger.debug("Zaktualizowano datę zgłoszenia na: {}", dateSubmitted);
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        logger.debug("Zaktualizowano pracownika zgłaszającego problem: {}", employee.getLogin());
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        logger.info("Zmieniono status zgłoszenia z {} na {}", this.status, status);
        this.status = status;
    }

    @Override
    public String toString() {
        return "TechnicalIssue{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", dateSubmitted=" + dateSubmitted +
                ", employee=" + (employee != null ? employee.getLogin() : "null") +
                ", status='" + status + '\'' +
                '}';
    }
}