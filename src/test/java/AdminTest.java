import org.example.sys.Admin;
import org.example.sys.Employee;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void testFullConstructor() {
        try{
            Admin admin = new Admin("John", "Doe", 30, "123 Street", "adminLogin", "adminPass");
            assertEquals("John", admin.getName());
            assertEquals("Doe", admin.getSurname());
            assertEquals(30, admin.getAge());
            assertEquals("123 Street", admin.getAddress());
            assertEquals("adminPass", admin.getPassword());
            assertEquals("adminLogin", admin.getEmail());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testConstructorWithoutAddress() {
        try{
            Admin admin = new Admin("John", "Doe", 30, "adminLogin", "adminPass");
            assertEquals("John", admin.getName());
            assertEquals("Doe", admin.getSurname());
            assertEquals(30, admin.getAge());
            assertNull(admin.getAddress()); // Address should be null
            assertEquals("adminPass", admin.getPassword());
            assertEquals("adminLogin", admin.getEmail());
        }catch (Exception e){
            e.printStackTrace();
        }

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
        try{
            Admin admin = new Admin();
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            admin.updateName(employee, "Alice");
            assertEquals("Alice", employee.getName());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateSurname() {
        try{
            Admin admin = new Admin();
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            admin.updateSurname(employee, "Brown");
            assertEquals("Brown", employee.getSurname());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateAge() {
        try{
            Admin admin = new Admin();
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            admin.updateAge(employee, 35);
            assertEquals(35, employee.getAge());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateAddress() {
        try{
            Admin admin = new Admin();
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com",  "HR", "Manager", 5000);
            admin.updateAddress(employee, "789 Avenue");
            assertEquals("789 Avenue", employee.getAddress());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdatePassword() {
        try{
            Admin admin = new Admin();
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            admin.updatePassword(employee, "newPass");
            assertEquals("newPass", employee.getPassword());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateEmail() {
        try{
            Admin admin = new Admin();
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            admin.updateEmail(employee, "new.email@example.com");
            assertEquals("new.email@example.com", employee.getEmail());
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Test
    void testUpdateDepartment() {
        try{
            Admin admin = new Admin();
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            admin.updateDepartment(employee, "Finance");
            assertEquals("Finance", employee.getDepartment());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdatePosition() {
        try{
            Admin admin = new Admin();
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            admin.updatePosition(employee, "Senior Manager");
            assertEquals("Senior Manager", employee.getPosition());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateSalary() {
        try{
            Admin admin = new Admin();
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            admin.updateSalary(employee, 7000);
            assertEquals(7000, employee.getSalary(), 0.01);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}