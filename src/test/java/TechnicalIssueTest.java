

import org.example.sys.Employee;
import org.example.sys.TechnicalIssue;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla klasy TechnicalIssue.
 */
class TechnicalIssueTest {

    private TechnicalIssue issueNew;
    private TechnicalIssue issueExisting;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        date = LocalDate.of(2025, 5, 1);
        // konstruktor bez ID (nowe zgłoszenie)
        issueNew = new TechnicalIssue(
                "Awaria sprzętu",
                "Komputer nie startuje",
                date,
                new Employee(),  // zakładamy prostego Employee
                "Nowe"
        );
        // konstruktor z ID (istniejące zgłoszenie)
        issueExisting = new TechnicalIssue(
                42,
                "Błąd oprogramowania",
                "Aplikacja się zawiesza",
                date,
                new Employee(),
                "W toku"
        );
    }

    /**
     * Testuje konstruktor bez ID oraz gettery.
     */
    @Test
    void testConstructorWithoutIdAndGetters() {
        assertEquals("Awaria sprzętu", issueNew.getType(), "Typ powinien być ustawiony");
        assertEquals("Komputer nie startuje", issueNew.getDescription(), "Opis powinien być ustawiony");
        assertEquals(date, issueNew.getDateSubmitted(), "Data zgłoszenia powinna być ustawiona");
        assertNotNull(issueNew.getEmployee(), "Employee nie może być null");
        assertEquals("Nowe", issueNew.getStatus(), "Status domyślny powinien być 'Nowe'");
        // ID dla nowego jest domyślnie 0
        assertEquals(0, issueNew.getId(), "Nowe zgłoszenie powinno mieć id=0");
    }

    /**
     * Testuje konstruktor z ID oraz gettery.
     */
    @Test
    void testConstructorWithIdAndGetters() {
        assertEquals(42, issueExisting.getId(), "ID powinno być ustawione");
        assertEquals("Błąd oprogramowania", issueExisting.getType(), "Typ powinien być ustawiony");
        assertEquals("Aplikacja się zawiesza", issueExisting.getDescription(), "Opis powinien być ustawiony");
        assertEquals(date, issueExisting.getDateSubmitted(), "Data zgłoszenia powinna być ustawiona");
        assertNotNull(issueExisting.getEmployee(), "Employee nie może być null");
        assertEquals("W toku", issueExisting.getStatus(), "Status powinien być ustawiony");
    }

    /**
     * Testuje settery i ponowne odczytanie wartości.
     */
    @Test
    void testSetters() {
        TechnicalIssue issue = new TechnicalIssue();
        issue.setId(7);
        issue.setType("Inne");
        issue.setDescription("Testowy opis");
        issue.setDateSubmitted(LocalDate.of(2025, 6, 15));
        Employee emp = new Employee();
        emp.setId(99);
        issue.setEmployee(emp);
        issue.setStatus("Zamknięte");

        assertEquals(7, issue.getId(), "ID setter powinien działać");
        assertEquals("Inne", issue.getType(), "Type setter powinien działać");
        assertEquals("Testowy opis", issue.getDescription(), "Description setter powinien działać");
        assertEquals(LocalDate.of(2025, 6, 15), issue.getDateSubmitted(), "DateSubmitted setter powinien działać");
        assertEquals(99, issue.getEmployee().getId(), "Employee setter powinien działać");
        assertEquals("Zamknięte", issue.getStatus(), "Status setter powinien działać");
    }

    /**
     * Testuje zachowanie domyślnego konstruktora oraz ustawianie pól na null.
     */
    @Test
    void testDefaultConstructorAndNulls() {
        TechnicalIssue issue = new TechnicalIssue();
        issue.setType(null);
        issue.setDescription(null);
        issue.setDateSubmitted(null);
        issue.setEmployee(null);
        issue.setStatus(null);

        assertEquals(0, issue.getId(), "Domyślne ID powinno być 0");
        assertNull(issue.getType(), "Type może być null");
        assertNull(issue.getDescription(), "Description może być null");
        assertNull(issue.getDateSubmitted(), "DateSubmitted może być null");
        assertNull(issue.getEmployee(), "Employee może być null");
        assertNull(issue.getStatus(), "Status może być null");
    }
}
