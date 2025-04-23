import org.example.sys.Employee;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void testConstructorInitialization() {
       try{
           Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
           assertEquals("John", employee.getName());
           assertEquals("Doe", employee.getSurname());
           assertEquals(30, employee.getAge());
           assertEquals("123 Street", employee.getAddress());
           assertEquals("pass123", employee.getPassword());
           assertEquals("john@example.com", employee.getEmail());
           assertEquals("E123", employee.getEmployeeId());
           assertEquals("HR", employee.getDepartment());
           assertEquals("Manager", employee.getPosition());
           assertEquals(5000, employee.getSalary(), 0.01);
           assertFalse(employee.isOnSickLeave());
           assertNull(employee.getSickLeaveStartDate());
       }catch (Exception e){
           e.printStackTrace();
       }

    }

    @Test
    void testSettersAndGetters() {
        try{
            Employee employee = new Employee();
            employee.setName("Jane");
            employee.setSurname("Smith");
            employee.setAge(25);
            employee.setAddress("456 Street");
            employee.setPassword("newPass");
            employee.setEmail("jane@example.com");
            //employee.setEmployeeId("E456");
            employee.setDepartment("IT");
            employee.setPosition("Developer");
            employee.setSalary(6000);

            assertEquals("Jane", employee.getName());
            assertEquals("Smith", employee.getSurname());
            assertEquals(25, employee.getAge());
            assertEquals("456 Street", employee.getAddress());
            assertEquals("newPass", employee.getPassword());
            assertEquals("jane@example.com", employee.getEmail());
            assertEquals("E456", employee.getEmployeeId());
            assertEquals("IT", employee.getDepartment());
            assertEquals("Developer", employee.getPosition());
            assertEquals(6000, employee.getSalary(), 0.01);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testStartSickLeave() {
        try{
            Employee employee = new Employee("John", "Doe", 30, "123 Street", "pass123", "john@example.com", "HR", "Manager", 5000);
            Date startDate = new Date();
            employee.startSickLeave(startDate);

            assertTrue(employee.isOnSickLeave());
            assertEquals(startDate, employee.getSickLeaveStartDate());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    void testDefaultValues() {
        Employee employee = new Employee();
        assertNull(employee.getName());
        assertNull(employee.getSurname());
        assertEquals(0, employee.getAge());
        assertNull(employee.getAddress());
        assertNull(employee.getPassword());
        assertNull(employee.getEmail());
        assertNull(employee.getEmployeeId());
        assertNull(employee.getDepartment());
        assertNull(employee.getPosition());
        assertEquals(0.0, employee.getSalary(), 0.01);
        assertFalse(employee.isOnSickLeave());
        assertNull(employee.getSickLeaveStartDate());
    }
}