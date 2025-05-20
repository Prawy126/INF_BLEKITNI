/*
 * Classname: AbsenceRequest
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.Objects;

/**
 * Klasa reprezentująca wniosek o nieobecność pracownika.
 * Zawiera informacje o typie wniosku, datach rozpoczęcia i zakończenia, opisie oraz pracowniku.
 */
@Entity
@Table(name = "Wnioski_o_nieobecnosc")
@Access(AccessType.FIELD)
public class AbsenceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Typ_wniosku", nullable = false)
    private String typWniosku;

    @Column(name = "Data_rozpoczecia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataRozpoczecia;

    @Column(name = "Data_zakonczenia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataZakonczenia;

    @Column(name = "Opis")
    private String opis;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee pracownik;

    // === Konstruktory ===
    /**
     * Domyślny konstruktor.
     */
    public AbsenceRequest() {}

    /**
     * Konstruktor z parametrami.
     *
     * @exception IllegalArgumentException jeśli któryś z wymaganych parametrów jest null.
     *
     * @param typWniosku      Typ wniosku o nieobecność.
     * @param dataRozpoczecia Data rozpoczęcia nieobecności.
     * @param dataZakonczenia Data zakończenia nieobecności.
     * @param opis            Opis wniosku.
     * @param pracownik       Pracownik składający wniosek.
     */
    public AbsenceRequest(String typWniosku, Date dataRozpoczecia, Date dataZakonczenia, String opis, Employee pracownik) {
        if (typWniosku == null || dataRozpoczecia == null || dataZakonczenia == null || pracownik == null) {
            throw new IllegalArgumentException("Wszystkie pola oprócz opisu są wymagane.");
        }
        this.typWniosku = typWniosku;
        this.dataRozpoczecia = dataRozpoczecia;
        this.dataZakonczenia = dataZakonczenia;
        this.opis = opis;
        this.pracownik = pracownik;
    }

    // === Gettery i settery ===
    /**
     * Zwraca identyfikator wniosku.
     *
     * @return Identyfikator wniosku.
     */
    public int getId() {
        return id;
    }

    /**
     * Pobiera identyfikator wniosku.
     *
     * @return Identyfikator wniosku.
     */
    public String getTypWniosku() {
        return typWniosku;
    }

    /**
     * Ustawia typ wniosku.
     *
     * @param typWniosku Typ wniosku o nieobecność.
     */
    public void setTypWniosku(String typWniosku) {
        this.typWniosku = typWniosku;
    }

    /**
     * Pobiera datę rozpoczęcia nieobecności.
     *
     * @return Data rozpoczęcia nieobecności.
     */
    public Date getDataRozpoczecia() {
        return dataRozpoczecia;
    }

    /**
     * Ustawia datę rozpoczęcia nieobecności.
     *
     * @param dataRozpoczecia Data rozpoczęcia nieobecności.
     */
    public void setDataRozpoczecia(Date dataRozpoczecia) {
        this.dataRozpoczecia = dataRozpoczecia;
    }

    /**
     * Pobiera datę zakończenia nieobecności.
     *
     * @return Data zakończenia nieobecności.
     */
    public Date getDataZakonczenia() {
        return dataZakonczenia;
    }

    /**
     * Ustawia datę zakończenia nieobecności.
     *
     * @param dataZakonczenia Data zakończenia nieobecności.
     */
    public void setDataZakonczenia(Date dataZakonczenia) {
        this.dataZakonczenia = dataZakonczenia;
    }

    /**
     * Pobiera opis wniosku.
     *
     * @return Opis wniosku.
     */
    public String getOpis() {
        return opis;
    }

    /**
     * Ustawia opis wniosku.
     *
     * @param opis Opis wniosku.
     */
    public void setOpis(String opis) {
        this.opis = opis;
    }

    /**
     * Pobiera pracownika składającego wniosek.
     *
     * @return Pracownik składający wniosek.
     */
    public Employee getPracownik() {
        return pracownik;
    }

    /**
     * Ustawia pracownika składającego wniosek.
     *
     * @param pracownik Pracownik składający wniosek.
     */
    public void setPracownik(Employee pracownik) {
        this.pracownik = pracownik;
    }

    /**
     * Zwraca reprezentację tekstową obiektu AbsenceRequest.
     *
     * @return Reprezentacja tekstowa obiektu.
     */
    @Override
    public String toString() {
        return String.format(
                "AbsenceRequest{id=%d, typ='%s', od=%s, do=%s, opis='%s', pracownik=%s %s}",
                id, typWniosku, dataRozpoczecia, dataZakonczenia, opis,
                Objects.toString(pracownik.getName(), "null"),
                Objects.toString(pracownik.getSurname(), "")
        );
    }
}
