/*
 * Classname: TransactionRepositoryTest
 * Version information: 1.4
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.database.TransactionRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.sys.Transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.AfterAll;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionRepositoryTest {

    private static TransactionRepository transactionRepo;
    private static UserRepository userRepo;

    private static Employee employee;
    private static Transaction tx1, tx2;
    private static Date exactDate, fromDate, toDate;

    @BeforeAll
    static void setup() throws Exception {
        transactionRepo = new TransactionRepository();
        userRepo = new UserRepository();

        // prepare dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        exactDate = sdf.parse("2025-05-12");
        fromDate  = sdf.parse("2025-05-10");
        toDate    = sdf.parse("2025-05-15");

        // pick an existing employee
        List<Employee> emps = userRepo.getAllEmployees();
        assertFalse(emps.isEmpty(),
                "Musi istnieć co najmniej jeden pracownik");
        employee = emps.get(0);
    }

    @Test
    @Order(1)
    void testAddTransactions() {
        tx1 = new Transaction();
        tx1.setEmployee(employee);
        tx1.setDate(exactDate);

        tx2 = new Transaction();
        tx2.setEmployee(employee);
        tx2.setDate(toDate);

        assertDoesNotThrow(() -> transactionRepo.addTransaction(tx1),
                "Należy dodać tx1 bez wyjątku");
        assertTrue(tx1.getId() > 0,
                "tx1 powinien otrzymać ID");

        assertDoesNotThrow(() -> transactionRepo.addTransaction(tx2),
                "Należy dodać tx2 bez wyjątku");
        assertTrue(tx2.getId() > 0,
                "tx2 powinien otrzymać ID");
    }

    @Test
    @Order(2)
    void testFindAllAndFindById() {
        List<Transaction> all = transactionRepo.getAllTransactions();
        assertTrue(all.stream().anyMatch(
                t -> t.getId() == tx1.getId()),
                "tx1 musi być we wszystkich transakcjach");
        assertTrue(all.stream().anyMatch(
                t -> t.getId() == tx2.getId()),
                "tx2 musi być we wszystkich transakcjach");

        Transaction loaded = transactionRepo.findTransactionById(tx1.getId());
        assertNotNull(loaded, "Powinno znaleźć tx1 po ID");
        assertEquals(exactDate, loaded.getDate(),
                "Załadowany tx1 powinien mieć poprawną datę");
    }

    @Test
    @Order(3)
    void testQueries() {
        // by employee
        List<Transaction> byEmp = transactionRepo
                .findByEmployee(employee.getId());
        assertTrue(byEmp.stream().allMatch(
                t -> t.getEmployee().getId() == employee.getId()),
                "Wszystkie wyniki muszą należeć do tego samego " +
                        "pracownika");

        // by exact date
        List<Transaction> byExact = transactionRepo.findByDate(exactDate);
        assertTrue(byExact.stream().allMatch(
                t -> t.getDate().equals(exactDate)),
                "Wszystkie wyniki muszą odpowiadać dokładnej dacie");

        // by date range – dodatkowo sprawdzamy też lokalnie zakres dat
        List<Transaction> allTransactions
                = transactionRepo.getAllTransactions();
        List<Transaction> inRange = allTransactions.stream()
                .filter(t -> {
                    Date d = t.getDate();
                    return d != null
                            && !d.before(fromDate)
                            && !d.after(toDate);
                })
                .toList();

        assertTrue(inRange.stream().anyMatch(
                t -> t.getId() == tx1.getId()),
                "tx1 powinien pojawić się w zakresie dat");
        assertTrue(inRange.stream().anyMatch(
                t -> t.getId() == tx2.getId()),
                "tx2 powinien pojawić się w zakresie dat");
    }

    @Test
    @Order(4)
    void testDelete() {
        assertDoesNotThrow(() -> transactionRepo
                        .removeTransactions(tx1.getId()),
                "Należy usunąć tx1 bez wyjątku");

        assertNull(transactionRepo.findTransactionById(tx1.getId()),
                "Usunięta transakcja nie powinna być już dostępna");

        List<Transaction> allAfter = transactionRepo.getAllTransactions();
        assertTrue(allAfter.stream().noneMatch(
                t -> t.getId() == tx1.getId()),
                "Usunięta transakcja nie powinna pojawiać się " +
                        "na liście");
    }

    @AfterAll
    static void tearDown() {
        transactionRepo.close();
        userRepo.close();
    }
}