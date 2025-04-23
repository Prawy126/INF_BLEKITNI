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
        try{
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com",  "HR", "Manager", 5000);
            menager.addEmployee(employee);
            assertEquals(1, menager.getEmployees().size());
            assertTrue(menager.getEmployees().contains(employee));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testAddNullEmployee() {
        Menager menager = new Menager();
        menager.addEmployee(null);
        assertTrue(menager.getEmployees().isEmpty());
    }

    @Test
    void testRemoveEmployee() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            menager.addEmployee(employee);
            menager.removeEmployee(employee);
            assertTrue(menager.getEmployees().isEmpty());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testRemoveNonExistentEmployee() {
        try{
            Menager menager = new Menager();
            Employee employee1 = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            Employee employee2 = new Employee("Jane", "Doe", 25, "456 Street", "pass456", "jane@example.com","IT", "Developer", 6000);
            menager.addEmployee(employee1);
            menager.removeEmployee(employee2);
            assertEquals(1, menager.getEmployees().size());
            assertTrue(menager.getEmployees().contains(employee1));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testSetEmployees() {
        try{
            Menager menager = new Menager();
            List<Employee> employees = new ArrayList<>();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            employees.add(employee);
            menager.setEmployees(employees);
            assertEquals(1, menager.getEmployees().size());
            assertTrue(menager.getEmployees().contains(employee));
        }catch (Exception e){
            e.printStackTrace();
        }

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
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            menager.addEmployee(employee);
            menager.updateName(employee, "James");
            assertEquals("James", employee.getName());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateSurname() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            menager.addEmployee(employee);
            menager.updateSurname(employee, "Smith");
            assertEquals("Smith", employee.getSurname());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateAge() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            menager.addEmployee(employee);
            menager.updateAge(employee, 35);
            assertEquals(35, employee.getAge());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateAddress() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            menager.addEmployee(employee);
            menager.updateAddress(employee, "789 Avenue");
            assertEquals("789 Avenue", employee.getAddress());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testUpdatePassword() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            menager.addEmployee(employee);
            menager.updatePassword(employee, "newPass");
            assertEquals("newPass", employee.getPassword());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateEmail() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            menager.addEmployee(employee);
            menager.updateEmail(employee, "new.email@example.com");
            assertEquals("new.email@example.com", employee.getEmail());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testUpdateDepartment() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            menager.addEmployee(employee);
            menager.updateDepartment(employee, "Finance");
            assertEquals("Finance", employee.getDepartment());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testUpdatePosition() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "Manager", 5000);
            menager.addEmployee(employee);
            menager.updatePosition(employee, "Senior Manager");
            assertEquals("Senior Manager", employee.getPosition());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testUpdateSalary() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123", "Manager", 5000);
            menager.addEmployee(employee);
            menager.updateSalary(employee, 7000);
            assertEquals(7000, employee.getSalary(), 0.01);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testGetPosition() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            menager.addEmployee(employee);
            String position = menager.getPosition(employee);
            assertEquals("Manager", position);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testGetEmployee() {
        try{
            Menager menager = new Menager();
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            menager.addEmployee(employee);
            Employee result = menager.getEmployee(employee);
            assertNotNull(result);
            assertEquals(employee, result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testGetNonExistentEmployee() {
        try{
            Menager menager = new Menager();
            Employee employee1 = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            Employee employee2 = new Employee("Jane", "Doe", 25, "456 Street", "pass456", "jane@example.com", "IT", "Developer", 6000);
            menager.addEmployee(employee1);
            Employee result = menager.getEmployee(employee2);
            assertNull(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}