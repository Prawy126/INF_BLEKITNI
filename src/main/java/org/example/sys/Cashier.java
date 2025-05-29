/*
 * Classname: Cashier
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import java.math.BigDecimal;

// Importy Log4j2
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasa reprezentująca rolę kasjera – logiczną nakładkę na encję Employee.
 */
public class Cashier {

    private static final Logger logger = LogManager.getLogger(Cashier.class);

    private final Employee employee;

    /**
     * Tworzy nową instancję kasjera na podstawie istniejącego pracownika.
     *
     * @param employee Pracownik, który staje się kasjerem.
     */
    public Cashier(Employee employee) {
        this.employee = employee;
        logger.info("Utworzono kasjera: {} {}", employee.getName(), employee.getSurname());
    }

    /**
     * Zwraca pracownika, który jest kasjerem.
     *
     * @return Pracownik będący kasjerem.
     */
    public Employee getEmployee() {
        logger.debug("Pobrano pracownika-kasjera: {}", employee.getLogin());
        return employee;
    }

    /**
     * Zmienia hasło kasjera.
     */
    public void updatePassword(String newPassword) {
        try {
            employee.setPassword(newPassword);
            logger.info("Zmieniono hasło kasjera: {}", employee.getLogin());
        } catch (PasswordException e) {
            logger.error("Błąd zmiany hasła kasjera {}: {}", employee.getLogin(), e.getMessage(), e);
            System.err.println("Błąd zmiany hasła: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje wynagrodzenie kasjera.
     */
    public void updateSalary(BigDecimal newSalary) {
        try {
            employee.setSalary(newSalary);
            logger.info("Zmieniono wynagrodzenie kasjera {} na: {}", employee.getLogin(), newSalary);
        } catch (SalaryException e) {
            logger.error("Błąd zmiany wynagrodzenia kasjera {}: {}", employee.getLogin(), e.getMessage(), e);
            System.err.println("Błąd zmiany wynagrodzenia: " + e.getMessage());
        }
    }

    /**
     * Symuluje zeskanowanie produktu przez kasjera.
     */
    public void scanProduct(String productName) {
        logger.info("Kasjer {} zeskanował produkt: {}", employee.getLogin(), productName);
        System.out.println("Kasjer " + employee.getName() + " zeskanował produkt: " + productName);
    }

    /**
     * Symuluje zakończenie transakcji przez kasjera.
     */
    public void endTransaction() {
        logger.info("Kasjer {} zakończył transakcję", employee.getLogin());
        System.out.println("Kasjer " + employee.getName() + " zakończył transakcję.");
    }
}