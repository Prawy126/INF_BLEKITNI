/*
 * Classname: Address
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
import jakarta.persistence.OneToMany;
import java.util.List;

/**
 * Klasa reprezentująca adres.
 * Zawiera informacje o miejscowości, numerze domu, numerze mieszkania, kodzie pocztowym i mieście.
 * Zawiera również relację do pracowników przypisanych do tego adresu.
 */
@Entity
@Table(name = "Adresy")
@Access(AccessType.FIELD)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Miejscowosc")
    private String miejscowosc;

    @Column(name = "Numer_domu")
    private String numerDomu;

    @Column(name = "Numer_mieszkania")
    private String numerMieszkania;

    @Column(name = "Kod_pocztowy")
    private String kodPocztowy;

    @Column(name = "Miasto")
    private String miasto;

    @OneToMany(mappedBy = "adres")
    private List<Employee> pracownicy;

    // Gettery i settery
    /**
     * Funkcja zwracająca identyfikator adresu.
     *
     * @return identyfikator adresu
     */
    public int getId() { return id; }

    /**
     * Funkcja zwracająca miejscowość adresu.
     *
     * @return zwraca miejscowość adresu
     */
    public String getMiejscowosc() { return miejscowosc; }

    /**
     * Funkcja ustawiająca miejscowość adresu.
     *
     * @param miejscowosc miejscowość do ustawienia
     */
    public void setMiejscowosc(String miejscowosc) { this.miejscowosc = miejscowosc; }

    /**
     * Funkcja zwracająca numer domu adresu.
     *
     * @return zwraca numer domu adresu
     */
    public String getNumerDomu() { return numerDomu; }

    /**
     * Funkcja ustawiająca numer domu adresu.
     *
     * @param numerDomu numer domu do ustawienia
     */
    public void setNumerDomu(String numerDomu) { this.numerDomu = numerDomu; }

    /**
     * Funkcja zwracająca numer mieszkania adresu.
     *
     * @return zwraca numer mieszkania adresu
     */
    public String getNumerMieszkania() { return numerMieszkania; }

    /**
     * Ustawia numer mieszkania adresu.
     *
     * @param numerMieszkania numer mieszkania do ustawienia
     */
    public void setNumerMieszkania(String numerMieszkania) { this.numerMieszkania = numerMieszkania; }

    /**
     * Funkcja zwracająca kod pocztowy adresu.
     *
     * @return zwraca kod pocztowy adresu
     */
    public String getKodPocztowy() { return kodPocztowy; }

    /**
     * Funkcja ustawiająca kod pocztowy adresu.
     *
     * @param kodPocztowy kod pocztowy do ustawienia
     */
    public void setKodPocztowy(String kodPocztowy) {
        if (kodPocztowy == null || kodPocztowy.isBlank()) {
            throw new IllegalArgumentException("Kod pocztowy nie może być pusty.");
        }
        this.kodPocztowy = kodPocztowy;
    }

    /**
     * Funkcja zwracająca miasto adresu.
     *
     * @return zwraca miasto adresu
     */
    public String getMiasto() { return miasto; }

    /**
     * Funkcja ustawiająca miasto adresu.
     *
     * @param miasto miasto do ustawienia
     */
    public void setMiasto(String miasto) { this.miasto = miasto; }

    /**
     * Nadpisana metoda toString() do reprezentacji adresu w formacie "Miejscowość, ul. NumerDomu/NumerMieszkania, KodPocztowy Miasto".
     *
     * @return reprezentacja adresu w formacie tekstowym
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(miejscowosc).append(", ul. ").append(numerDomu);

        if (numerMieszkania != null && !numerMieszkania.trim().isEmpty()) {
            sb.append("/").append(numerMieszkania);
        }

        sb.append(", ").append(kodPocztowy).append(" ").append(miasto);

        return sb.toString();
    }
}