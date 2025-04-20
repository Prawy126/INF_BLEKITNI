import org.example.sys.Employee;
import org.example.sys.Menager;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MenagerTest {

    @Test
    void testConstructorInitialization() {
        Menager menager = new Menager();
        assertNotNull(menager.getEmployees());
        assertTrue(menager.getEmployees().isEmpty());
    }

    @Test
    void testAddEmployee() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        assertEquals(1, menager.getEmployees().size());
        assertTrue(menager.getEmployees().contains(employee));
    }

    @Test
    void testAddNullEmployee() {
        Menager menager = new Menager();
        menager.addEmployee(null);
        assertTrue(menager.getEmployees().isEmpty());
    }

    @Test
    void testRemoveEmployee() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.removeEmployee(employee);
        assertTrue(menager.getEmployees().isEmpty());
    }

    @Test
    void testRemoveNonExistentEmployee() {
        Menager menager = new Menager();
        Employee employee1 = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        Employee employee2 = new Employee("Jane", "Doe", 25, "456 Street", "pass456", "jane@example.com", "E456", "IT", "Developer", 6000);
        menager.addEmployee(employee1);
        menager.removeEmployee(employee2);
        assertEquals(1, menager.getEmployees().size());
        assertTrue(menager.getEmployees().contains(employee1));
    }

    @Test
    void testSetEmployees() {
        Menager menager = new Menager();
        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        employees.add(employee);
        menager.setEmployees(employees);
        assertEquals(1, menager.getEmployees().size());
        assertTrue(menager.getEmployees().contains(employee));
    }

    @Test
    void testSetNullEmployees() {
        Menager menager = new Menager();
        menager.setEmployees(null);
        assertNotNull(menager.getEmployees());
        assertTrue(menager.getEmployees().isEmpty());
    }

    @Test
    void testUpdateName() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.updateName(employee, "James");
        assertEquals("James", employee.getName());
    }

    @Test
    void testUpdateSurname() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.updateSurname(employee, "Smith");
        assertEquals("Smith", employee.getSurname());
    }

    @Test
    void testUpdateAge() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.updateAge(employee, 35);
        assertEquals(35, employee.getAge());
    }

    @Test
    void testUpdateAddress() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.updateAddress(employee, "789 Avenue");
        assertEquals("789 Avenue", employee.getAddress());
    }

    @Test
    void testUpdatePassword() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.updatePassword(employee, "newPass");
        assertEquals("newPass", employee.getPassword());
    }

    @Test
    void testUpdateEmail() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.updateEmail(employee, "new.email@example.com");
        assertEquals("new.email@example.com", employee.getEmail());
    }

    @Test
    void testUpdateEmployeeId() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.updateEmployeeId(employee, "E999");
        assertEquals("E999", employee.getEmployeeId());
    }

    @Test
    void testUpdateDepartment() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.updateDepartment(employee, "Finance");
        assertEquals("Finance", employee.getDepartment());
    }

    @Test
    void testUpdatePosition() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.updatePosition(employee, "Senior Manager");
        assertEquals("Senior Manager", employee.getPosition());
    }

    @Test
    void testUpdateSalary() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        menager.updateSalary(employee, 7000);
        assertEquals(7000, employee.getSalary(), 0.01);
    }

    @Test
    void testGetPosition() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        String position = menager.getPosition(employee);
        assertEquals("Manager", position);
    }

    @Test
    void testGetEmployee() {
        Menager menager = new Menager();
        Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        menager.addEmployee(employee);
        Employee result = menager.getEmployee(employee);
        assertNotNull(result);
        assertEquals(employee, result);
    }

    @Test
    void testGetNonExistentEmployee() {
        Menager menager = new Menager();
        Employee employee1 = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "HR", "Manager", 5000);
        Employee employee2 = new Employee("Jane", "Doe", 25, "456 Street", "pass456", "jane@example.com", "E456", "IT", "Developer", 6000);
        menager.addEmployee(employee1);
        Employee result = menager.getEmployee(employee2);
        assertNull(result);
    }
}