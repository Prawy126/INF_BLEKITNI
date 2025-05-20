/*
 * Classname: Order
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Klasa reprezentująca zamówienie w systemie.
 * Zawiera informacje o produkcie, pracowniku, ilości, cenie i dacie zamówienia.
 */
@Entity
@Table(name = "Zamowienia")
@Access(AccessType.FIELD)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "Id_produktu", nullable = false)
    private Product produkt;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee pracownik;

    @Column(name = "Ilosc", nullable = false)
    private int ilosc;

    @Column(name = "Cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal cena;

    @Temporal(TemporalType.DATE)
    @Column(name = "Data", nullable = false)
    private Date data;

    /**
     * Domyślny konstruktor.
     */
    public Order() {
    }

    /**
     * Konstruktor z parametrami.
     *
     * @param produkt  Produkt zamówienia
     * @param pracownik Pracownik realizujący zamówienie
     * @param ilosc    Ilość zamówionego produktu
     * @param cena     Cena zamówienia
     * @param data     Data zamówienia
     */
    public Order(Product produkt, Employee pracownik, int ilosc, BigDecimal cena, Date data) {
        this.produkt = produkt;
        this.pracownik = pracownik;
        this.ilosc = ilosc;
        this.cena = cena;
        this.data = data;
    }

    // === Gettery i settery ===

    /**
     * Zwraca id zamówienia.
     *
     * @return Identyfikator zamówienia
     */
    public int getId() {
        return id;
    }

    /**
     * Zwraca produkt zamówienia.
     *
     * @return produkt Produkt zamówienia
     */
    public Product getProdukt() {
        return produkt;
    }

    /**
     * Ustawia produkt zamówienia.
     *
     * @param produkt Produkt zamówienia
     */
    public void setProdukt(Product produkt) {
        this.produkt = produkt;
    }

    /**
     * Zwraca pracownika realizującego zamówienie.
     *
     * @return pracownik Pracownik realizujący zamówienie
     */
    public Employee getPracownik() {
        return pracownik;
    }

    /**
     * Ustawia pracownika realizującego zamówienie.
     *
     * @param pracownik Pracownik realizujący zamówienie
     */
    public void setPracownik(Employee pracownik) {
        this.pracownik = pracownik;
    }

    /**
     * Zwraca ilość zamówionego produktu.
     *
     * @return ilosc Ilość zamówionego produktu
     */
    public int getIlosc() {
        return ilosc;
    }

    /**
     * Ustawia ilość zamówionego produktu.
     *
     * @param ilosc Ilość zamówionego produktu
     */
    public void setIlosc(int ilosc) {
        this.ilosc = ilosc;
    }

    /**
     * Zwraca cenę zamówienia.
     *
     * @return cena Cena zamówienia
     */
    public BigDecimal getCena() {
        return cena;
    }

    /**
     * Ustawia cenę zamówienia.
     *
     * @param cena Cena zamówienia
     */
    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }

    /**
     * Zwraca datę zamówienia.
     *
     * @return data Data zamówienia
     */
    public Date getData() {
        return data;
    }

    /**
     * Ustawia datę zamówienia.
     *
     * @param data Data zamówienia
     */
    public void setData(Date data) {
        this.data = data;
    }

    /**
     * Zwraca reprezentację tekstową zamówienia.
     *
     * @return Reprezentacja tekstowa zamówienia
     */
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", produkt=" + (produkt != null ? produkt.getName() : "null") +
                ", pracownik=" + (pracownik != null ? pracownik.getLogin() : "null") +
                ", ilosc=" + ilosc +
                ", cena=" + cena +
                ", data=" + data +
                '}';
    }
}
