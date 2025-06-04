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
 * Mapowana na tabelę "Zgloszenia_techniczne" w bazie danych.
 * Zawiera informacje o typie zgłoszenia, opisie, dacie zgłoszenia,
 * pracowniku oraz statusie. Umożliwia śledzenie i obsługę problemów
 * technicznych zgłaszanych przez pracowników.
 */
@Entity
@Table(name = "Zgloszenia_techniczne")
public class TechnicalIssue {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą TechnicalIssue.
     */
    private static final Logger logger
            = LogManager.getLogger(TechnicalIssue.class);

    /**
     * Unikalny identyfikator zgłoszenia technicznego generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Typ zgłoszenia technicznego.
     * Przykładowe wartości: "Awaria sprzętu", "Błąd oprogramowania", "Inne".
     */
    @Column(name = "Typ", nullable = false)
    private String type;

    /**
     * Szczegółowy opis zgłoszonego problemu technicznego.
     * Przechowywany jako tekst bez ograniczenia długości.
     */
    @Column(name = "Opis", columnDefinition = "TEXT")
    private String description;

    /**
     * Data złożenia zgłoszenia technicznego.
     */
    @Column(name = "Data_zgłoszenia")
    private LocalDate dateSubmitted;

    /**
     * Pracownik zgłaszający problem techniczny.
     * Relacja wiele-do-jednego, gdzie wiele zgłoszeń może być przypisanych
     * do jednego pracownika.
     */
    @ManyToOne
    @JoinColumn(name = "Id_pracownika", referencedColumnName = "Id")
    private Employee employee;

    /**
     * Status zgłoszenia, określający etap obsługi problemu.
     * Domyślna wartość to "Nowe".
     */
    @Column(name = "Status", length = 50)
    private String status = "Nowe";

    /**
     * Konstruktor dla nowych zgłoszeń (bez istniejącego ID).
     * Tworzy nowe zgłoszenie techniczne z podanymi parametrami.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param type typ zgłoszenia technicznego
     * @param description opis problemu
     * @param dateSubmitted data złożenia zgłoszenia
     * @param employee pracownik zgłaszający problem
     * @param status status zgłoszenia
     */
    public TechnicalIssue(
            String type,
            String description,
            LocalDate dateSubmitted,
            Employee employee,
            String status
    ) {
        this.type = type;
        this.description = description;
        this.dateSubmitted = dateSubmitted;
        this.employee = employee;
        this.status = status;

        logger.info("Utworzono nowe zgłoszenie techniczne: typ={}," +
                        " pracownik={}, data={}",
                type, employee.getLogin(), dateSubmitted);
    }

    /**
     * Konstruktor dla istniejących zgłoszeń (z określonym ID).
     * Używany głównie przy wczytywaniu danych z bazy.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param id identyfikator zgłoszenia
     * @param type typ zgłoszenia technicznego
     * @param description opis problemu
     * @param dateSubmitted data złożenia zgłoszenia
     * @param employee pracownik zgłaszający problem
     * @param status status zgłoszenia
     */
    public TechnicalIssue(
            int id,
            String type,
            String description,
            LocalDate dateSubmitted,
            Employee employee,
            String status
    ) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.dateSubmitted = dateSubmitted;
        this.employee = employee;
        this.status = status;

        logger.info("Wczytano istniejące zgłoszenie techniczne:" +
                " ID={}, typ={}, status={}", id, type, status);
    }

    /**
     * Konstruktor domyślny wymagany przez JPA.
     * Operacja jest logowana na poziomie DEBUG.
     */
    public TechnicalIssue() {
        logger.debug("Utworzono nową instancję TechnicalIssue" +
                " (domyślny konstruktor).");
    }

    /**
     * @return identyfikator zgłoszenia
     */
    public int getId() {
        return id;
    }

    /**
     * @param id nowy identyfikator zgłoszenia
     */
    public void setId(int id) {
        this.id = id;
        logger.debug("Zaktualizowano ID zgłoszenia na: {}", id);
    }

    /**
     * @return typ zgłoszenia
     */
    public String getType() {
        return type;
    }

    /**
     * @param type nowy typ zgłoszenia
     */
    public void setType(String type) {
        this.type = type;
        logger.debug("Zaktualizowano typ zgłoszenia na: {}", type);
    }

    /**
     * @return opis problemu technicznego
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description nowy opis problemu
     */
    public void setDescription(String description) {
        this.description = description;
        logger.debug("Zaktualizowano opis zgłoszenia.");
    }

    /**
     * @return data złożenia zgłoszenia
     */
    public LocalDate getDateSubmitted() {
        return dateSubmitted;
    }

    /**
     * @param dateSubmitted nowa data złożenia zgłoszenia
     */
    public void setDateSubmitted(LocalDate dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
        logger.debug("Zaktualizowano datę zgłoszenia na:" +
                " {}", dateSubmitted);
    }

    /**
     * @return pracownik zgłaszający problem
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * @param employee nowy pracownik zgłaszający problem
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
        logger.debug("Zaktualizowano pracownika" +
                " zgłaszającego problem: {}", employee.getLogin());
    }

    /**
     * @return status zgłoszenia
     */
    public String getStatus() {
        return status;
    }

    /**
     * Aktualizuje status zgłoszenia.
     * Zmiana statusu jest logowana na poziomie INFO.
     *
     * @param status nowy status zgłoszenia
     */
    public void setStatus(String status) {
        logger.info("Zmieniono status zgłoszenia z {} na {}",
                this.status, status);
        this.status = status;
    }

    /**
     * Zwraca reprezentację tekstową zgłoszenia technicznego.
     * Zawiera wszystkie istotne informacje o zgłoszeniu.
     *
     * @return tekstowa reprezentacja zgłoszenia
     */
    @Override
    public String toString() {
        return "TechnicalIssue{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", dateSubmitted=" + dateSubmitted +
                ", employee=" + (employee != null
                ? employee.getLogin() : "null") +
                ", status='" + status + '\'' +
                '}';
    }
}