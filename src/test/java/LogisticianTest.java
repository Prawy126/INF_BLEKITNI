import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.sys.Logistician;
import org.example.wyjatki.AgeException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LogisticianTest {

    @Test
    void testConstructorInitialization() throws Exception {
        Address address = new Address();
        address.setMiasto("Wrocław");

        Logistician logistician = new Logistician(
                "John", "Doe", 30, "jdoe", address, "pass12345", "Logistician", new BigDecimal("4000")
        );

        assertEquals("John", logistician.getName());
        assertEquals("Doe", logistician.getSurname());
        assertEquals(30, logistician.getAge());
        assertEquals("jdoe", logistician.getLogin());
        assertEquals("pass12345", logistician.getPassword());
        assertEquals("Logistician", logistician.getStanowisko());
        assertEquals(new BigDecimal("4000"), logistician.getZarobki());
        assertEquals("Wrocław", logistician.getAdres().getMiasto());
        assertTrue(logistician.isLogistician());
    }

    @Test
    void testIsLogistician() {
        Logistician logistician = exampleLogistician();
        assertTrue(logistician.isLogistician());
    }

    @Test
    void testUpdateName() {
        Logistician logistician = exampleLogistician();
        Employee employee = exampleEmployee();

        logistician.updateName(employee, "Alice");
        assertEquals("Alice", employee.getName());
    }

    @Test
    void testUpdateSurname() {
        Logistician logistician = exampleLogistician();
        Employee employee = exampleEmployee();

        logistician.updateSurname(employee, "Nowak");
        assertEquals("Nowak", employee.getSurname());
    }

    @Test
    void testUpdateAge() {
        Logistician logistician = exampleLogistician();
        Employee employee = exampleEmployee();

        logistician.updateAge(employee, 45);
        assertEquals(45, employee.getAge());
    }

    @Test
    void testUpdateAddress() {
        Logistician logistician = exampleLogistician();
        Employee employee = exampleEmployee();

        Address newAddress = new Address();
        newAddress.setMiasto("Gdańsk");

        logistician.updateAddress(employee, newAddress);
        assertEquals("Gdańsk", employee.getAdres().getMiasto());
    }

    @Test
    void testUpdatePassword() throws Exception {
        Logistician logistician = exampleLogistician();
        Employee employee = exampleEmployee();

        logistician.updatePassword(employee, "newStrongPass");
        assertEquals("newStrongPass", employee.getPassword());
    }

    @Test
    void testUpdateDepartment() {
        Logistician logistician = exampleLogistician();
        Employee employee = exampleEmployee();

        logistician.updateDepartment(employee, "Transport");
        assertEquals("Transport", employee.getStanowisko());
    }

    @Test
    void testUpdateSalary() throws Exception {
        Logistician logistician = exampleLogistician();
        Employee employee = exampleEmployee();

        logistician.updateSalary(employee, new BigDecimal("6800.00"));
        assertEquals(new BigDecimal("6800.00"), employee.getZarobki());
    }

    // ==== POMOCNICZE ==== //

    private Logistician exampleLogistician() {
        try {
            Address addr = new Address();
            addr.setMiasto("Lublin");
            return new Logistician("Jan", "Kowalski", 35, "jkowal", addr, "secure1234", "Logistician", new BigDecimal("5000"));
        } catch (Exception e) {
            throw new RuntimeException("Błąd tworzenia logistyka", e);
        }
    }

    private Employee exampleEmployee() {
        try {
            Address addr = new Address();
            addr.setMiasto("Poznań");
            return new Employee("Anna", "Nowak", 28, "anna@example.com",
                    "anowak", "password123", addr, "Sprzedawca", new BigDecimal("4500"));
        } catch (Exception e) {
            throw new RuntimeException("Błąd tworzenia pracownika", e);
        }
    }
}
