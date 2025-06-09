/*
 * Classname: Address
 * Version information: 1.4
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import java.util.List;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasa reprezentująca adres w systemie.
 * Mapowana na tabelę "Adresy" w bazie danych.
 * Zawiera informacje o lokalizacji jak miasto, kod pocztowy,
 * numer domu oraz mieszkania.
 */
@Entity
@Table(name = "Adresy")
@Access(AccessType.FIELD)
public class Address {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą Address.
     */
    private static final Logger logger = LogManager.getLogger(Address.class);

    /**
     * Unikalny identyfikator adresu generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Miejscowość, w której znajduje się adres.
     */
    @Column(name = "Miejscowosc")
    private String town;

    /**
     * Numer domu.
     */
    @Column(name = "Numer_domu")
    private String houseNumber;

    /**
     * Numer mieszkania (opcjonalny).
     */
    @Column(name = "Numer_mieszkania")
    private String apartmentNumber;

    /**
     * Kod pocztowy w formacie przyjętym w Polsce (XX-XXX).
     */
    @Column(name = "Kod_pocztowy")
    private String zipCode;

    /**
     * Nazwa miasta.
     */
    @Column(name = "Miasto")
    private String city;

    /**
     * Lista pracowników przypisanych do tego adresu.
     * Relacja jeden-do-wielu, gdzie jeden adres może być
     * przypisany do wielu pracowników.
     */
    @OneToMany(mappedBy = "address")
    private List<Employee> employees;

    /**
     * Domyślny konstruktor — logowanie tworzenia nowego obiektu.
     * Tworzy pusty obiekt adresu bez inicjalizacji pól.
     */
    public Address() {
        logger.debug("Utworzono nowy obiekt Address (konstruktor domyślny)");
    }

    // Gettery i settery z logowaniem

    /**
     * Zwraca identyfikator adresu.
     *
     * @return unikalny identyfikator adresu
     */
    public int getId() {
        return id;
    }

    /**
     * Pobiera nazwę miejscowości.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return nazwa miejscowości
     */
    public String getTown() {
        logger.trace("Pobrano miejscowość: {}", town);
        return town;
    }

    /**
     * Ustawia nazwę miejscowości.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param town nowa nazwa miejscowości
     */
    public void setTown(String town) {
        logger.info("Zmieniono miejscowość na: {}", town);
        this.town = town;
    }

    /**
     * Pobiera numer domu.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return numer domu
     */
    public String getHouseNumber() {
        logger.trace("Pobrano numer domu: {}", houseNumber);
        return houseNumber;
    }

    /**
     * Ustawia numer domu.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param houseNumber nowy numer domu
     */
    public void setHouseNumber(String houseNumber) {
        logger.info("Zmieniono numer domu na: {}", houseNumber);
        this.houseNumber = houseNumber;
    }

    /**
     * Pobiera numer mieszkania.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return numer mieszkania lub null, jeśli nie jest określony
     */
    public String getApartmentNumber() {
        logger.trace("Pobrano numer mieszkania: {}", apartmentNumber);
        return apartmentNumber;
    }

    /**
     * Ustawia numer mieszkania.
     * Jeżeli wartość jest null lub pusta, generuje ostrzeżenie w logu.
     * W przeciwnym wypadku loguje zmianę na poziomie INFO.
     *
     * @param apartmentNumber nowy numer mieszkania
     */
    public void setApartmentNumber(String apartmentNumber) {
        if (apartmentNumber == null || apartmentNumber.isEmpty()) {
            logger.warn("Numer mieszkania został usunięty" +
                    " (wartość null lub pusta).");
        } else {
            logger.info("Zmieniono numer mieszkania na: {}",
                    apartmentNumber);
        }
        this.apartmentNumber = apartmentNumber;
    }

    /**
     * Pobiera kod pocztowy.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return kod pocztowy
     */
    public String getZipCode() {
        logger.trace("Pobrano kod pocztowy: {}", zipCode);
        return zipCode;
    }

    /**
     * Ustawia kod pocztowy.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param zipCode nowy kod pocztowy
     */
    public void setZipCode(String zipCode) {
        logger.info("Zmieniono kod pocztowy na: {}", zipCode);
        this.zipCode = zipCode;
    }

    /**
     * Pobiera nazwę miasta.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return nazwa miasta
     */
    public String getCity() {
        logger.trace("Pobrano miasto: {}", city);
        return city;
    }

    /**
     * Ustawia nazwę miasta.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param city nowa nazwa miasta
     */
    public void setCity(String city) {
        logger.info("Zmieniono miasto na: {}", city);
        this.city = city;
    }

    /**
     * Przesłonięta metoda toString z logowaniem.
     * Zwraca reprezentację tekstową adresu w formacie:
     * [miejscowość], ul. [numer domu]/[numer mieszkania], [kod pocztowy]
     * [miasto]
     *
     * Numer mieszkania jest dodawany tylko jeśli jest określony.
     *
     * @return tekstowa reprezentacja adresu
     */
    @Override
    public String toString() {
        String apartment = (apartmentNumber != null
                && !apartmentNumber.isEmpty())
                ? "/" + apartmentNumber
                : "";
        String result = town + ", ul. " + houseNumber + apartment +
                ", " + zipCode + " " + city;

        logger.trace("Wygenerowano toString(): {}", result);
        return result;
    }
}