/*
 * Classname: Report
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */

// src/main/java/org/example/sys/Report.java
package org.example.sys;

import jakarta.persistence.*;
import java.time.LocalDate;

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

    public Raport() {}

    public Raport(String typRaportu, LocalDate dataPoczatku,
                  LocalDate dataZakonczenia, Employee pracownik, String sciezkaPliku) {
        this.typRaportu = typRaportu;
        this.dataPoczatku = dataPoczatku;
        this.dataZakonczenia = dataZakonczenia;
        this.pracownik = pracownik;
        this.sciezkaPliku = sciezkaPliku;
    }

    /* --- gettery / settery --- */
    public int getId()                         { return id; }
    public String     getTypRaportu()          { return typRaportu; }
    public void       setTypRaportu(String t)  { this.typRaportu = t; }
    public LocalDate  getDataPoczatku()        { return dataPoczatku; }
    public void       setDataPoczatku(LocalDate d){ this.dataPoczatku = d; }
    public LocalDate  getDataZakonczenia()     { return dataZakonczenia; }
    public void       setDataZakonczenia(LocalDate d){ this.dataZakonczenia = d; }
    public Employee   getPracownik()           { return pracownik; }
    public void       setPracownik(Employee p) { this.pracownik = p; }
    public String     getSciezkaPliku()        { return sciezkaPliku; }
    public void       setSciezkaPliku(String p){ this.sciezkaPliku = p; }
}

