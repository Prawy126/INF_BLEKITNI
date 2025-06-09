/*
 * Classname: AbsenceRequest
 * Version information: 1.2
 * Date: 2025-06-04
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasa reprezentująca wniosek o nieobecność w systemie.
 * Mapowana na tabelę "Wnioski_o_nieobecnosc" w bazie danych.
 * Zawiera informacje o typie wniosku, datach nieobecności,
 * pracowniku składającym wniosek oraz statusie wniosku.
 */
@Entity
@Table(name = "Wnioski_o_nieobecnosc")
@Access(AccessType.FIELD)
public class AbsenceRequest {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą AbsenceRequest.
     */
    private static final Logger logger
            = LogManager.getLogger(AbsenceRequest.class);

    /**
     * Unikalny identyfikator wniosku generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    /**
     * Typ wniosku o nieobecność (np. urlop wypoczynkowy, urlop zdrowotny).
     */
    @Column(name = "Typ_wniosku", length = 100, nullable = false)
    private String requestType;

    /**
     * Data rozpoczęcia nieobecności.
     */
    @Column(name = "Data_rozpoczecia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    /**
     * Data zakończenia nieobecności.
     */
    @Column(name = "Data_zakonczenia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    /**
     * Dodatkowy opis wniosku o nieobecność.
     */
    @Column(name = "Opis", columnDefinition = "TEXT")
    private String description;

    /**
     * Status wniosku o nieobecność.
     * Domyślnie ustawiony na PENDING (oczekujący).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    /**
     * Pracownik składający wniosek o nieobecność.
     * Relacja wiele-do-jednego, gdzie wiele wniosków może być złożonych
     * przez jednego pracownika.
     */
    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee employee;

    /**
     * Enum reprezentujący możliwe statusy wniosku.
     * Mapuje wartości w bazie danych na reprezentacje w systemie.
     */
    public enum RequestStatus {
        /**
         * Wniosek oczekujący na rozpatrzenie.
         */
        PENDING("Oczekuje"),

        /**
         * Wniosek odrzucony.
         */
        REJECTED("Odrzucony"),

        /**
         * Wniosek zaakceptowany.
         */
        ACCEPTED("Przyjęty");

        private final String value;

        /**
         * Konstruktor enuma statusu.
         *
         * @param value polska nazwa statusu
         */
        RequestStatus(String value) {
            this.value = value;
        }

        /**
         * Zwraca polską nazwę statusu.
         *
         * @return polska nazwa statusu
         */
        public String getValue() {
            return value;
        }

        /**
         * Zwraca polską nazwę statusu jako reprezentację tekstową.
         *
         * @return polska nazwa statusu
         */
        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Domyślny konstruktor — tworzy pusty obiekt wniosku.
     * Loguje utworzenie nowego obiektu na poziomie DEBUG.
     */
    public AbsenceRequest() {
        logger.debug("Utworzono nowy obiekt AbsenceRequest" +
                " (konstruktor domyślny)");
    }

    /**
     * Konstruktor pełnoparametrowy.
     * Inicjalizuje wszystkie pola wniosku o nieobecność oraz loguje
     * utworzenie
     * wniosku.
     *
     * @param requestType typ wniosku o nieobecność
     * @param startDate data rozpoczęcia nieobecności
     * @param endDate data zakończenia nieobecności
     * @param description dodatkowy opis wniosku
     * @param employee pracownik składający wniosek
     * @param status początkowy status wniosku (jeśli null, ustawia PENDING)
     */
    public AbsenceRequest(String requestType,
                          Date startDate,
                          Date endDate,
                          String description,
                          Employee employee,
                          RequestStatus status) {
        this.requestType = requestType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.employee = employee;
        this.status = status != null ? status : RequestStatus.PENDING;

        logger.info("Utworzono wniosek o nieobecność:" +
                        " typ={}, od={}, do={}, pracownik={}",
                requestType,
                formatDate(startDate),
                formatDate(endDate),
                employee != null ? employee.getName() + " " +
                        employee.getSurname() : "null");
    }

    /**
     * Zwraca identyfikator wniosku.
     *
     * @return unikalny identyfikator wniosku
     */
    public int getId() {
        return id;
    }

    /**
     * Pobiera typ wniosku o nieobecność.
     * Operacja jest logowana na poziomie DEBUG.
     *
     * @return typ wniosku o nieobecność
     */
    public String getRequestType() {
        logger.debug("Pobrano typ wniosku: {}", requestType);
        return requestType;
    }

    /**
     * Ustawia typ wniosku o nieobecność.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param requestType nowy typ wniosku
     */
    public void setRequestType(String requestType) {
        logger.info("Zmieniono typ wniosku na: {}", requestType);
        this.requestType = requestType;
    }

    /**
     * Pobiera datę rozpoczęcia nieobecności.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return data rozpoczęcia nieobecności
     */
    public Date getStartDate() {
        logger.trace("Pobrano datę rozpoczęcia: {}",
                formatDate(startDate));
        return startDate;
    }

    /**
     * Ustawia datę rozpoczęcia nieobecności.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param startDate nowa data rozpoczęcia nieobecności
     */
    public void setStartDate(Date startDate) {
        logger.info("Zmieniono datę rozpoczęcia na: {}",
                formatDate(startDate));
        this.startDate = startDate;
    }

    /**
     * Pobiera datę zakończenia nieobecności.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return data zakończenia nieobecności
     */
    public Date getEndDate() {
        logger.trace("Pobrano datę zakończenia: {}",
                formatDate(endDate));
        return endDate;
    }

    /**
     * Ustawia datę zakończenia nieobecności.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param endDate nowa data zakończenia nieobecności
     */
    public void setEndDate(Date endDate) {
        logger.info("Zmieniono datę zakończenia na: {}",
                formatDate(endDate));
        this.endDate = endDate;
    }

    /**
     * Pobiera opis wniosku o nieobecność.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return opis wniosku
     */
    public String getDescription() {
        logger.trace("Pobrano opis wniosku.");
        return description;
    }

    /**
     * Ustawia opis wniosku o nieobecność.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param description nowy opis wniosku
     */
    public void setDescription(String description) {
        logger.info("Zaktualizowano opis wniosku.");
        this.description = description;
    }

    /**
     * Pobiera pracownika składającego wniosek.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return pracownik składający wniosek
     */
    public Employee getEmployee() {
        logger.trace("Pobrano pracownika: {} {}",
                employee != null ? employee.getName() : "null",
                employee != null ? employee.getSurname() : "");
        return employee;
    }

    /**
     * Ustawia pracownika składającego wniosek.
     * Jeśli wartość jest null, generuje ostrzeżenie w logu.
     * W przeciwnym wypadku loguje zmianę na poziomie INFO.
     *
     * @param employee nowy pracownik składający wniosek
     */
    public void setEmployee(Employee employee) {
        if (employee != null) {
            logger.info("Przypisano pracownika: {} {}",
                    employee.getName(), employee.getSurname());
        } else {
            logger.warn("Usunięto przypisanie pracownika (wartość null).");
        }
        this.employee = employee;
    }

    /**
     * Pobiera status wniosku.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return status wniosku
     */
    public RequestStatus getStatus() {
        logger.trace("Pobrano status wniosku: {}", status.getValue());
        return status;
    }

    /**
     * Ustawia status wniosku o nieobecność.
     * Jeśli nowa wartość jest null, ustawia domyślny status PENDING.
     * Operacja jest logowana na poziomie INFO wraz z informacją o poprzednim
     * statusie.
     *
     * @param status nowy status wniosku
     */
    public void setStatus(RequestStatus status) {
        RequestStatus oldStatus = this.status;
        this.status = status != null ? status : RequestStatus.PENDING;

        logger.info("Zmieniono status wniosku: {} → {}",
                oldStatus.getValue(), this.status.getValue());
    }

    /**
     * Metoda pomocnicza do formatowania daty bez rzucania wyjątków.
     * Jeśli data jest null, zwraca "null".
     * W przypadku błędu formatowania, loguje błąd i zwraca "nieznana data".
     *
     * @param date data do sformatowania
     * @return sformatowana data w formacie "yyyy-MM-dd" lub komunikat błędu
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
     * Przesłonięta metoda toString zwracająca tekstową reprezentację
     * wniosku.
     * Zawiera wszystkie istotne informacje o wniosku, w tym dane pracownika.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return tekstowa reprezentacja wniosku o nieobecność
     */
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedStartDate = startDate != null
                ? dateFormat.format(startDate) : "null";
        String formattedEndDate = endDate != null
                ? dateFormat.format(endDate) : "null";

        String empName = employee != null ? employee.getName() : "null";
        String empSurname = employee != null ? employee.getSurname() : "";

        String result = String.format(
                "AbsenceRequest{id=%d, type='%s', from=%s, to=%s," +
                        " description='%s', status='%s', employee=%s %s}",
                id,
                requestType,
                formattedStartDate,
                formattedEndDate,
                description,
                status,
                empName,
                empSurname);

        logger.trace("Wygenerowano toString(): {}", result);
        return result;
    }
}