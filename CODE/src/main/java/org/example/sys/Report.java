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
 * Mapowana na tabelę "Raporty" w bazie danych.
 * Zawiera informacje o typie raportu, zakresie dat,
 * pracowniku generującym raport oraz ścieżce do pliku.
 */
@Entity
@Table(name = "Raporty")
@Access(AccessType.FIELD)
public class Report {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą Report.
     */
    private static final Logger logger = LogManager.getLogger(Report.class);

    /**
     * Unikalny identyfikator raportu generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    /**
     * Typ raportu określający rodzaj generowanych danych.
     */
    @Column(name = "Typ_raportu", nullable = false)
    private String reportType;

    /**
     * Data początkowa zakresu danych uwzględnianych w raporcie.
     */
    @Column(name = "Data_poczatku", nullable = false)
    private LocalDate startDate;

    /**
     * Data końcowa zakresu danych uwzględnianych w raporcie.
     */
    @Column(name = "Data_zakonczenia", nullable = false)
    private LocalDate endDate;

    /**
     * Pracownik, który wygenerował raport.
     * Relacja wiele-do-jednego, gdzie wiele raportów może być przypisanych
     * do jednego pracownika.
     */
    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee employee;

    /**
     * Ścieżka do pliku z wygenerowanym raportem.
     */
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
     * Tworzy raport z określonymi wartościami wszystkich pól.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param reportType typ raportu
     * @param startDate data początkowa zakresu
     * @param endDate data końcowa zakresu
     * @param employee pracownik generujący raport
     * @param filePath ścieżka do pliku raportu
     */
    public Report(
            String reportType,
            LocalDate startDate,
            LocalDate endDate,
            Employee employee,
            String filePath
    ) {
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employee = employee;
        this.filePath = filePath;

        logger.info("Utworzono nowy raport typu: {}," +
                        " zakres: {} - {}, pracownik: {}, ścieżka: {}",
                reportType, startDate, endDate, employee.getLogin(), filePath);
    }

    /* --- gettery / settery --- */

    /**
     * @return identyfikator raportu
     */
    public int getId() {
        return id;
    }

    /**
     * @return typ raportu
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * @param t nowy typ raportu
     */
    public void setReportType(String t) {
        this.reportType = t;
        logger.debug("Zaktualizowano typ raportu na: {}", t);
    }

    /**
     * @return data początkowa zakresu
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param d nowa data początkowa
     */
    public void setStartDate(LocalDate d) {
        this.startDate = d;
        logger.debug("Zaktualizowano datę początkową" +
                " raportu na: {}", d);
    }

    /**
     * @return data końcowa zakresu
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param d nowa data końcowa
     */
    public void setEndDate(LocalDate d) {
        this.endDate = d;
        logger.debug("Zaktualizowano datę końcową raportu na: {}", d);
    }

    /**
     * @return pracownik generujący raport
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * @param p nowy pracownik
     */
    public void setEmployee(Employee p) {
        this.employee = p;
        logger.debug("Zaktualizowano pracownika" +
                " generującego raport: {}", p.getLogin());
    }

    /**
     * @return ścieżka do pliku raportu
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param p nowa ścieżka do pliku
     */
    public void setFilePath(String p) {
        this.filePath = p;
        logger.debug("Zaktualizowano ścieżkę do" +
                " pliku raportu na: {}", p);
    }
}