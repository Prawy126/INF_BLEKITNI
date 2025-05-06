import org.example.sys.Admin;
import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.AgeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void testFullConstructor() throws Exception {
        Address address = new Address();
        address.setMiasto("Warszawa");

        Admin admin = new Admin(
                "John", "Doe", 30, "john@example.com",
                "adminLogin", "adminPass123",
                address, "Admin", new BigDecimal("6000.00")
        );

        assertEquals("John", admin.getName());
        assertEquals("Doe", admin.getSurname());
        assertEquals(30, admin.getAge());
        assertEquals("john@example.com", admin.getEmail());
        assertEquals("adminLogin", admin.getLogin());
        assertEquals("adminPass123", admin.getPassword());
        assertEquals("Admin", admin.getStanowisko());
        assertEquals(new BigDecimal("6000.00"), admin.getZarobki());
        assertEquals("Warszawa", admin.getAdres().getMiasto());
    }

    @Test
    void testDefaultConstructor() {
        Admin admin = new Admin();
        assertNull(admin.getName());
        assertNull(admin.getSurname());
        assertEquals(0, admin.getAge());
        assertNull(admin.getEmail());
        assertNull(admin.getLogin());
        assertNull(admin.getPassword());
        assertNull(admin.getStanowisko());
        assertNull(admin.getZarobki());
        assertNull(admin.getAdres());
    }

    @Test
    void testIsAdmin() {
        Admin admin = new Admin();
        assertTrue(admin.isAdmin());
    }

    @Test
    void testUpdateName() throws NameException {
        Admin admin = new Admin();
        Employee emp = exampleEmployee();
        admin.updateName(emp, "Ewa");
        assertEquals("Ewa", emp.getName());
    }

    @Test
    void testUpdateSurname() throws NameException {
        Admin admin = new Admin();
        Employee emp = exampleEmployee();
        admin.updateSurname(emp, "Nowak");
        assertEquals("Nowak", emp.getSurname());
    }

    @Test
    void testUpdateAge() throws AgeException {
        Admin admin = new Admin();
        Employee emp = exampleEmployee();
        admin.updateAge(emp, 45);
        assertEquals(45, emp.getAge());
    }

    @Test
    void testUpdateAddress() {
        Admin admin = new Admin();
        Employee emp = exampleEmployee();

        Address newAddr = new Address();
        newAddr.setMiasto("Kraków");

        admin.updateAddress(emp, newAddr);
        assertEquals("Kraków", emp.getAdres().getMiasto());
    }

    @Test
    void testUpdatePassword() throws PasswordException {
        Admin admin = new Admin();
        Employee emp = exampleEmployee();

        admin.updatePassword(emp, "newSecurePass");
        assertEquals("newSecurePass", emp.getPassword());
    }

    @Test
    void testUpdateDepartment() {
        Admin admin = new Admin();
        Employee emp = exampleEmployee();

        admin.updateDepartment(emp, "HR");
        assertEquals("HR", emp.getStanowisko());
    }

    @Test
    void testUpdateSalary() throws SalaryException {
        Admin admin = new Admin();
        Employee emp = exampleEmployee();

        admin.updateSalary(emp, new BigDecimal("8800.50"));
        assertEquals(new BigDecimal("8800.50"), emp.getZarobki());
    }

    /**
     * Pomocnicza metoda do tworzenia przykładowego pracownika
     */
    private Employee exampleEmployee() {
        try {
            Address addr = new Address();
            addr.setMiasto("Poznań");

            return new Employee(
                    "Anna", "Kowalska", 28, "anna@wp.pl",
                    "akowal", "pass12345", addr,
                    "Sprzedawca", new BigDecimal("4200")
            );
        } catch (Exception e) {
            throw new RuntimeException("Błąd przy tworzeniu przykładowego pracownika", e);
        }
    }
}
