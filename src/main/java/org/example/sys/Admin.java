/*
 * Classname: Admin
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.example.wyjatki.AgeException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// Importy Log4j2
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasa reprezentująca rolę administratora systemu – logiczną nakładkę na encję Employee.
 */
public class Admin {

    private static final Logger logger = LogManager.getLogger(Admin.class);

    private final Employee employee;
    private final List<Employee> allEmployees;

    /**
     * Tworzy nową instancję administratora na podstawie istniejącego pracownika.
     *
     * @param employee Pracownik, który staje się administratorem.
     */
    public Admin(Employee employee) {
        this.employee = employee;
        this.allEmployees = new ArrayList<>();
        logger.info("Utworzono administratora: {}", employee.getLogin());
    }

    /**
     * Zwraca pracownika, który jest administratorem.
     *
     * @return Pracownik będący administratorem.
     */
    public Employee getEmployee() {
        logger.debug("Pobrano pracownika-administratora: {}", employee.getLogin());
        return employee;
    }

    /**
     * Zwraca informację czy użytkownik jest administratorem.
     *
     * @return true, jeśli użytkownik jest administratorem, false w przeciwnym razie.
     */
    public boolean isAdmin() {
        logger.trace("Sprawdzono uprawnienia administratora dla: {}", employee.getLogin());
        return true;
    }

    /**
     * Dodaje pracownika do listy wszystkich pracowników.
     *
     * @param e Pracownik do dodania.
     */
    public void addEmployee(Employee e) {
        if (e != null && !allEmployees.contains(e)) {
            allEmployees.add(e);
            logger.info("Dodano pracownika do listy: {} {}", e.getName(), e.getSurname());
        } else {
            logger.warn("Próbowano dodać nieprawidłowego pracownika (null lub już istnieje)");
        }
    }

    /**
     * Usuwa pracownika z listy wszystkich pracowników.
     *
     * @param e Pracownik do usunięcia.
     */
    public void removeEmployee(Employee e) {
        if (e != null && "root".equalsIgnoreCase(e.getPosition())) {
            logger.warn("Próba usunięcia użytkownika z rolą root została zablokowana");
            System.err.println("Próba usunięcia użytkownika z rolą root została zablokowana");
            return;
        }
        if (e != null) {
            allEmployees.remove(e);
            logger.info("Usunięto pracownika: {} {}", e.getName(), e.getSurname());
        } else {
            logger.warn("Próbowano usunąć pracownika o wartości null");
        }
    }

    /**
     * Zwraca listę wszystkich pracowników.
     *
     * @return Lista wszystkich pracowników.
     */
    public List<Employee> getAllEmployees() {
        logger.debug("Pobrano listę wszystkich pracowników (liczba: {})", allEmployees.size());
        return allEmployees;
    }

    /**
     * Aktualizuje imię administratora.
     */
    public void updateName(String newName) {
        try {
            employee.setName(newName);
            logger.info("Zmieniono imię administratora na: {}", newName);
        } catch (Exception e) {
            logger.error("Błąd zmiany imienia: {}", e.getMessage(), e);
            System.err.println("Błąd zmiany imienia: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje nazwisko administratora.
     */
    public void updateSurname(String newSurname) {
        try {
            employee.setSurname(newSurname);
            logger.info("Zmieniono nazwisko administratora na: {}", newSurname);
        } catch (Exception e) {
            logger.error("Błąd zmiany nazwiska: {}", e.getMessage(), e);
            System.err.println("Błąd zmiany nazwiska: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje wiek administratora.
     */
    public void updateAge(int newAge) {
        try {
            employee.setAge(newAge);
            logger.info("Zmieniono wiek administratora na: {}", newAge);
        } catch (AgeException e) {
            logger.error("Błąd zmiany wieku: {}", e.getMessage(), e);
            System.err.println("Błąd zmiany wieku: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje adres administratora.
     */
    public void updateAddress(Address address) {
        employee.setAddress(address);
        logger.info("Zaktualizowano adres administratora");
    }

    /**
     * Aktualizuje hasło administratora.
     */
    public void updatePassword(String newPassword) {
        try {
            employee.setPassword(newPassword);
            logger.info("Zmieniono hasło administratora");
        } catch (PasswordException e) {
            logger.error("Błąd zmiany hasła: {}", e.getMessage(), e);
            System.err.println("Błąd zmiany hasła: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje stanowisko pracownika.
     */
    public void updateDepartment(String newDepartment) {
        employee.setPosition(newDepartment);
        logger.info("Zmieniono stanowisko pracownika na: {}", newDepartment);
    }

    /**
     * Aktualizuje wynagrodzenie pracownika.
     */
    public void updateSalary(BigDecimal newSalary) {
        try {
            employee.setSalary(newSalary);
            logger.info("Zmieniono wynagrodzenie pracownika na: {}", newSalary);
        } catch (SalaryException e) {
            logger.error("Błąd zmiany wynagrodzenia: {}", e.getMessage(), e);
            System.err.println("Błąd zmiany wynagrodzenia: " + e.getMessage());
        }
    }

    /**
     * Resetuje ustawienia systemowe.
     */
    public void resetSystemSettings() {
        logger.info("Administrator {} resetuje ustawienia systemowe", employee.getLogin());
        System.out.println("Administrator " + employee.getLogin() + " resetuje ustawienia systemowe.");
    }

    /**
     * Generuje pełny raport systemowy.
     */
    public void generateFullSystemReport() {
        logger.info("Administrator {} generuje pełny raport systemowy (łącznie: {} pracowników)",
                employee.getLogin(), allEmployees.size());
        System.out.println("Administrator " + employee.getLogin() + " generuje pełny raport systemowy (łącznie: "
                + allEmployees.size() + " pracowników).");
    }
}