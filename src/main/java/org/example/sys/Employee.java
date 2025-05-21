/*
 * Classname: Employee
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.AgeException;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;

import java.math.BigDecimal;
import java.util.Date;

@SqlResultSetMapping(
        name = "EmployeeWorkloadMapping",
        classes = @ConstructorResult(
                targetClass = pdf.WorkloadReportGenerator.EmployeeWorkload.class,
                columns = {
                        @ColumnResult(name = "employeeName", type = String.class),
                        @ColumnResult(name = "department", type = String.class),
                        @ColumnResult(name = "totalHours", type = Double.class)
                }
        )
)

/**
 * Klasa reprezentująca pracownika w systemie.
 */
@Entity
@Table(name = "Pracownicy")
@Access(AccessType.FIELD)
public class Employee extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Login", nullable = false)
    private String login;

    @Column(name = "Haslo", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "Id_adresu")
    private Address adres;

    @Column(name = "Zarobki", precision = 10, scale = 2, nullable = false)
    private BigDecimal zarobki;

    @Column(name = "Stanowisko", nullable = false)
    private String stanowisko;

    @Column(name = "onSickLeave", nullable = false)
    private boolean onSickLeave;

    @Column(name = "sickLeaveStartDate")
    @Temporal(TemporalType.DATE)
    private Date sickLeaveStartDate;

    // Dodane pole do usuwania miękkiego
    @Column(name = "usuniety", nullable = false)
    private boolean usuniety = false;

    /*
     * Konstruktor domyślny
     */
    public Employee() {}

    /**
     * Konstruktor z parametrami.
     *
     * @param name       Imię pracownika
     * @param surname    Nazwisko pracownika
     * @param age        Wiek pracownika
     * @param email      Adres e-mail pracownika
     * @param login      Login pracownika
     * @param password   Hasło pracownika
     * @param adres      Adres pracownika
     * @param stanowisko Stanowisko pracownika
     * @param zarobki    Zarobki pracownika
     */
    public Employee(String name, String surname, int age, String email,
                    String login, String password, Address adres,
                    String stanowisko, BigDecimal zarobki)
            throws NameException, AgeException, PasswordException, SalaryException {
        super(name, surname, age, email);
        setLogin(login);
        setPassword(password);
        this.adres = adres;
        this.stanowisko = stanowisko;
        setZarobki(zarobki);
        this.onSickLeave = false;
    }

    /**
     * Konstruktor z parametrami.
     *
     * @param name       Imię pracownika
     * @param surname    Nazwisko pracownika
     * @param age        Wiek pracownika
     * @param adres      Adres pracownika
     * @param login      Login pracownika
     * @param password   Hasło pracownika
     * @param stanowisko Stanowisko pracownika
     * @param zarobki    Zarobki pracownika
     */
    public Employee(String name, String surname, int age, Address adres,
                    String login, String password, String stanowisko, BigDecimal zarobki)
            throws NameException, AgeException, SalaryException, PasswordException {
        super(name, surname, age, null);
        this.adres = adres;
        setLogin(login);
        setPassword(password);
        this.stanowisko = stanowisko;
        setZarobki(zarobki);
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
    }

    // Getter i setter dla pola 'usuniety'
    /**
     * Zwraca informację, czy pracownik został usunięty.
     *
     * @return true, jeśli pracownik został usunięty, false w przeciwnym razie
     */
    public boolean isUsuniety() {
        return usuniety;
    }

    /**
     * Ustawia informację, czy pracownik został usunięty.
     *
     * @param usuniety true, jeśli pracownik został usunięty, false w przeciwnym razie
     */
    public void setUsuniety(boolean usuniety) {
        this.usuniety = usuniety;
    }

    /**
     * Zwraca identyfikator pracownika.
     *
     * @return Identyfikator pracownika
     */
    public int getId() {
        return id;
    }

    /**
     * Zwraca login pracownika.
     *
     * @return Login pracownika
     */
    public String getLogin() {
        return login;
    }

    /**
     * Ustawia login pracownika.
     *
     * @param login Login do ustawienia
     * @throws IllegalArgumentException jeśli login jest pusty
     */
    public void setLogin(String login) {
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("Login nie może być pusty");
        }
        this.login = login;
    }

    /**
     * Zwraca hasło pracownika.
     *
     * @return Hasło pracownika
     */
    public String getPassword() {
        return password;
    }

    /**
     * Ustawia hasło pracownika.
     *
     * @param password Hasło do ustawienia
     * @throws PasswordException jeśli hasło jest puste lub ma mniej niż 8 znaków
     */
    public void setPassword(String password) throws PasswordException {
        if (password == null || password.length() < 8) {
            throw new PasswordException("Hasło musi mieć co najmniej 8 znaków");
        }
        this.password = password;
    }

    /**
     * Zwraca adres pracownika.
     *
     * @return Adres pracownika
     */
    public Address getAdres() {
        return adres;
    }

    /**
     * Ustawia adres pracownika.
     *
     * @param adres Adres do ustawienia
     */
    public void setAdres(Address adres) {
        this.adres = adres;
    }

    /**
     * Zwraca zarobki pracownika.
     *
     * @return Zarobki pracownika
     */
    public BigDecimal getZarobki() {
        return zarobki;
    }

    /**
     * Ustawia zarobki pracownika.
     *
     * @param zarobki Zarobki do ustawienia
     * @throws SalaryException jeśli zarobki są mniejsze lub równe 0
     */
    public void setZarobki(BigDecimal zarobki) throws SalaryException {
        if (zarobki == null || zarobki.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SalaryException("Zarobki muszą być większe od zera");
        }
        this.zarobki = zarobki;
    }

    /**
     * Zwraca stanowisko pracownika.
     *
     * @return Stanowisko pracownika
     */
    public String getStanowisko() {
        return stanowisko;
    }

    /**
     * Ustawia stanowisko pracownika.
     *
     * @param stanowisko Stanowisko do ustawienia
     */
    public void setStanowisko(String stanowisko) {
        this.stanowisko = stanowisko;
    }

    /**
     * Zwraca informację, czy pracownik jest na zwolnieniu lekarskim.
     *
     * @return true, jeśli pracownik jest na zwolnieniu lekarskim, false w przeciwnym razie
     */
    public boolean isOnSickLeave() {
        return onSickLeave;
    }

    /**
     * Ustawia informację, czy pracownik jest na zwolnieniu lekarskim.
     *
     * @param startDate Data rozpoczęcia zwolnienia lekarskiego
     */
    public void startSickLeave(Date startDate) {
        this.sickLeaveStartDate = startDate;
        this.onSickLeave = true;
    }

    /**
     * Sprawdza, czy pracownik ma rolę "root".
     *
     * @return true jeśli pracownik ma rolę "root", false w przeciwnym przypadku
     */
    public boolean isRoot() {
        return "root".equalsIgnoreCase(this.stanowisko);
    }

    /**
     * Zwraca datę końca zwolnienia lekarskiego.
     *
     * @return Data końca zwolnienia lekarskiego
     */
    public Date getSickLeaveStartDate() {
        return sickLeaveStartDate;
    }

    /**
     * Ustawia końca zwolnienia lekarskiego.
     */
    public void endSickLeave() {
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
    }
}