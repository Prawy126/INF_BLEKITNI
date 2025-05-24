/*
 * Classname: AbsenceRequestTest
 * Version information: 1.2
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.AbsenceRequest;
import org.example.sys.Address;
import org.example.sys.Employee;

import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class AbsenceRequestTest {

    private AbsenceRequest request;
    private Employee mockEmployee;

    @BeforeEach
    void setup() {
        try {
            Address address = new Address();
            address.setTown("Warszawa");

            mockEmployee = new Employee(
                    "Jan", "Kowalski", 30, address,
                    "jankow", "bezpieczneHaslo123",
                    "Kasjer", new BigDecimal("4000")
            );

            request = new AbsenceRequest();
            request.setEmployee(mockEmployee);

        } catch (NameException | AgeException | PasswordException | SalaryException e) {
            fail("Błąd podczas tworzenia mockEmployee: " + e.getMessage());
        }
    }

    @Test
    void testSetAndGetTypWniosku() {
        request.setRequestType("Urlop");
        assertEquals("Urlop", request.getRequestType());
    }

    @Test
    void testSetAndGetDataRozpoczecia() {
        Date now = new Date();
        request.setStartDate(now);
        assertEquals(now, request.getStartDate());
    }

    @Test
    void testSetAndGetDataZakonczenia() {
        Date later = new Date(System.currentTimeMillis() + 86400000);
        request.setEndDate(later);
        assertEquals(later, request.getEndDate());
    }

    @Test
    void testSetAndGetOpis() {
        request.setDescription("Nieobecność z powodu choroby");
        assertEquals("Nieobecność z powodu choroby", request.getDescription());
    }

    @Test
    void testSetAndGetPracownik() {
        assertEquals("Jan", request.getEmployee().getName());
        assertEquals("Kowalski", request.getEmployee().getSurname());
    }

    @Test
    void testDefaultStatus() {
        assertEquals(AbsenceRequest.RequestStatus.PENDING, request.getStatus());
    }

    @Test
    void testSetAndGetStatus() {
        request.setStatus(AbsenceRequest.RequestStatus.ACCEPTED);
        assertEquals(AbsenceRequest.RequestStatus.ACCEPTED, request.getStatus());
    }

    @Test
    void testToStringIncludesFields() {
        request.setRequestType("Chorobowe");
        request.setDescription("Grypa");
        Date start = new Date();
        Date end = new Date(System.currentTimeMillis() + 2 * 86400000); // +2 dni
        request.setStartDate(start);
        request.setEndDate(end);
        request.setStatus(AbsenceRequest.RequestStatus.ACCEPTED);
        String result = request.toString();

        // Debug: opcjonalnie usuń później
        System.out.println("toString(): " + result);

        assertTrue(result.contains("Chorobowe"));
        assertTrue(result.contains("Grypa"));
        assertTrue(result.contains("Jan"));
        assertTrue(result.contains("Kowalski"));

        // UWAGA! Tutaj jest poprawna wartość zgodna z toString() enuma
        assertTrue(result.contains("Przyjęty")); // Zwróć uwagę na wielkość liter i ogonek!

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedStart = dateFormat.format(start);
        String formattedEnd = dateFormat.format(end);

        assertTrue(result.contains(formattedStart));
        assertTrue(result.contains(formattedEnd));
    }
}