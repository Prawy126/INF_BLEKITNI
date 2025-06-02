/*
 * Classname: OrderRepositoryTest
 * Version information: 1.5
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.database.OrderRepository;
import org.example.database.ProductRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.sys.Product;
import org.example.sys.Order;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderRepositoryTest {

    private static OrderRepository   orderRepo;
    private static ProductRepository productRepo;
    private static UserRepository    userRepo;

    private static Product  product;
    private static Employee employee;
    private static Order    order;

    private static LocalDate localOrderDate;

    /* === konfiguracja wspólna dla wszystkich testów === */
    @BeforeAll
    static void setUp() {
        orderRepo   = new OrderRepository();
        productRepo = new ProductRepository();
        userRepo    = new UserRepository();

        // 1) produkt testowy
        product = new Product("Testowy produkt", "Testowa kategoria", 5.99);
        productRepo.addProduct(product);

        // 2) pierwszy dostępny pracownik
        List<Employee> emps = userRepo.getAllEmployees();
        assertFalse(emps.isEmpty(), "Brak pracowników w bazie!");
        employee = emps.getFirst();          // Java 21+

        // 3) data zamówienia
        localOrderDate = LocalDate.of(2025, 5, 12);
    }

    /** Dodawanie zamówienia. */
    @Test
    @org.junit.jupiter.api.Order(1)
    void testAddOrder() {
        order = new Order();
        order.setProduct(product);
        order.setEmployee(employee);
        order.setQuantity(10);
        order.setPrice(new BigDecimal("59.90"));
        order.setDate(Date.valueOf(localOrderDate));

        assertDoesNotThrow(() -> orderRepo.addOrder(order));

        Order added = orderRepo.findOrderById(order.getId());
        assertNotNull(added, "Zamówienie powinno być zapisane");
        assertTrue(added.getId() > 0, "ID powinno być > 0");
    }

    /** Pobranie wszystkich + aktualizacja. */
    @Test
    @org.junit.jupiter.api.Order(2)
    void testFindAllAndUpdate() {
        assertTrue(
                orderRepo.getAllOrders().stream()
                        .anyMatch(o -> o.getId() == order.getId()),
                "Zamówienie powinno być na liście"
        );

        order.setQuantity(20);
        order.setPrice(new BigDecimal("119.80"));
        assertDoesNotThrow(() -> orderRepo.updateOrder(order));

        Order reloaded = orderRepo.findOrderById(order.getId());
        assertEquals(20, reloaded.getQuantity());
        assertEquals(new BigDecimal("119.80"), reloaded.getPrice());
    }

    /** Złożone zapytania wyszukujące. */
    @Test
    @org.junit.jupiter.api.Order(3)
    void testQueries() {
        // wg produktu
        assertTrue(orderRepo.findOrdersByProductId(product.getId())
                .stream()
                .allMatch(o -> o.getProduct().getId() == product.getId()));

        // wg pracownika
        assertTrue(orderRepo.findOrdersByEmployeeId(employee.getId())
                .stream()
                .allMatch(o -> o.getEmployee().getId() == employee.getId()));

        // wg dokładnej daty
        assertTrue(orderRepo.findOrdersByDate(localOrderDate)
                .stream()
                .allMatch(o ->
                        Instant.ofEpochMilli(o.getDate().getTime())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .equals(localOrderDate)));

        // przedział dat
        LocalDate from = localOrderDate.minusDays(1);
        LocalDate to   = localOrderDate.plusDays(1);
        assertTrue(orderRepo.findDateRangeOrders(from, to).stream()
                .anyMatch(o -> o.getId() == order.getId()));

        // minimalna ilość
        assertTrue(orderRepo.findOrdersWithMinimalQuantity(20).stream()
                .allMatch(o -> o.getQuantity() >= 20));

        // przedział cen
        BigDecimal min = new BigDecimal("100.00");
        BigDecimal max = new BigDecimal("200.00");
        assertTrue(orderRepo.findPriceRangeOrders(min, max).stream()
                .allMatch(o -> o.getPrice().compareTo(min) >= 0 &&
                        o.getPrice().compareTo(max) <= 0));
    }

    /** Usunięcie zamówienia. */
    @Test
    @org.junit.jupiter.api.Order(4)
    void testDelete() {
        assertDoesNotThrow(() -> orderRepo.removeOrder(order.getId()));

        assertNull(orderRepo.findOrderById(order.getId()));
        assertTrue(orderRepo.getAllOrders().stream()
                .noneMatch(o -> o.getId() == order.getId()));
    }

    /* === sprzątanie === */
    @AfterAll
    static void tearDown() {
        orderRepo.close();
        productRepo.close();
        userRepo.close();
    }
}
