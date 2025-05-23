/*
 * Classname: UserRepositoryTest
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


import org.example.database.AddressRepository;
import org.example.database.UserRepository;
import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.wyjatki.SalaryException;
import org.example.wyjatki.PasswordException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {

    private static AddressRepository addressRepo;
    private static UserRepository userRepo;

    private static Address  testAddress;
    private static Employee testEmployee;

    @BeforeAll
    static void setup() {
        addressRepo = new AddressRepository();
        userRepo    = new UserRepository();
    }

    @Test
    @Order(1)
    void testAddAddressAndEmployee() throws SalaryException, PasswordException {
        // 1. Dodajemy adres
        testAddress = new Address();
        testAddress.setTown("Testowo");
        testAddress.setCity("Miastko");
        testAddress.setZipCode("00-000");
        testAddress.setHouseNumber("10A");
        testAddress.setApartmentNumber("5");
        assertDoesNotThrow(() -> addressRepo.addAddress(testAddress),
                "Should persist address without exception");
        assertTrue(testAddress.getId() > 0, "Address should get an ID");

        // 2. Dodajemy pracownika powiązanego z tym adresem
        testEmployee = new Employee(
                "Michał", "Brzozowski", 26, testAddress,
                "mbrzo", "tajnehaslo", "Kasjer",
                new BigDecimal("3200.00")
        );
        assertDoesNotThrow(() -> userRepo.addEmployee(testEmployee),
                "Should persist employee without exception");
        assertTrue(testEmployee.getId() > 0, "Employee should get an ID");
    }

    @Test
    @Order(2)
    void testFindersByRoleAndLogin() {
        // 3. pobieramy kasjerów
        List<Employee> kasjerzy = userRepo.getCashiers();
        assertTrue(kasjerzy.stream().anyMatch(e -> e.getId() == testEmployee.getId()),
                "New cashier should appear in kasjerzy");

        // 4. wyszukaj tylko po loginie
        Employee byLogin = userRepo.findByLogin("mbrzo");
        assertNotNull(byLogin, "Should find employee by login");
        assertEquals(testEmployee.getId(), byLogin.getId());

        // 5. wyszukaj po loginie i haśle
        Employee byLoginPass = userRepo.findByLoginAndPassword("mbrzo", "tajnehaslo");
        assertNotNull(byLoginPass, "Login + password should authenticate");
        assertEquals(testEmployee.getId(), byLoginPass.getId());
    }

    @Test
    @Order(3)
    void testCurrentEmployeeAndLookups() {
        // 6. getCurrentEmployee
        Employee current = userRepo.getCurrentEmployee();
        assertNotNull(current, "Current logged-in employee must be set");
        assertEquals(testEmployee.getId(), current.getId());

        // 7. znajdź po ID
        Employee byId = userRepo.findById(testEmployee.getId());
        assertNotNull(byId);

        // 8a. wyszukaj po fragmencie imienia
        List<Employee> byName = userRepo.findByName("chał");
        assertTrue(byName.stream().anyMatch(e -> e.getId() == testEmployee.getId()));

        // 8b. wyszukaj po fragmencie nazwiska
        List<Employee> bySurname = userRepo.findBySurname("Brzo");
        assertTrue(bySurname.stream().anyMatch(e -> e.getId() == testEmployee.getId()));

        // 8c. wyszukaj po wieku
        List<Employee> byAge = userRepo.findByAge(20, 30);
        assertTrue(byAge.stream().anyMatch(e -> e.getId() == testEmployee.getId()));
    }

    @Test
    @Order(4)
    void testAddressEmailSalaryPositionAndSickLeave() {
        // 9a. wyszukaj po ID adresu
        List<Employee> byAddress = userRepo.findByAddress(testAddress.getId());
        assertTrue(byAddress.stream().anyMatch(e -> e.getId() == testEmployee.getId()));

        // 9b. zaktualizuj e-mail i wyszukaj po fragmencie
        testEmployee.setEmail("mbrzo@example.com");
        assertDoesNotThrow(() -> userRepo.updateEmployee(testEmployee));
        List<Employee> byEmail = userRepo.findByEmail("mbrzo@");
        assertTrue(byEmail.stream().anyMatch(e -> e.getId() == testEmployee.getId()));

        // 9c. wyszukaj po zarobkach
        List<Employee> bySalary = userRepo.findBySalary(3000, 3500);
        assertTrue(bySalary.stream().anyMatch(e -> e.getId() == testEmployee.getId()));

        // 9d. wyszukaj po stanowisku
        List<Employee> byPosition = userRepo.findByPosition("Kasjer");
        assertTrue(byPosition.stream().anyMatch(e -> e.getId() == testEmployee.getId()));

        // 10. filtry zwolnień lekarskich
        testEmployee.startSickLeave(Date.valueOf(LocalDate.now().minusDays(1)));
        assertDoesNotThrow(() -> userRepo.updateEmployee(testEmployee));
        List<Employee> onLeave    = userRepo.getOnSickLeave();
        List<Employee> notOnLeave = userRepo.getNotOnSickLeave();
        assertTrue(onLeave.stream().anyMatch(e -> e.getId() == testEmployee.getId()));
        assertTrue(notOnLeave.stream().noneMatch(e -> e.getId() == testEmployee.getId()));
    }

    @Test
    @Order(5)
    void testSoftDeleteAndCleanup() {
        // 11. miękkie usunięcie pracownika
        assertDoesNotThrow(() -> userRepo.removeEmployee(testEmployee));
        Employee deleted = userRepo.findById(testEmployee.getId());
        assertNull(deleted, "Soft-deleted employee should not be returned");

        // upewnij się, że adres nadal istnieje (brak kaskady)
        Address fetched = addressRepo.findAddressById(testAddress.getId());
        assertNotNull(fetched, "Address should remain after employee deletion");
    }

    @AfterAll
    static void tearDown() {
        userRepo.close();
        addressRepo.close();
    }
}