/*
 * Classname: Person
 * Version information: 1.1
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;

/**
 * Klasa bazowa reprezentująca osobę w systemie.
 * Zawiera podstawowe informacje o osobie, takie jak imię, nazwisko, wiek i adres e-mail.
 * Klasa jest mapowana do bazy danych jako klasa nadrzędna.
 */
@MappedSuperclass
public abstract class Person {

    // Inicjalizacja logera
    private static final Logger logger = LogManager.getLogger(Person.class);

    @Column(name = "Imie")
    private String name;

    @Column(name = "Nazwisko")
    private String surname;

    @Column(name = "Wiek")
    private int age;

    @Column(name = "Email")
    private String email;

    /**
     * Domyślny konstruktor.
     * Używany przez Hibernate do tworzenia instancji klasy.
     */
    public Person() {
        logger.debug("Utworzono nową instancję Person (domyślny konstruktor).");
    }

    /**
     * Konstruktor z parametrami.
     * Umożliwia ustawienie imienia, nazwiska, wieku i adresu e-mail osoby.
     *
     * @param name    Imię osoby
     * @param surname Nazwisko osoby
     * @param age     Wiek osoby
     * @param email   Adres e-mail osoby
     * @throws AgeException  Jeśli wiek jest nieprawidłowy
     * @throws NameException Jeśli imię lub nazwisko są nieprawidłowe
     */
    public Person(String name, String surname, int age, String email)
            throws AgeException, NameException {
        setName(name);
        setSurname(surname);
        setAge(age);
        setEmail(email);

        logger.info("Utworzono osobę: {} {}, wiek: {}, email: {}", name, surname, age, email);
    }

    /**
     * Metoda zwracająca imię osoby.
     *
     * @return Imię osoby
     */
    public String getName() {
        return name;
    }

    /**
     * Metoda ustawiająca imię osoby.
     *
     * @param name Imię do ustawienia
     * @throws NameException Jeśli imię jest nieprawidłowe
     */
    public void setName(String name) throws NameException {
        if (name == null || name.isEmpty()) {
            logger.warn("Próba ustawienia pustego imienia.");
            throw new NameException("Imię nie może być puste");
        } else if (name.length() < 2) {
            logger.warn("Imię za krótkie: {}", name);
            throw new NameException("Imię musi mieć co najmniej 2 znaki");
        }
        this.name = name;
        logger.debug("Zaktualizowano imię: {}", name);
    }

    /**
     * Metoda zwracająca nazwisko osoby.
     *
     * @return Nazwisko osoby
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Metoda ustawiająca nazwisko osoby.
     *
     * @param surname Nazwisko do ustawienia
     * @throws NameException Jeśli nazwisko jest nieprawidłowe
     */
    public void setSurname(String surname) throws NameException {
        if (surname == null || surname.isEmpty()) {
            logger.warn("Próba ustawienia pustego nazwiska.");
            throw new NameException("Nazwisko nie może być puste");
        } else if (surname.length() < 2) {
            logger.warn("Nazwisko za krótkie: {}", surname);
            throw new NameException("Nazwisko musi mieć co najmniej 2 znaki");
        }
        this.surname = surname;
        logger.debug("Zaktualizowano nazwisko: {}", surname);
    }

    /**
     * Metoda zwracająca wiek osoby.
     *
     * @return Wiek osoby
     */
    public int getAge() {
        return age;
    }

    /**
     * Metoda ustawiająca wiek osoby.
     *
     * @param age Wiek do ustawienia
     * @throws AgeException Jeśli wiek jest nieprawidłowy
     */
    public void setAge(int age) throws AgeException {
        if (age < 0 || age > 120) {
            logger.warn("Nieprawidłowy wiek: {}", age);
            throw new AgeException("Wiek musi być z przedziału 0–120");
        } else if (age < 18) {
            logger.warn("Osoba młodsza niż 18 lat: {}", age);
            throw new AgeException("Osoba musi mieć co najmniej 18 lat");
        }
        this.age = age;
        logger.debug("Zaktualizowano wiek: {}", age);
    }

    /**
     * Metoda zwracająca adres e-mail osoby.
     *
     * @return Adres e-mail osoby
     */
    public String getEmail() {
        return email;
    }

    /**
     * Metoda ustawiająca adres e-mail osoby.
     *
     * @param email Adres e-mail do ustawienia
     */
    public void setEmail(String email) {
        this.email = email;
        logger.debug("Zaktualizowano email: {}", email);
    }

    /**
     * Metoda zwracająca reprezentację tekstową osoby.
     *
     * @return Reprezentacja tekstowa osoby
     */
    @Override
    public String toString() {
        return name + " " + surname + " (" + age + "), " + email;
    }
}