/*
 * Classname: Report
 * Version information: 1.3
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
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

/**
 * Klasa reprezentująca raport w systemie.
 * Zawiera informacje o typie raportu, dacie początkowej, dacie zakończenia,
 * pracowniku oraz ścieżce do pliku raportu.
 */
@Entity
@Table(name = "Raporty")
@Access(AccessType.FIELD)
public class Report {

    // Inicjalizacja logera
    private static final Logger logger = LogManager.getLogger(Report.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Typ_raportu", nullable = false)
    private String reportType;

    @Column(name = "Data_poczatku", nullable = false)
    private LocalDate startDate;

    @Column(name = "Data_zakonczenia", nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee employee;

    @Column(name = "Plik", nullable = false)
    private String filePath;

    /**
     * Domyślny konstruktor.
     * Używany przez Hibernate do tworzenia instancji klasy.
     */
    public Report() {
        logger.debug("Utworzono nową instancję Report (domyślny konstruktor).");
    }

    /**
     * Konstruktor z parametrami.
     * Umożliwia ustawienie typu raportu, daty początkowej, daty zakończenia,
     * pracownika oraz ścieżki do pliku raportu.
     *
     * @param reportType   Typ raportu
     * @param startDate    Data początkowa
     * @param endDate      Data zakończenia
     * @param employee     Pracownik
     * @param filePath     Ścieżka do pliku raportu
     */
    public Report(String reportType, LocalDate startDate,
                  LocalDate endDate, Employee employee, String filePath) {
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employee = employee;
        this.filePath = filePath;

        logger.info("Utworzono nowy raport typu: {}, zakres: {} - {}, pracownik: {}, ścieżka: {}",
                reportType, startDate, endDate, employee.getLogin(), filePath);
    }

    /* --- gettery / settery --- */

    public int getId() {
        return id;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String t) {
        this.reportType = t;
        logger.debug("Zaktualizowano typ raportu na: {}", t);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate d) {
        this.startDate = d;
        logger.debug("Zaktualizowano datę początkową raportu na: {}", d);
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate d) {
        this.endDate = d;
        logger.debug("Zaktualizowano datę końcową raportu na: {}", d);
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee p) {
        this.employee = p;
        logger.debug("Zaktualizowano pracownika generującego raport: {}", p.getLogin());
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String p) {
        this.filePath = p;
        logger.debug("Zaktualizowano ścieżkę do pliku raportu na: {}", p);
    }
}