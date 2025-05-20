/*
 * Classname: Report
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */

// src/main/java/org/example/sys/Report.java
package org.example.sys;

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
public class Raport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Typ_raportu", nullable = false)
    private String typRaportu;

    @Column(name = "Data_poczatku", nullable = false)
    private LocalDate dataPoczatku;

    @Column(name = "Data_zakonczenia", nullable = false)
    private LocalDate dataZakonczenia;

    @ManyToOne @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee pracownik;

    @Column(name = "Plik", nullable = false)
    private String sciezkaPliku;

    /**
     * Domyślny konstruktor.
     * Używany przez Hibernate do tworzenia instancji klasy.
     */
    public Raport() {}

    /**
     * Konstruktor z parametrami.
     * Umożliwia ustawienie typu raportu, daty początkowej, daty zakończenia,
     * pracownika oraz ścieżki do pliku raportu.
     *
     * @param typRaportu      Typ raportu
     * @param dataPoczatku    Data początkowa
     * @param dataZakonczenia Data zakończenia
     * @param pracownik       Pracownik
     * @param sciezkaPliku    Ścieżka do pliku raportu
     */
    public Raport(String typRaportu, LocalDate dataPoczatku,
                  LocalDate dataZakonczenia, Employee pracownik, String sciezkaPliku) {
        this.typRaportu = typRaportu;
        this.dataPoczatku = dataPoczatku;
        this.dataZakonczenia = dataZakonczenia;
        this.pracownik = pracownik;
        this.sciezkaPliku = sciezkaPliku;
    }

    /* --- gettery / settery --- */
    /**
     * Zwraca identyfikator raportu.
     *
     * @return Identyfikator raportu
     */
    public int getId(){
        return id;
    }

    /**
     * Zwraca typ raportu.
     *
     * @return Typ raportu
     */
    public String getTypRaportu(){
        return typRaportu;
    }
    /**
     * Ustawia typ raportu.
     *
     * @param t Typ raportu
     */
    public void setTypRaportu(String t){
        this.typRaportu = t;
    }

    /**
     * Zwraca datę początkową raportu.
     *
     * @return Data początkowa
     */
    public LocalDate getDataPoczatku(){
        return dataPoczatku;
    }

    /**
     * Ustawia datę początkową raportu.
     *
     * @param d Data początkowa
     */
    public void setDataPoczatku(LocalDate d){
        this.dataPoczatku = d;
    }

    /**
     * Zwraca datę zakończenia raportu.
     *
     * @return Data zakończenia
     */
    public LocalDate getDataZakonczenia(){
        return dataZakonczenia;
    }

    /**
     * Ustawia datę zakończenia raportu.
     *
     * @param d Data zakończenia
     */
    public void setDataZakonczenia(LocalDate d){
        this.dataZakonczenia = d;
    }

    /**
     * Zwraca pracownika, który wygenerował raport.
     *
     * @return Pracownik
     */
    public Employee getPracownik(){
        return pracownik;
    }

    /**
     * Ustawia pracownika, który wygenerował raport.
     *
     * @param p Pracownik
     */
    public void setPracownik(Employee p){
        this.pracownik = p;
    }

    /**
     * Zwraca ścieżkę do pliku raportu.
     *
     * @return Ścieżka do pliku
     */
    public String getSciezkaPliku(){
        return sciezkaPliku;
    }

    /**
     * Ustawia ścieżkę do pliku raportu.
     *
     * @param p Ścieżka do pliku
     */
    public void setSciezkaPliku(String p){
        this.sciezkaPliku = p;
    }
}

