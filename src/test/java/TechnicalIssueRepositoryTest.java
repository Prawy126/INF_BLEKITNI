/*
 * Classname: TechnicalIssueRepositoryTest
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


import org.example.database.TechnicalIssueRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.sys.TechnicalIssue;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TechnicalIssueRepositoryTest {

    private static TechnicalIssueRepository issueRepo;
    private static UserRepository userRepo;

    private static Employee      employee;
    private static TechnicalIssue issue;
    private static LocalDate     testDate;

    @BeforeAll
    static void setup() {
        issueRepo = new TechnicalIssueRepository();
        userRepo  = new UserRepository();

        // pick an existing employee
        List<Employee> emps = userRepo.getAllEmployess();
        assertFalse(emps.isEmpty(), "At least one Employee must exist");
        employee = emps.get(0);

        // fixed test date
        testDate = LocalDate.of(2025, 6, 15);
    }

    @Test
    @Order(1)
    void testAddIssue() {
        issue = new TechnicalIssue();
        issue.setType("Awaria terminala");
        issue.setDescription("Terminal płatniczy nie działa.");
        issue.setDateSubmitted(testDate);
        issue.setStatus("Nowe");
        issue.setEmployee(employee);

        assertDoesNotThrow(() -> issueRepo.addIssue(issue),
                "Should add TechnicalIssue without exception");
        assertTrue(issue.getId() > 0, "Issue should receive an ID");
    }

    @Test
    @Order(2)
    void testFindAllAndReadById() {
        List<TechnicalIssue> all = issueRepo.getAllIssues();
        assertTrue(all.stream().anyMatch(i -> i.getId() == issue.getId()),
                "New issue should appear in all issues");

        TechnicalIssue loaded = issueRepo.findIssueById(issue.getId());
        assertNotNull(loaded, "Should find issue by ID");
        assertEquals("Awaria terminala", loaded.getType());
    }

    @Test
    @Order(3)
    void testUpdate() {
        issue.setStatus("W trakcie");
        issue.setDescription("Zgłoszenie przekazane do serwisu.");
        assertDoesNotThrow(() -> issueRepo.updateIssue(issue),
                "Should update issue without exception");

        TechnicalIssue reloaded = issueRepo.findIssueById(issue.getId());
        assertNotNull(reloaded);
        assertEquals("W trakcie", reloaded.getStatus());
        assertEquals("Zgłoszenie przekazane do serwisu.", reloaded.getDescription());
    }

    @Test
    @Order(4)
    void testSearchMethods() {
        // by type fragment
        List<TechnicalIssue> byType = issueRepo.findByType("Awaria");
        assertTrue(byType.stream().allMatch(i -> i.getType().contains("Awaria")));

        // by date range [testDate-1, testDate+1]
        LocalDate start = testDate.minusDays(1);
        LocalDate end   = testDate.plusDays(1);
        List<TechnicalIssue> byDate = issueRepo.findByDate(start, end);
        assertTrue(byDate.stream().anyMatch(i -> i.getId() == issue.getId()));

        // by exact status
        List<TechnicalIssue> byStatus = issueRepo.findByStatus("W trakcie");
        assertTrue(byStatus.stream().allMatch(i -> i.getStatus().equals("W trakcie")));

        // by employee
        List<TechnicalIssue> byEmp = issueRepo.findByEmployee(employee.getId());
        assertTrue(byEmp.stream().allMatch(i -> i.getEmployee().getId() == employee.getId()));
    }

    @Test
    @Order(5)
    void testDelete() {
        assertDoesNotThrow(() -> issueRepo.removeIssue(issue),
                "Should delete issue without exception");

        assertNull(issueRepo.findIssueById(issue.getId()),
                "Deleted issue should no longer be found");

        List<TechnicalIssue> allAfter = issueRepo.getAllIssues();
        assertTrue(allAfter.stream().noneMatch(i -> i.getId() == issue.getId()));
    }

    @AfterAll
    static void tearDown() {
        issueRepo.close();
        userRepo.close();
    }
}