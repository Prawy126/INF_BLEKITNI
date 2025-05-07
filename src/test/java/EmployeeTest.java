import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void testConstructorInitialization() throws NameException, AgeException, PasswordException, SalaryException {
        Address address = new Address();
        address.setMiasto("Warszawa");

        Employee employee = new Employee(
                "John", "Doe", 30, "john@example.com",
                "jdoe", "pass12345", address, "Manager", new BigDecimal("5000.00")
        );

        assertEquals("John", employee.getName());
        assertEquals("Doe", employee.getSurname());
        assertEquals(30, employee.getAge());
        assertEquals("john@example.com", employee.getEmail());
        assertEquals("jdoe", employee.getLogin());
        assertEquals("pass12345", employee.getPassword());
        assertEquals("Manager", employee.getStanowisko());
        assertEquals(new BigDecimal("5000.00"), employee.getZarobki());
        assertFalse(employee.isOnSickLeave());
        assertNull(employee.getSickLeaveStartDate());
        assertEquals("Warszawa", employee.getAdres().getMiasto());
    }

    @Test
    void testSettersAndGetters() throws PasswordException, SalaryException, AgeException, NameException {
        Employee employee = new Employee();
        Address address = new Address();
        address.setMiasto("Lublin");

        employee.setName("Jane");
        employee.setSurname("Smith");
        employee.setAge(25);
        employee.setEmail("jane@example.com");
        employee.setLogin("jsmith");
        employee.setPassword("securepass");
        employee.setStanowisko("Developer");
        employee.setZarobki(new BigDecimal("6000.00"));
        employee.setAdres(address);

        assertEquals("Jane", employee.getName());
        assertEquals("Smith", employee.getSurname());
        assertEquals(25, employee.getAge());
        assertEquals("jane@example.com", employee.getEmail());
        assertEquals("jsmith", employee.getLogin());
        assertEquals("securepass", employee.getPassword());
        assertEquals("Developer", employee.getStanowisko());
        assertEquals(new BigDecimal("6000.00"), employee.getZarobki());
        assertEquals("Lublin", employee.getAdres().getMiasto());
    }

    @Test
    void testStartSickLeave() throws NameException, AgeException, PasswordException, SalaryException {
        Address address = new Address();
        address.setMiasto("Kraków");

        Employee employee = new Employee(
                "Anna", "Nowak", 29, "anna@ex.com",
                "anowak", "haslo1234", address, "Sprzedawca", new BigDecimal("4200")
        );

        Date startDate = new Date();
        employee.startSickLeave(startDate);

        assertTrue(employee.isOnSickLeave());
        assertEquals(startDate, employee.getSickLeaveStartDate());
    }

    @Test
    void testDefaultValues() {
        Employee employee = new Employee();
        assertNull(employee.getName());
        assertNull(employee.getSurname());
        assertEquals(0, employee.getAge());
        assertNull(employee.getEmail());
        assertNull(employee.getLogin());
        assertNull(employee.getPassword());
        assertNull(employee.getStanowisko());
        assertNull(employee.getZarobki());
        assertNull(employee.getAdres());
        assertFalse(employee.isOnSickLeave());
        assertNull(employee.getSickLeaveStartDate());
    }

    @Test
    void testEndSickLeave() throws NameException, AgeException, PasswordException, SalaryException {
        Address address = new Address();
        address.setMiasto("Gdańsk");

        Employee employee = new Employee(
                "Tomasz", "Lis", 45, "tomasz@abc.pl",
                "tlis", "bezpieczneHaslo", address, "Kierownik", new BigDecimal("7500")
        );

        Date startDate = new Date();
        employee.startSickLeave(startDate);
        assertTrue(employee.isOnSickLeave());

        employee.endSickLeave();
        assertFalse(employee.isOnSickLeave());
        assertNull(employee.getSickLeaveStartDate());
    }
}
