import org.example.sys.Employee;
import org.example.sys.Logistician;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogisticianTest {

    @Test
    void testConstructorInitialization() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "Logistics", "Logistician", 4000);
            assertEquals("John", logistician.getName());
            assertEquals("Doe", logistician.getSurname());
            assertEquals(30, logistician.getAge());
            assertEquals("123 Street", logistician.getAddress());
            assertEquals("pass123", logistician.getPassword());
            assertEquals("john@example.com", logistician.getEmail());
            assertEquals("E123", logistician.getEmployeeId());
            assertEquals("Logistics", logistician.getDepartment());
            assertEquals("Logistician", logistician.getPosition());
            assertEquals(4000, logistician.getSalary(), 0.01);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testIsLogistician() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com",  "Logistics", "Logistician", 4000);
            assertTrue(logistician.isLogistician());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateName() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com",  "Logistics", "Logistician", 4000);
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com",  "HR", "Manager", 5000);
            logistician.updateName(employee, "Alice");
            assertEquals("Alice", employee.getName());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateSurname() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "Logistics", "Logistician", 4000);
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            logistician.updateSurname(employee, "Brown");
            assertEquals("Brown", employee.getSurname());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateAge() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123",  "Logistician", 4000);
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            logistician.updateAge(employee, 35);
            assertEquals(35, employee.getAge());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateAddress() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "E123",  "Logistician", 4000);
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            logistician.updateAddress(employee, "789 Avenue");
            assertEquals("789 Avenue", employee.getAddress());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdatePassword() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "Logistics", "Logistician", 4000);
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            logistician.updatePassword(employee, "newPass");
            assertEquals("newPass", employee.getPassword());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateEmail() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "Logistics", "Logistician", 4000);
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            logistician.updateEmail(employee, "new.email@example.com");
            assertEquals("new.email@example.com", employee.getEmail());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateDepartment() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com",  "Logistics", "Logistician", 4000);
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            logistician.updateDepartment(employee, "Finance");
            assertEquals("Finance", employee.getDepartment());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdatePosition() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com",  "Logistics", "Logistician", 4000);
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            logistician.updatePosition(employee, "Senior Manager");
            assertEquals("Senior Manager", employee.getPosition());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testUpdateSalary() {
        try{
            Logistician logistician = new Logistician("John", "Doe", 30, "123 Street", "pass123", "john@example.com",  "Logistics", "Logistician", 4000);
            Employee employee = new Employee("Jane", "Smith", 25, "456 Street", "pass456", "jane@example.com", "HR", "Manager", 5000);
            logistician.updateSalary(employee, 7000);
            assertEquals(7000, employee.getSalary(), 0.01);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}