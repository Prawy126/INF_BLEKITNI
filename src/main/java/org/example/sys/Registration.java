/*
 * Classname: Registration
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

/**
 * Klasa reprezentująca rejestrację w systemie.
 * Zawiera informacje o wiadomości, imieniu, nazwisku, tytule,
 * dacie oraz statusie rejestracji.
 */
public class Registration {

    // Inicjalizacja logera
    private static final Logger logger
            = LogManager.getLogger(Registration.class);

    private String message;
    private String name;
    private String surname;
    private String title;
    private LocalDate date;
    private StatusRegistration status;

    /**
     * Domyślny konstruktor.
     * Używany przez Hibernate do tworzenia instancji klasy.
     */
    public Registration(
            String message,
            String name,
            String surname,
            LocalDate date,
            String title
    ) {
        this(message, name, surname, date, title, StatusRegistration.PENDING);
    }

    /**
     * Konstruktor z parametrami.
     * Umożliwia ustawienie wiadomości, imienia, nazwiska, tytułu,
     * daty oraz statusu rejestracji.
     *
     * @param message Wiadomość
     * @param name    Imię
     * @param surname Nazwisko
     * @param date    Data
     * @param title   Tytuł
     * @param status  Status rejestracji
     */
    public Registration(
            String message,
            String name,
            String surname,
            LocalDate date,
            String title,
            StatusRegistration status
    ) {
        this.message = message;
        this.name = name;
        this.surname = surname;
        this.title = title;
        this.date = date;
        this.status = status;

        logger.info("Utworzono nową rejestrację:" +
                " {} {}, tytuł: {}, data: {}", name, surname, title, date);
    }

    /**
     * Konstruktor z parametrami.
     * Umożliwia ustawienie wiadomości oraz daty rejestracji.
     *
     * @param message Wiadomość
     * @param date    Data
     */
    public Registration(String message, LocalDate date) {
        this.message = message;
        this.date = date;
        this.status = StatusRegistration.PENDING;

        logger.info("Utworzono nową uproszczoną rejestrację, data: {}",
                date);
    }

    /**
     * Domyślny konstruktor.
     * Używany przez Hibernate do tworzenia instancji klasy.
     */
    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDate() {
        return date;
    }

    public StatusRegistration getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
        logger.debug("Zaktualizowano wiadomość rejestracji.");
    }

    public void setTitle(String title) {
        this.title = title;
        logger.debug("Zaktualizowano tytuł rejestracji na: {}", title);
    }

    public void setName(String name) {
        this.name = name;
        logger.debug("Zaktualizowano imię rejestracji na: {}", name);
    }

    public void setSurname(String surname) {
        this.surname = surname;
        logger.debug("Zaktualizowano nazwisko rejestracji na: {}",
                surname);
    }

    public void setDate(LocalDate date) {
        this.date = date;
        logger.debug("Zaktualizowano datę rejestracji na: {}",
                date);
    }

    public void setStatus(StatusRegistration status) {
        logger.info("Zmieniono status rejestracji z {} na {}",
                this.status, status);
        this.status = status;
    }

    /**
     * Ustawia status rejestracji na zaakceptowany.
     */
    public void accept() {
        logger.info("Rejestracja została zaakceptowana.");
        this.status = StatusRegistration.ACCEPTED;
    }

    /**
     * Ustawia status rejestracji na odrzucony.
     */
    public void reject() {
        logger.info("Rejestracja została odrzucona.");
        this.status = StatusRegistration.REJECTED;
    }

    /**
     * Ustawia status rejestracji na zrealizowany.
     */
    public void realize() {
        logger.info("Rejestracja została zrealizowana.");
        this.status = StatusRegistration.COMPLETED;
    }

    /**
     * Ustawia status rejestracji na oczekujący.
     */
    @Override
    public String toString() {
        return String.format("Registration{name='%s %s', title='%s'," +
                        " date=%s, status=%s}",
                name, surname, title, date, status);
    }
}