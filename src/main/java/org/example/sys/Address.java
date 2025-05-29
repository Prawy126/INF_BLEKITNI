/*
 * Classname: Address
 * Version information: 1.3
 * Date: 2025-05-29
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

// Importy Log4j2
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = "Adresy")
@Access(AccessType.FIELD)
public class Address {

    private static final Logger logger = LogManager.getLogger(Address.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Miejscowosc")
    private String town;

    @Column(name = "Numer_domu")
    private String houseNumber;

    @Column(name = "Numer_mieszkania")
    private String apartmentNumber;

    @Column(name = "Kod_pocztowy")
    private String zipCode;

    @Column(name = "Miasto")
    private String city;

    @OneToMany(mappedBy = "address")
    private List<Employee> employees;

    /**
     * Domyślny konstruktor — logowanie tworzenia nowego obiektu.
     */
    public Address() {
        logger.debug("Utworzono nowy obiekt Address (konstruktor domyślny)");
    }

    // Gettery i settery z logowaniem

    public int getId() {
        return id;
    }

    public String getTown() {
        logger.trace("Pobrano miejscowość: {}", town);
        return town;
    }

    public void setTown(String town) {
        logger.info("Zmieniono miejscowość na: {}", town);
        this.town = town;
    }

    public String getHouseNumber() {
        logger.trace("Pobrano numer domu: {}", houseNumber);
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        logger.info("Zmieniono numer domu na: {}", houseNumber);
        this.houseNumber = houseNumber;
    }

    public String getApartmentNumber() {
        logger.trace("Pobrano numer mieszkania: {}", apartmentNumber);
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        if (apartmentNumber == null || apartmentNumber.isEmpty()) {
            logger.warn("Numer mieszkania został usunięty (wartość null lub pusta).");
        } else {
            logger.info("Zmieniono numer mieszkania na: {}", apartmentNumber);
        }
        this.apartmentNumber = apartmentNumber;
    }

    public String getZipCode() {
        logger.trace("Pobrano kod pocztowy: {}", zipCode);
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        logger.info("Zmieniono kod pocztowy na: {}", zipCode);
        this.zipCode = zipCode;
    }

    public String getCity() {
        logger.trace("Pobrano miasto: {}", city);
        return city;
    }

    public void setCity(String city) {
        logger.info("Zmieniono miasto na: {}", city);
        this.city = city;
    }

    /**
     * Przesłonięta metoda toString z logowaniem.
     */
    @Override
    public String toString() {
        String apartment = (apartmentNumber != null && !apartmentNumber.isEmpty())
                ? "/" + apartmentNumber
                : "";
        String result = town + ", ul. " + houseNumber + apartment + ", " + zipCode + " " + city;

        logger.trace("Wygenerowano toString(): {}", result);
        return result;
    }
}