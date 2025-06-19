/*
 * Classname: Employee
 * Version information: 1.3
 * Date: 2025-05-29
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Access;
import jakarta.persistence.OneToMany;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.AgeException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SqlResultSetMapping(
        name = "EmployeeWorkloadMapping",
        classes = @ConstructorResult(
                targetClass
                        = pdf.WorkloadReportGenerator.EmployeeWorkload.class,
                columns = {
                        @ColumnResult(name
                                = "employeeName", type = String.class),
                        @ColumnResult(name
                                = "department", type = String.class),
                        @ColumnResult(name
                                = "totalHours", type = Double.class)
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

    private static final Logger logger = LogManager.getLogger(Employee.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Login", nullable = false, length = 100)
    private String login;

    @Column(name = "Haslo", nullable = false, length = 100)
    private String password;

    @ManyToOne
    @JoinColumn(name = "Id_adresu")
    private Address address;

    @Column(name = "Zarobki", precision = 10, scale = 2, nullable = false)
    private BigDecimal salary;

    @Column(name = "Stanowisko", nullable = false, length = 100)
    private String position;

    @Column(name = "onSickLeave", nullable = false)
    private boolean onSickLeave;

    @Column(name = "sickLeaveStartDate")
    @Temporal(TemporalType.DATE)
    private Date sickLeaveStartDate;

    // Pole do usuwania miękkiego
    @Column(name = "usuniety", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean deleted = false;

    /** lista powiązań do zadań przez tabelę łączącą */
    @OneToMany(mappedBy = "employee", fetch
            = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TaskEmployee> taskEmployees = new ArrayList<>();

    /**
     * Domyślny konstruktor – logowanie tworzenia nowego obiektu.
     */
    public Employee() {
        logger.debug("Utworzono nowego pracownika (konstruktor domyślny)");
    }

    /**
     * Konstruktor z parametrami.
     *
     * @param name       Imię pracownika
     * @param surname    Nazwisko pracownika
     * @param age        Wiek pracownika
     * @param email      Adres e-mail pracownika
     * @param login      Login pracownika
     * @param password   Hasło pracownika
     * @param address      Adres pracownika
     * @param position Stanowisko pracownika
     * @param salary    Zarobki pracownika
     */
    public Employee(String name,
                    String surname,
                    int age,
                    String email,
                    String login,
                    String password,
                    Address address,
                    String position,
                    BigDecimal salary
    ) throws NameException,
            AgeException,
            PasswordException,
            SalaryException {
        super(name, surname, age, email);
        setLogin(login);
        setPassword(password);
        this.address = address;
        this.position = position;
        setSalary(salary);
        this.onSickLeave = false;
        logger.info("Utworzono pracownika: {} {}, stanowisko: {}",
                name, surname, position);
    }

    /**
     * Alternatywny konstruktor bez adresu e-mail.
     */
    public Employee(String name,
                    String surname,
                    int age,
                    Address address,
                    String login,
                    String password,
                    String position,
                    BigDecimal salary
    ) throws NameException,
            AgeException,
            SalaryException,
            PasswordException
    {
        super(name, surname, age, null);
        this.address = address;
        setLogin(login);
        setPassword(password);
        this.position = position;
        setSalary(salary);
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
        logger.info("Utworzono pracownika bez e-maila: {} {}," +
                " stanowisko: {}", name, surname, position);
    }

    // === Gettery i settery z logowaniem ===

    /**
     * Sprawdza czy pracownik jest oznaczony jako usunięty.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return true jeśli pracownik jest oznaczony jako usunięty,
     *         false w przeciwnym przypadku
     */
    public boolean isDeleted() {
        logger.trace("Pobrano status usunięcia pracownika: {}",
                deleted);
        return deleted;
    }

    /**
     * Ustawia status usunięcia pracownika.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param deleted nowy status usunięcia pracownika
     */
    public void setDeleted(boolean deleted) {
        logger.info("Zmieniono status usunięcia pracownika na: {}",
                deleted);
        this.deleted = deleted;
    }

    /**
     * Pobiera identyfikator pracownika.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return unikalny identyfikator pracownika
     */
    public int getId() {
        logger.trace("Pobrano ID pracownika: {}", id);
        return id;
    }

    /**
     * Ustawia identyfikator pracownika.
     * Operacja jest logowana na poziomie DEBUG.
     *
     * @param id nowy identyfikator pracownika
     */
    public void setId(int id) {
        logger.debug("Ustawiono ID pracownika: {}", id);
        this.id = id;
    }

    /**
     * Pobiera login pracownika.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return login pracownika używany do logowania w systemie
     */
    public String getLogin() {
        logger.trace("Pobrano login pracownika: {}", login);
        return login;
    }

    /**
     * Ustawia login pracownika.
     * Weryfikuje czy login nie jest pusty oraz czy nie przekracza
     * maksymalnej długości (100 znaków).
     * W przypadku próby ustawienia nieprawidłowego loginu, rzuca wyjątek.
     * Operacja jest logowana na poziomie INFO lub WARN w przypadku błędu.
     *
     * @param login nowy login pracownika
     * @throws IllegalArgumentException jeśli login jest pusty, null lub
     * zbyt długi
     */
    public void setLogin(String login) {
        if (login == null || login.isEmpty()) {
            logger.warn("Próbowano ustawić pusty login");
            throw new IllegalArgumentException("Login nie może być pusty");
        }
        if (login.length() > 100) {
            logger.warn("Próbowano ustawić zbyt długi login: {} znaków", login.length());
            throw new IllegalArgumentException("Login nie może być dłuższy niż 100 znaków");
        }
        logger.info("Zmieniono login pracownika na: {}", login);
        this.login = login;
    }

    /**
     * Pobiera hasło pracownika.
     * Ze względów bezpieczeństwa samo hasło nie jest logowane.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return zaszyfrowane hasło pracownika
     */
    public String getPassword() {
        logger.trace("Pobrano hasło pracownika");
        return password;
    }

    /**
     * Ustawia hasło pracownika.
     * Weryfikuje minimalną długość hasła (co najmniej 8 znaków) oraz
     * maksymalną długość (100 znaków).
     * W przypadku próby ustawienia nieprawidłowego hasła, rzuca wyjątek.
     * Operacja jest logowana na poziomie INFO lub WARN w przypadku błędu.
     *
     * @param password nowe hasło pracownika
     * @throws PasswordException jeśli hasło jest zbyt krótkie, zbyt długie
     * lub null
     */
    public void setPassword(String password) throws PasswordException {
        if (password == null || password.length() < 8) {
            logger.warn("Próbowano ustawić zbyt krótkie hasło");
            throw new PasswordException("Hasło musi mieć co najmniej 8 znaków");
        }
        if (password.length() > 100) {
            logger.warn("Próbowano ustawić zbyt długie hasło: {} znaków",
                    password.length());
            throw new PasswordException("Hasło nie może być dłuższe niż 100 znaków");
        }
        logger.info("Zmieniono hasło pracownika");
        this.password = password;
    }

    /**
     * Pobiera adres pracownika.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return obiekt adresu przypisany do pracownika
     */
    public Address getAddress() {
        logger.trace("Pobrano adres pracownika");
        return address;
    }

    /**
     * Ustawia adres pracownika.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param address nowy adres pracownika
     */
    public void setAddress(Address address) {
        logger.info("Zaktualizowano adres pracownika");
        this.address = address;
    }

    /**
     * Pobiera wynagrodzenie pracownika.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return kwota wynagrodzenia pracownika
     */
    public BigDecimal getSalary() {
        logger.trace("Pobrano wynagrodzenie pracownika: {}", salary);
        return salary;
    }

    /**
     * Ustawia wynagrodzenie pracownika.
     * Weryfikuje czy wynagrodzenie jest większe od zera.
     * W przypadku próby ustawienia nieprawidłowego wynagrodzenia,
     * rzuca wyjątek.
     * Operacja jest logowana na poziomie INFO lub WARN w przypadku błędu.
     *
     * @param salary nowe wynagrodzenie pracownika
     * @throws SalaryException jeśli wynagrodzenie jest mniejsze lub równe
     * zero albo null
     */
    public void setSalary(BigDecimal salary) throws SalaryException {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Próbowano ustawić nieprawidłowe" +
                    " wynagrodzenie: {}", salary);
            throw new SalaryException("Zarobki muszą być większe od zera");
        }
        logger.info("Zmieniono wynagrodzenie pracownika na: {}",
                salary);
        this.salary = salary;
    }

    /**
     * Pobiera stanowisko pracownika.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return nazwa stanowiska pracownika
     */
    public String getPosition() {
        logger.trace("Pobrano stanowisko pracownika: {}", position);
        return position;
    }

    /**
     * Ustawia stanowisko pracownika.
     * Weryfikuje czy stanowisko nie przekracza maksymalnej długości
     * (100 znaków).
     * Operacja jest logowana na poziomie INFO lub WARN w przypadku błędu.
     *
     * @param position nowe stanowisko pracownika
     * @throws IllegalArgumentException jeśli stanowisko jest zbyt długie
     */
    public void setPosition(String position) {
        if (position != null && position.length() > 100) {
            logger.warn("Próbowano ustawić zbyt długie stanowisko: {} znaków",
                    position.length());
            throw new IllegalArgumentException("Stanowisko nie może być dłuższe" +
                    " niż 100 znaków");
        }
        logger.info("Zmieniono stanowisko pracownika na: {}", position);
        this.position = position;
    }

    /**
     * Sprawdza czy pracownik przebywa na zwolnieniu lekarskim.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return true jeśli pracownik jest na zwolnieniu lekarskim,
     *         false w przeciwnym przypadku
     */
    public boolean isOnSickLeave() {
        logger.trace("Sprawdzono czy pracownik jest na zwolnieniu: {}",
                onSickLeave);
        return onSickLeave;
    }

    /**
     * Rozpoczyna zwolnienie lekarskie dla pracownika.
     * Ustawia datę rozpoczęcia zwolnienia oraz zmienia status na aktywny.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param startDate data rozpoczęcia zwolnienia lekarskiego
     */
    public void startSickLeave(Date startDate) {
        logger.info("Rozpoczęto zwolnienie lekarskie," +
                " data rozpoczęcia: {}", startDate);
        this.sickLeaveStartDate = startDate;
        this.onSickLeave = true;
    }

    /**
     * Sprawdza czy pracownik posiada uprawnienia administratora (root).
     * Sprawdzenie bazuje na porównaniu stanowiska z wartością "root".
     * Operacja jest logowana na poziomie INFO.
     *
     * @return true jeśli pracownik ma uprawnienia administratora,
     *         false w przeciwnym przypadku
     */
    public boolean isRoot() {
        boolean isRoot = "root".equalsIgnoreCase(this.position);
        logger.info("Sprawdzono rolę użytkownika:" +
                " {}", isRoot ? "root" : "nie root");
        return isRoot;
    }

    /**
     * Pobiera datę rozpoczęcia zwolnienia lekarskiego.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return data rozpoczęcia zwolnienia lekarskiego
     *         lub null jeśli pracownik nie jest na zwolnieniu
     */
    public Date getSickLeaveStartDate() {
        logger.trace("Pobrano datę rozpoczęcia zwolnienia:" +
                " {}", sickLeaveStartDate);
        return sickLeaveStartDate;
    }

    /**
     * Kończy zwolnienie lekarskie pracownika.
     * Resetuje status zwolnienia oraz datę jego rozpoczęcia.
     * Operacja jest logowana na poziomie INFO.
     */
    public void endSickLeave() {
        logger.info("Zakończono zwolnienie lekarskie");
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
    }

    /**
     * Pobiera listę zadań przypisanych do pracownika.
     * Operacja jest logowana na poziomie TRACE wraz z liczbą zadań.
     *
     * @return lista obiektów TaskEmployee powiązanych z pracownikiem
     */
    public List<TaskEmployee> getTaskEmployees() {
        logger.trace("Pobrano listę zadań pracownika (liczba: {})",
                taskEmployees.size());
        return taskEmployees;
    }

    /**
     * Dodaje nowe zadanie do listy zadań pracownika.
     * Nie pozwala na dodanie zduplikowanych lub nieprawidłowych zadań.
     * Operacja jest logowana na poziomie INFO lub WARN w przypadku błędu.
     *
     * @param taskEmployee obiekt powiązania zadania z pracownikiem
     */
    public void addTaskEmployee(TaskEmployee taskEmployee) {
        if (taskEmployee != null && !taskEmployees.contains(taskEmployee)) {
            taskEmployees.add(taskEmployee);
            logger.info("Dodano zadanie do pracownika: {}",
                    taskEmployee.getTask().getName());
        } else {
            logger.warn("Próbowano dodać istniejące lub" +
                    " nieprawidłowe powiązanie zadania");
        }
    }

    /**
     * Usuwa zadanie z listy zadań pracownika.
     * Sprawdza czy zadanie istnieje przed jego usunięciem.
     * Operacja jest logowana na poziomie INFO lub WARN w przypadku błędu.
     *
     * @param taskEmployee obiekt powiązania zadania z pracownikiem do
     *                    usunięcia
     */
    public void removeTaskEmployee(TaskEmployee taskEmployee) {
        if (taskEmployee != null && taskEmployees.contains(taskEmployee)) {
            taskEmployees.remove(taskEmployee);
            logger.info("Usunięto zadanie pracownika: {}",
                    taskEmployee.getTask().getName());
        } else {
            logger.warn("Próbowano usunąć nieistniejące powiązanie zadania");
        }
    }
}