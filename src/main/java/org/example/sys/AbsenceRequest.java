/*
 * Classname: AbsenceRequest
 * Version information: 1.1
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

// Importy Log4j2
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = "Wnioski_o_nieobecnosc")
@Access(AccessType.FIELD)
public class AbsenceRequest {

    private static final Logger logger = LogManager.getLogger(AbsenceRequest.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Typ_wniosku", length = 100, nullable = false)
    private String requestType;

    @Column(name = "Data_rozpoczecia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "Data_zakonczenia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "Opis", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private RequestStatus status = RequestStatus.PENDING; // domyślna wartość

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee employee;

    // Enum odpowiadający możliwym statusom w tabeli SQL
    public enum RequestStatus {
        PENDING("Oczekuje"),
        NOTACCEPTED("Nie przyjęty"),
        ACCEPTED("Przyjęty");

        private final String value;

        RequestStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Domyślny konstruktor — logowanie tworzenia nowego obiektu.
     */
    public AbsenceRequest() {
        logger.debug("Utworzono nowy obiekt AbsenceRequest (konstruktor domyślny)");
    }

    /**
     * Konstruktor pełnoparametrowy — logowanie inicjalizacji wartości.
     */
    public AbsenceRequest(String requestType, Date startDate, Date endDate,
                          String description, Employee employee, RequestStatus status) {
        this.requestType = requestType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.employee = employee;
        this.status = status != null ? status : RequestStatus.PENDING;

        logger.info("Utworzono wniosek o nieobecność: typ={}, od={}, do={}, pracownik={}",
                requestType,
                formatDate(startDate),
                formatDate(endDate),
                employee != null ? employee.getName() + " " + employee.getSurname() : "null");
    }

    // === Gettery i settery z logowaniem ===

    public int getId() {
        return id;
    }

    public String getRequestType() {
        logger.debug("Pobrano typ wniosku: {}", requestType);
        return requestType;
    }

    public void setRequestType(String requestType) {
        logger.info("Zmieniono typ wniosku na: {}", requestType);
        this.requestType = requestType;
    }

    public Date getStartDate() {
        logger.trace("Pobrano datę rozpoczęcia: {}", formatDate(startDate));
        return startDate;
    }

    public void setStartDate(Date startDate) {
        logger.info("Zmieniono datę rozpoczęcia na: {}", formatDate(startDate));
        this.startDate = startDate;
    }

    public Date getEndDate() {
        logger.trace("Pobrano datę zakończenia: {}", formatDate(endDate));
        return endDate;
    }

    public void setEndDate(Date endDate) {
        logger.info("Zmieniono datę zakończenia na: {}", formatDate(endDate));
        this.endDate = endDate;
    }

    public String getDescription() {
        logger.trace("Pobrano opis wniosku.");
        return description;
    }

    public void setDescription(String description) {
        logger.info("Zaktualizowano opis wniosku.");
        this.description = description;
    }

    public Employee getEmployee() {
        logger.trace("Pobrano pracownika: {} {}",
                employee != null ? employee.getName() : "null",
                employee != null ? employee.getSurname() : "");
        return employee;
    }

    public void setEmployee(Employee employee) {
        if (employee != null) {
            logger.info("Przypisano pracownika: {} {}", employee.getName(), employee.getSurname());
        } else {
            logger.warn("Usunięto przypisanie pracownika (wartość null).");
        }
        this.employee = employee;
    }

    public RequestStatus getStatus() {
        logger.trace("Pobrano status wniosku: {}", status.getValue());
        return status;
    }

    public void setStatus(RequestStatus status) {
        RequestStatus oldStatus = this.status;
        this.status = status != null ? status : RequestStatus.PENDING;

        logger.info("Zmieniono status wniosku: {} → {}", oldStatus.getValue(), this.status.getValue());
    }

    /**
     * Metoda pomocnicza do formatowania daty bez rzucania wyjątków.
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "null";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        } catch (Exception e) {
            logger.error("Błąd podczas formatowania daty.", e);
            return "nieznana data";
        }
    }

    /**
     * Przesłonięta metoda toString z logowaniem.
     */
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedStartDate = startDate != null ? dateFormat.format(startDate) : "null";
        String formattedEndDate = endDate != null ? dateFormat.format(endDate) : "null";

        String empName = employee != null ? employee.getName() : "null";
        String empSurname = employee != null ? employee.getSurname() : "";

        String result = String.format(
                "AbsenceRequest{id=%d, type='%s', from=%s, to=%s, description='%s', status='%s', employee=%s %s}",
                id, requestType, formattedStartDate, formattedEndDate, description, status, empName, empSurname);

        logger.trace("Wygenerowano toString(): {}", result);
        return result;
    }
}