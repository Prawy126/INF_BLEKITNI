import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.sys.Menager;
import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenagerTest {

    private Employee exampleEmployee() throws Exception {
        Address address = new Address();
        address.setMiasto("Warszawa");
        return new Employee("John", "Doe", 30, "john@example.com",
                "jdoe", "pass12345", address, "Manager", new BigDecimal("5000"));
    }

    private Menager exampleManager() throws Exception {
        Address address = new Address();
        address.setMiasto("Kraków");
        return new Menager("Anna", "Nowak", 40, address, "anowak", "strongpass", "Manager", new BigDecimal("8000"));
    }

    @Test
    void testConstructorInitialization() {
        Menager menager = new Menager();
        assertNotNull(menager.getEmployees());
        assertTrue(menager.getEmployees().isEmpty());
    }

    @Test
    void testAddEmployee() throws Exception {
        Menager menager = exampleManager();
        Employee employee = exampleEmployee();
        menager.addEmployee(employee);
        assertEquals(1, menager.getEmployees().size());
        assertTrue(menager.getEmployees().contains(employee));
    }

    @Test
    void testAddNullEmployee() throws Exception {
        Menager menager = exampleManager();
        menager.addEmployee(null);
        assertTrue(menager.getEmployees().isEmpty());
    }

    @Test
    void testRemoveEmployee() throws Exception {
        Menager menager = exampleManager();
        Employee employee = exampleEmployee();
        menager.addEmployee(employee);
        menager.removeEmployee(employee);
        assertTrue(menager.getEmployees().isEmpty());
    }

    @Test
    void testRemoveNonExistentEmployee() throws Exception {
        Menager menager = exampleManager();
        Employee emp1 = exampleEmployee();
        Address address = new Address();
        address.setMiasto("Gdynia");
        Employee emp2 = new Employee("Jane", "Doe", 25, "jane@example.com",
                "jdoe2", "pass67890", address, "Developer", new BigDecimal("6000"));

        menager.addEmployee(emp1);
        menager.removeEmployee(emp2);
        assertEquals(1, menager.getEmployees().size());
        assertTrue(menager.getEmployees().contains(emp1));
    }

    @Test
    void testSetEmployees() throws Exception {
        Menager menager = exampleManager();
        Employee employee = exampleEmployee();
        List<Employee> list = new ArrayList<>();
        list.add(employee);
        menager.setEmployees(list);
        assertEquals(1, menager.getEmployees().size());
    }

    @Test
    void testSetNullEmployees() throws Exception {
        Menager menager = exampleManager();
        menager.setEmployees(null);
        assertNotNull(menager.getEmployees());
        assertTrue(menager.getEmployees().isEmpty());
    }

    @Test
    void testUpdateName() throws Exception {
        Menager menager = exampleManager();
        Employee emp = exampleEmployee();
        menager.updateName(emp, "Michael");
        assertEquals("Michael", emp.getName());
    }

    @Test
    void testUpdateSurname() throws Exception {
        Menager menager = exampleManager();
        Employee emp = exampleEmployee();
        menager.updateSurname(emp, "Kowalski");
        assertEquals("Kowalski", emp.getSurname());
    }

    @Test
    void testUpdateAge() throws Exception {
        Menager menager = exampleManager();
        Employee emp = exampleEmployee();
        menager.updateAge(emp, 45);
        assertEquals(45, emp.getAge());
    }

    @Test
    void testUpdateAddress() throws Exception {
        Menager menager = exampleManager();
        Employee emp = exampleEmployee();
        Address newAddress = new Address();
        newAddress.setMiasto("Wrocław");
        menager.updateAddress(emp, newAddress);
        assertEquals("Wrocław", emp.getAdres().getMiasto());
    }

    @Test
    void testUpdatePassword() throws Exception {
        Menager menager = exampleManager();
        Employee emp = exampleEmployee();
        menager.updatePassword(emp, "newStrongPass");
        assertEquals("newStrongPass", emp.getPassword());
    }

    @Test
    void testUpdateEmail() throws Exception {
        Employee emp = exampleEmployee();
        emp.setEmail("updated@example.com");
        assertEquals("updated@example.com", emp.getEmail());
    }

    @Test
    void testUpdateDepartment() throws Exception {
        Menager menager = exampleManager();
        Employee emp = exampleEmployee();
        menager.updateDepartment(emp, "Finance");
        assertEquals("Finance", emp.getStanowisko());
    }

    @Test
    void testUpdateSalary() throws Exception {
        Menager menager = exampleManager();
        Employee emp = exampleEmployee();
        menager.updateSalary(emp, new BigDecimal("9000.00"));
        assertEquals(new BigDecimal("9000.00"), emp.getZarobki());
    }

    @Test
    void testGetEmployee() throws Exception {
        Menager menager = exampleManager();
        Employee emp = exampleEmployee();
        menager.addEmployee(emp);
        assertEquals(emp, menager.getEmployee(emp));
    }

    @Test
    void testGetNonExistentEmployee() throws Exception {
        Menager menager = exampleManager();
        Employee emp1 = exampleEmployee();
        Address addr = new Address();
        addr.setMiasto("Sopot");
        Employee emp2 = new Employee("Anna", "Zielinska", 26, "anna@example.com",
                "aziel", "pass8765", addr, "Analyst", new BigDecimal("5000"));

        menager.addEmployee(emp1);
        assertNull(menager.getEmployee(emp2));
    }
}
