/*
 * Classname: Logistician
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import org.example.wyjatki.AgeException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasa reprezentująca rolę logistyka – logiczną "nakładkę" na encję Employee.
 */
public class Logistician {

    private static final Logger logger
            = LogManager.getLogger(Logistician.class);

    private final Employee employee;

    /**
     * Konstruktor przyjmujący istniejącego pracownika.
     */
    public Logistician(Employee employee) {
        this.employee = employee;
        logger.info("Utworzono logistykę: {} {}",
                employee.getName(),
                employee.getSurname());
    }

    /**
     * Zwraca referencję do powiązanego pracownika.
     */
    public Employee getEmployee() {
        logger.debug("Pobrano pracownika-logistykę: {}",
                employee.getLogin());
        return employee;
    }

    // === Metody operujące na danych pracownika ===

    /**
     * Metoda aktualizująca imię logistyka.
     */
    public void updateName(String newName) {
        try {
            employee.setName(newName);
            logger.info("Zmieniono imię logistyka na: {}", newName);
        } catch (Exception e) {
            logger.error("Błąd zmiany imienia logistyka", e);
            System.err.println("Błąd zmiany imienia: " + e.getMessage());
        }
    }

    /**
     * Metoda aktualizująca nazwisko logistyka.
     */
    public void updateSurname(String newSurname) {
        try {
            employee.setSurname(newSurname);
            logger.info("Zmieniono nazwisko logistyka na: {}",
                    newSurname);
        } catch (Exception e) {
            logger.error("Błąd zmiany nazwiska logistyka", e);
            System.err.println("Błąd zmiany nazwiska: " + e.getMessage());
        }
    }

    /**
     * Metoda aktualizująca wiek logistyka.
     */
    public void updateAge(int newAge) {
        try {
            employee.setAge(newAge);
            logger.info("Zmieniono wiek logistyka na: {}", newAge);
        } catch (AgeException e) {
            logger.error("Błąd zmiany wieku logistyka", e);
            System.err.println("Błąd zmiany wieku: " + e.getMessage());
        }
    }

    /**
     * Metoda aktualizująca adres logistyka.
     */
    public void updateAddress(Address newAddress) {
        employee.setAddress(newAddress);
        logger.info("Zaktualizowano adres logistyka");
    }

    /**
     * Metoda aktualizująca hasło logistyka.
     */
    public void updatePassword(String newPassword) {
        try {
            employee.setPassword(newPassword);
            logger.info("Zmieniono hasło logistyka");
        } catch (PasswordException e) {
            logger.error("Błąd zmiany hasła logistyka", e);
            System.err.println("Błąd zmiany hasła: " + e.getMessage());
        }
    }

    /**
     * Metoda aktualizująca stanowisko logistyka.
     */
    public void updateDepartment(String newDepartment) {
        employee.setPosition(newDepartment);
        logger.info("Zmieniono stanowisko logistyka na: {}",
                newDepartment);
    }

    /**
     * Metoda aktualizująca zarobki logistyka.
     */
    public void updateSalary(BigDecimal newSalary) {
        try {
            employee.setSalary(newSalary);
            logger.info("Zmieniono wynagrodzenie logistyka na: {}",
                    newSalary);
        } catch (SalaryException e) {
            logger.error("Błąd zmiany wynagrodzenia logistyka", e);
            System.err.println("Błąd zmiany zarobków: " + e.getMessage());
        }
    }

    // === Przykładowa metoda logistyczna ===

    /**
     * Metoda przydzielająca zamówienie do realizacji.
     *
     * @param orderId ID zamówienia do przydzielenia
     */
    public void assignOrder(int orderId) {
        logger.info("Logistyk {} przydzielił zamówienie o ID: {}",
                employee.getName(), orderId);
        System.out.println("Logistyk " + employee.getName() +
                " przydzielił zamówienie o ID: " + orderId);
    }
}