/*
 * Classname: CashierTest
 * Version information: 1.3
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.Employee;
import org.example.sys.Cashier;
import org.example.sys.Address;

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class CashierTest {

    private Employee employee;
    private Cashier cashier;

    @BeforeEach
    void setUp() throws Exception {
        Address address = new Address();
        address.setCity("Łódź");
        employee = new Employee("Kamil", "Nowak",
                29, "kamil@example.com",
                "kamiln", "silneHaslo1", address,
                "Kasjer", new BigDecimal("4000"));
        cashier = new Cashier(employee);
    }

    @Test
    void testGetEmployee() {
        assertEquals(employee, cashier.getEmployee());
    }

    @Test
    void testUpdatePassword() throws PasswordException {
        cashier.updatePassword("NoweHaslo123");
        assertEquals("NoweHaslo123",
                cashier.getEmployee().getPassword());
    }

    @Test
    void testUpdatePasswordTooShort() {
        cashier.updatePassword("123"); // nie rzuca wyjątku, tylko loguje
        assertNotEquals("123", cashier.getEmployee().getPassword());
    }

    @Test
    void testUpdateSalary() throws SalaryException {
        BigDecimal newSalary = new BigDecimal("4500");
        cashier.updateSalary(newSalary);
        assertEquals(newSalary, cashier.getEmployee().getSalary());
    }

    @Test
    void testUpdateSalaryInvalid() {
        cashier.updateSalary(BigDecimal.ZERO); // nie rzuca wyjątku, tylko loguje
        assertNotEquals(BigDecimal.ZERO, cashier.getEmployee().getSalary());
    }

    @Test
    void testScanProduct() {
        assertDoesNotThrow(() -> cashier.scanProduct("Mleko 1L"));
    }

    @Test
    void testEndTransaction() {
        assertDoesNotThrow(cashier::endTransaction);
    }
}
