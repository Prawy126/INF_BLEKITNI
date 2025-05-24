/*
 * Classname: RaportRepositoryTest
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.ReportRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.sys.Report;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReportRepositoryTest {

    private static ReportRepository raportRepo;
    private static UserRepository userRepo;
    private static Employee          employee;
    private static Report r1, r2;
    private static Date              sqlStart1, sqlEnd1, sqlStart2, sqlEnd2;

    @BeforeAll
    static void setup() {
        raportRepo = new ReportRepository();
        userRepo   = new UserRepository();

        // pick an existing employee
        List<Employee> emps = userRepo.getAllEmployees();  // userRepo methods still in Polish
        assertFalse(emps.isEmpty(), "Must have at least one employee");
        employee = emps.get(0);

        // prepare date ranges
        LocalDate ldStart1 = LocalDate.of(2025, 4, 1);
        LocalDate ldEnd1   = LocalDate.of(2025, 4, 30);
        LocalDate ldStart2 = LocalDate.of(2025, 5, 1);
        LocalDate ldEnd2   = LocalDate.of(2025, 5, 10);

        sqlStart1 = Date.valueOf(ldStart1);
        sqlEnd1   = Date.valueOf(ldEnd1);
        sqlStart2 = Date.valueOf(ldStart2);
        sqlEnd2   = Date.valueOf(ldEnd2);

        // construct two reports
        r1 = new Report("Raport sprzedaży", ldStart1, ldEnd1, employee, "raporty/sprzedaz_0425.pdf");
        r2 = new Report("Raport pracowników", ldStart2, ldEnd2, employee, "raporty/pracownicy_0525.pdf");
    }

    @Test
    @Order(1)
    void shouldAddReportsWithoutException() {
        assertDoesNotThrow(() -> {
            raportRepo.addReport(r1);
            raportRepo.addReport(r2);
        }, "Should add both reports without exception");

        assertTrue(r1.getId() > 0, "r1 should have an ID");
        assertTrue(r2.getId() > 0, "r2 should have an ID");
    }

    @Test
    @Order(2)
    void shouldFindAllAndUpdateReport() {
        List<Report> all = raportRepo.getAllReports();
        assertTrue(all.stream().anyMatch(r -> r.getId() == r1.getId()));
        assertTrue(all.stream().anyMatch(r -> r.getId() == r2.getId()));

        // update r1
        r1.setReportType("Raport sprzedaży — zmodyfikowany");
        r1.setFilePath("raporty/zmieniony_sprzedaz.pdf");
        assertDoesNotThrow(() -> raportRepo.updateReport(r1));

        // reload
        Report reloaded = raportRepo.findReportById(r1.getId());
        assertEquals("Raport sprzedaży — zmodyfikowany", reloaded.getReportType());
        assertEquals("raporty/zmieniony_sprzedaz.pdf", reloaded.getFilePath());
    }

    @Test
    @Order(3)
    void shouldQueryReportsByVariousCriteria() {
        // by type fragment
        List<Report> byType = raportRepo.findByType("sprzedaży");
        assertTrue(byType.stream().allMatch(r -> r.getReportType().toLowerCase().contains("sprzedaży")));

        // by start date range
        List<Report> byStart = raportRepo.findByStartDate(sqlStart1, sqlEnd1);
        assertTrue(byStart.stream().allMatch(r ->
                !r.getStartDate().isBefore(sqlStart1.toLocalDate()) &&
                        !r.getStartDate().isAfter(sqlEnd1.toLocalDate())
        ));

        // by end date range
        List<Report> byEnd = raportRepo.findByEndDate(sqlStart2, sqlEnd2);
        assertTrue(byEnd.stream().allMatch(r ->
                !r.getEndDate().isBefore(sqlStart2.toLocalDate()) &&
                        !r.getEndDate().isAfter(sqlEnd2.toLocalDate())
        ));

        // by employee
        List<Report> byEmp = raportRepo.findByEmployee(employee.getId());
        assertTrue(byEmp.stream().allMatch(r -> r.getEmployee().getId() == employee.getId()));

        // by file path fragment
        List<Report> byPath = raportRepo.findByFilePath("pracownicy");
        assertTrue(byPath.stream().allMatch(r ->
                r.getFilePath().toLowerCase().contains("pracownicy")));
    }

    @Test
    @Order(4)
    void shouldRemoveReportSuccessfully() {
        // delete r2
        assertDoesNotThrow(() -> raportRepo.removeReport(r2.getId()));
        assertNull(raportRepo.findReportById(r2.getId()), "r2 should no longer exist");

        // final list should not contain r2
        List<Report> allAfter = raportRepo.getAllReports();
        assertTrue(allAfter.stream().noneMatch(r -> r.getId() == r2.getId()));
    }

    @AfterAll
    static void tearDown() {
        raportRepo.close();
        userRepo.close();
    }
}
