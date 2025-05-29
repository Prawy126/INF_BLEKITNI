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

// Importy wyjątków
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.AgeException;

// Importy Log4j2
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger logger = LogManager.getLogger(Employee.class);

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
    private Address address;

    @Column(name = "Zarobki", precision = 10, scale = 2, nullable = false)
    private BigDecimal salary;

    @Column(name = "Stanowisko", nullable = false)
    private String position;

    @Column(name = "onSickLeave", nullable = false)
    private boolean onSickLeave;

    @Column(name = "sickLeaveStartDate")
    @Temporal(TemporalType.DATE)
    private Date sickLeaveStartDate;

    // Pole do usuwania miękkiego
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    /** lista powiązań do zadań przez tabelę łączącą */
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
    public Employee(String name, String surname, int age, String email,
                    String login, String password, Address address,
                    String position, BigDecimal salary)
            throws NameException, AgeException, PasswordException, SalaryException {
        super(name, surname, age, email);
        setLogin(login);
        setPassword(password);
        this.address = address;
        this.position = position;
        setSalary(salary);
        this.onSickLeave = false;
        logger.info("Utworzono pracownika: {} {}, stanowisko: {}", name, surname, position);
    }

    /**
     * Alternatywny konstruktor bez adresu e-mail.
     */
    public Employee(String name, String surname, int age, Address address,
                    String login, String password, String position, BigDecimal salary)
            throws NameException, AgeException, SalaryException, PasswordException {
        super(name, surname, age, null);
        this.address = address;
        setLogin(login);
        setPassword(password);
        this.position = position;
        setSalary(salary);
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
        logger.info("Utworzono pracownika bez e-maila: {} {}, stanowisko: {}", name, surname, position);
    }

    // === Gettery i settery z logowaniem ===

    public boolean isDeleted() {
        logger.trace("Pobrano status usunięcia pracownika: {}", deleted);
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        logger.info("Zmieniono status usunięcia pracownika na: {}", deleted);
        this.deleted = deleted;
    }

    public int getId() {
        logger.trace("Pobrano ID pracownika: {}", id);
        return id;
    }

    public void setId(int id) {
        logger.debug("Ustawiono ID pracownika: {}", id);
        this.id = id;
    }

    public String getLogin() {
        logger.trace("Pobrano login pracownika: {}", login);
        return login;
    }

    public void setLogin(String login) {
        if (login == null || login.isEmpty()) {
            logger.warn("Próbowano ustawić pusty login");
            throw new IllegalArgumentException("Login nie może być pusty");
        }
        logger.info("Zmieniono login pracownika na: {}", login);
        this.login = login;
    }

    public String getPassword() {
        logger.trace("Pobrano hasło pracownika");
        return password;
    }

    public void setPassword(String password) throws PasswordException {
        if (password == null || password.length() < 8) {
            logger.warn("Próbowano ustawić zbyt krótkie hasło");
            throw new PasswordException("Hasło musi mieć co najmniej 8 znaków");
        }
        logger.info("Zmieniono hasło pracownika");
        this.password = password;
    }

    public Address getAddress() {
        logger.trace("Pobrano adres pracownika");
        return address;
    }

    public void setAddress(Address address) {
        logger.info("Zaktualizowano adres pracownika");
        this.address = address;
    }

    public BigDecimal getSalary() {
        logger.trace("Pobrano wynagrodzenie pracownika: {}", salary);
        return salary;
    }

    public void setSalary(BigDecimal salary) throws SalaryException {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Próbowano ustawić nieprawidłowe wynagrodzenie: {}", salary);
            throw new SalaryException("Zarobki muszą być większe od zera");
        }
        logger.info("Zmieniono wynagrodzenie pracownika na: {}", salary);
        this.salary = salary;
    }

    public String getPosition() {
        logger.trace("Pobrano stanowisko pracownika: {}", position);
        return position;
    }

    public void setPosition(String position) {
        logger.info("Zmieniono stanowisko pracownika na: {}", position);
        this.position = position;
    }

    public boolean isOnSickLeave() {
        logger.trace("Sprawdzono czy pracownik jest na zwolnieniu: {}", onSickLeave);
        return onSickLeave;
    }

    public void startSickLeave(Date startDate) {
        logger.info("Rozpoczęto zwolnienie lekarskie, data rozpoczęcia: {}", startDate);
        this.sickLeaveStartDate = startDate;
        this.onSickLeave = true;
    }

    public boolean isRoot() {
        boolean isRoot = "root".equalsIgnoreCase(this.position);
        logger.info("Sprawdzono rolę użytkownika: {}", isRoot ? "root" : "nie root");
        return isRoot;
    }

    public Date getSickLeaveStartDate() {
        logger.trace("Pobrano datę rozpoczęcia zwolnienia: {}", sickLeaveStartDate);
        return sickLeaveStartDate;
    }

    public void endSickLeave() {
        logger.info("Zakończono zwolnienie lekarskie");
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
    }

    public List<TaskEmployee> getTaskEmployees() {
        logger.trace("Pobrano listę zadań pracownika (liczba: {})", taskEmployees.size());
        return taskEmployees;
    }

    public void addTaskEmployee(TaskEmployee taskEmployee) {
        if (taskEmployee != null && !taskEmployees.contains(taskEmployee)) {
            taskEmployees.add(taskEmployee);
            logger.info("Dodano zadanie do pracownika: {}", taskEmployee.getTask().getName());
        } else {
            logger.warn("Próbowano dodać istniejące lub nieprawidłowe powiązanie zadania");
        }
    }

    public void removeTaskEmployee(TaskEmployee taskEmployee) {
        if (taskEmployee != null && taskEmployees.contains(taskEmployee)) {
            taskEmployees.remove(taskEmployee);
            logger.info("Usunięto zadanie pracownika: {}", taskEmployee.getTask().getName());
        } else {
            logger.warn("Próbowano usunąć nieistniejące powiązanie zadania");
        }
    }
}