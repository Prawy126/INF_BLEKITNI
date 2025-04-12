import org.example.sys.Admin;
import org.example.sys.Employee;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void testFullConstructor() {
        Admin admin = new Admin("John", "Doe", 30, "123 Street", "adminLogin", "adminPass");
        assertEquals("John", admin.getName());
        assertEquals("Doe", admin.getSurname());
        assertEquals(30, admin.getAge());
        assertEquals("123 Street", admin.getAddress());
        assertEquals("adminPass", admin.getPassword());
        assertEquals("adminLogin", admin.getEmail());
    }

    @Test
    void testConstructorWithoutAddress() {
        Admin admin = new Admin("John", "Doe", 30, "adminLogin", "adminPass");
        assertEquals("John", admin.getName());
        assertEquals("Doe", admin.getSurname());
        assertEquals(30, admin.getAge());
        assertNull(admin.getAddress()); // Address should be null
        assertEquals("adminPass", admin.getPassword());
        assertEquals("adminLogin", admin.getEmail());
    }

    @Test
    void testDefaultConstructor() {
        Admin admin = new Admin();
        assertNull(admin.getName());
        assertNull(admin.getSurname());
        assertEquals(0, admin.getAge());
        assertNull(admin.getAddress());
        assertNull(admin.getPassword());
        assertNull(admin.getEmail());
    }

    @Test
    void testIsAdmin() {
        Admin admin = new Admin();
        assertTrue(admin.isAdmin());
    }

    @Test
    void testUpdateName() {
        Admin admin = new Admin();
        Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "E456", "HR", "Manager", 5000);
        admin.updateName(employee, "Alice");
        assertEquals("Alice", employee.getName());
    }

    @Test
    void testUpdateSurname() {
        Admin admin = new Admin();
        Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "E456", "HR", "Manager", 5000);
        admin.updateSurname(employee, "Brown");
        assertEquals("Brown", employee.getSurname());
    }

    @Test
    void testUpdateAge() {
        Admin admin = new Admin();
        Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "E456", "HR", "Manager", 5000);
        admin.updateAge(employee, 35);
        assertEquals(35, employee.getAge());
    }

    @Test
    void testUpdateAddress() {
        Admin admin = new Admin();
        Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "E456", "HR", "Manager", 5000);
        admin.updateAddress(employee, "789 Avenue");
        assertEquals("789 Avenue", employee.getAddress());
    }

    @Test
    void testUpdatePassword() {
        Admin admin = new Admin();
        Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "E456", "HR", "Manager", 5000);
        admin.updatePassword(employee, "newPass");
        assertEquals("newPass", employee.getPassword());
    }

    @Test
    void testUpdateEmail() {
        Admin admin = new Admin();
        Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "E456", "HR", "Manager", 5000);
        admin.updateEmail(employee, "new.email@example.com");
        assertEquals("new.email@example.com", employee.getEmail());
    }

    @Test
    void testUpdateEmployeeId() {
        Admin admin = new Admin();
        Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "E456", "HR", "Manager", 5000);
        admin.updateEmployeeId(employee, "E999");
        assertEquals("E999", employee.getEmployeeId());
    }

    @Test
    void testUpdateDepartment() {
        Admin admin = new Admin();
        Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "E456", "HR", "Manager", 5000);
        admin.updateDepartment(employee, "Finance");
        assertEquals("Finance", employee.getDepartment());
    }

    @Test
    void testUpdatePosition() {
        Admin admin = new Admin();
        Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "E456", "HR", "Manager", 5000);
        admin.updatePosition(employee, "Senior Manager");
        assertEquals("Senior Manager", employee.getPosition());
    }

    @Test
    void testUpdateSalary() {
        Admin admin = new Admin();
        Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "E456", "HR", "Manager", 5000);
        admin.updateSalary(employee, 7000);
        assertEquals(7000, employee.getSalary(), 0.01);
    }
}