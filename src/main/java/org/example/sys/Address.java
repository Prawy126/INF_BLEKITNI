/*
 * Classname: OrderRepositoryTest
 * Version information: 1.2
 * Date: 2025-05-23
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

@Entity
@Table(name = "Adresy")
@Access(AccessType.FIELD)
public class Address {

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

    // Gettery i settery
    public int getId() { return id; }

    public String getTown() { return town; }
    public void setTown(String town) { this.town = town; }

    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }

    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    @Override
    public String toString() {
        String apartment = (apartmentNumber != null && !apartmentNumber.isEmpty())
                ? "/" + apartmentNumber
                : "";
        return town + ", ul. " + houseNumber + apartment + ", " + zipCode + " " + city;
    }
}