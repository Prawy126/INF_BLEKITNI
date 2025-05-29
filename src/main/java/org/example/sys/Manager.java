/*
 * Classname: Manager
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.wyjatki.AgeException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca rolę menedżera – logiczną nakładkę na encję Employee.
 */
public class Manager {

    // Dodanie statycznego pola typu Logger z org.apache.logging.log4j.Logger
    private static final Logger logger = LogManager.getLogger(Manager.class);

    private final Employee employee;
    private final List<Employee> managedEmployees;

    /**
     * Tworzy nową instancję menedżera na podstawie istniejącego pracownika.
     *
     * @param employee Pracownik, który staje się menedżerem.
     */
    public Manager(Employee employee) {
        this.employee = employee;
        this.managedEmployees = new ArrayList<>();
    }

    /**
     * Zwraca pracownika, który jest menedżerem.
     *
     * @return Pracownik będący menedżerem.
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Dodaje pracownika do listy pracowników zarządzanych przez menedżera.
     */
    public void addEmployee(Employee e) {
        if (e != null && !managedEmployees.contains(e)) {
            managedEmployees.add(e);
        }
    }

    /**
     * Usuwa pracownika z listy pracowników zarządzanych przez menedżera.
     */
    public void removeEmployee(Employee e) {
        managedEmployees.remove(e);
    }

    /**
     * Zwraca listę pracowników
     */
    public List<Employee> getManagedEmployees() {
        return managedEmployees;
    }

    /**
     * Aktualizuje imię menedżera.
     */
    public void updateName(String newName) {
        try {
            employee.setName(newName);
        } catch (Exception e) {
            logger.error("Błąd zmiany imienia: {}", e.getMessage(), e);
        }
    }

    /**
     * Aktualizuje nazwisko menedżera.
     */
    public void updateSurname(String newSurname) {
        try {
            employee.setSurname(newSurname);
        } catch (Exception e) {
            logger.error("Błąd zmiany nazwiska: {}", e.getMessage(), e);
        }
    }

    /**
     * Aktualizuje wiek menedżera.
     *
     * @param newAge aktualizuje wiek menedżera
     */
    public void updateAge(int newAge) {
        try {
            employee.setAge(newAge);
        } catch (AgeException e) {
            logger.error("Błąd zmiany wieku: {}", e.getMessage(), e);
        }
    }

    /**
     * Aktualizuje adres menedżera.
     *
     * @param newAddress aktualizuje adres menedżera
     */
    public void updateAddress(Address newAddress) {
        employee.setAddress(newAddress);
        logger.info("Zaktualizowano adres menedżera.");
    }

    /**
     * Aktualizuje hasło menedżera.
     * @param newPassword
     */
    public void updatePassword(String newPassword) {
        try {
            employee.setPassword(newPassword);
        } catch (PasswordException e) {
            logger.error("Błąd zmiany hasła: {}", e.getMessage(), e);
        }
    }

    /**
     * Aktualizuje stanowisko menedżera.
     *
     * @param newDepartment aktualizuje stanowisko menedżera
     */
    public void updateDepartment(String newDepartment) {
        employee.setPosition(newDepartment);
        logger.info("Zaktualizowano stanowisko menedżera.");
    }

    /**
     * Aktualizuje wynagrodzenie menedżera.
     *
     * @param newSalary aktualizuje wynagrodzenie menedżera
     */
    public void updateSalary(BigDecimal newSalary) {
        try {
            employee.setSalary(newSalary);
        } catch (SalaryException e) {
            logger.error("Błąd zmiany wynagrodzenia: {}", e.getMessage(), e);
        }
    }

    /**
     * Generuje raport dla zespołu menedżera.
     */
    public void generateTeamReport() {
        logger.info("Menedżer {} generuje raport dla {} pracowników.", employee.getName(), managedEmployees.size());
    }
}