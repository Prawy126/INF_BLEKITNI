/*
 * Classname: WorkloadRepositoryTest
 * Version information: 1.3
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.database.WorkloadRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.AfterAll;
import pdf.WorkloadReportGenerator.EmployeeWorkload;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkloadRepositoryTest {

    private static WorkloadRepository repo;
    private static LocalDate          startDate;
    private static LocalDate          endDate;
    private static List<EmployeeWorkload> result;

    @BeforeAll
    static void init() {
        repo      = new WorkloadRepository();
        startDate = LocalDate.of(2025, 4, 1);
        endDate   = LocalDate.of(2025, 4, 30);

        // samo wykonanie zapytania nie powinno rzucać wyjątku
        result = assertDoesNotThrow(
                () -> repo.getWorkloadData(startDate, endDate),
                "Pobranie danych nie powinno rzucić wyjątku"
        );
    }

    @Test @Order(1)
    void listIsNotNull() {
        assertNotNull(result, "Zwrócona lista nie może być null");
        // pusta lista jest OK, nie musimy nic więcej sprawdzać tutaj
    }

    @Test @Order(2)
    void everyItemHasValidFields() {
        for (EmployeeWorkload ew : result) {
            // employeeName()
            assertNotNull(ew.employeeName(), "employeeName nie może być null");
            assertFalse(ew.employeeName().isBlank(), "employeeName nie może być pusty");

            // department()
            assertNotNull(ew.department(), "department nie może być null");
            assertFalse(ew.department().isBlank(), "department nie może być pusty");

            // totalHours()
            assertTrue(ew.totalHours() >= 0,
                    () -> "totalHours musi być >= 0, a jest " + ew.totalHours());
        }
    }

    @Test @Order(3)
    void sumOfHoursReasonable() {
        double total = result.stream()
                .mapToDouble(EmployeeWorkload::totalHours)
                .sum();

        // Luźna reguła sanity-check:
        // max 24h * 31 dni = 744h
        double maxReasonable = 24 * 31;
        assertTrue(total <= maxReasonable,
                () -> "Suma godzin wydaje się podejrzanie duża: " + total);
    }

    @AfterAll
    static void tearDown() {
        repo.close();
    }
}