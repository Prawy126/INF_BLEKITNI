/*
 * Classname: OrderRepositoryTest
 * Version information: 1.2
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


import org.example.database.OrderRepository;
import org.example.database.ProductRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.sys.Product;
import org.example.sys.Order;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderRepositoryTest {

    private static OrderRepository orderRepo;
    private static ProductRepository productRepo;
    private static UserRepository userRepo;

    private static Product  product;
    private static Employee employee;
    private static Order    order;

    /** data w dwóch postaciach — przydaje się w różnych miejscach */
    private static LocalDate localOrderDate;
    private static Date      utilOrderDate;

    @BeforeAll
    static void setup() {
        orderRepo   = new OrderRepository();
        productRepo = new ProductRepository();
        userRepo    = new UserRepository();

        // 1) produkt testowy
        product = new Product("Testowy produkt", "Testowa kategoria", 5.99);
        productRepo.addProduct(product);

        // 2) dowolny istniejący pracownik
        List<Employee> emps = userRepo.getAllEmployess();
        assertFalse(emps.isEmpty(), "Brak pracowników w bazie!");
        employee = emps.get(0);

        // 3) data zamówienia
        localOrderDate = LocalDate.of(2025, 5, 12);
        utilOrderDate  = Date.from(localOrderDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Test @org.junit.jupiter.api.Order(1)
    void testAddOrder() {
        order = new Order();
        order.setProduct(product);
        order.setEmployee(employee);
        order.setQuantity(10);
        order.setPrice(new BigDecimal("59.90"));
        order.setDate(utilOrderDate);                // <-- java.util.Date

        assertDoesNotThrow(() -> orderRepo.addOrder(order));
        assertTrue(order.getId() > 0);
    }

    @Test @org.junit.jupiter.api.Order(2)
    void testFindAllAndUpdate() {
        assertTrue(
                orderRepo.getAllOrders()
                        .stream().anyMatch(o -> o.getId() == order.getId()));

        order.setQuantity(20);
        order.setPrice(new BigDecimal("119.80"));
        assertDoesNotThrow(() -> orderRepo.updateOrder(order));

        Order re = orderRepo.findOrderById(order.getId());
        assertNotNull(re);
        assertEquals(20, re.getQuantity());
        assertEquals(new BigDecimal("119.80"), re.getPrice());
    }

    @Test @org.junit.jupiter.api.Order(3)
    void testQueries() {
        // ID produktu
        assertTrue(
                orderRepo.findOrdersByProductId(product.getId())
                        .stream().allMatch(o -> o.getProduct().getId() == product.getId()));

        // ID pracownika
        assertTrue(
                orderRepo.findOrdersByEmployeeId(employee.getId())
                        .stream().allMatch(o -> o.getEmployee().getId() == employee.getId()));

        // dokładna data
        assertTrue(
                orderRepo.findOrdersByDate(localOrderDate)
                        .stream().allMatch(o ->
                                o.getDate().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                        .equals(localOrderDate)));

        // zakres dat
        assertTrue(
                orderRepo.findDateRangeOrders(
                                localOrderDate.minusDays(1),
                                localOrderDate.plusDays(1))
                        .stream().anyMatch(o -> o.getId() == order.getId()));

        // minimalna ilość
        assertTrue(
                orderRepo.findOrdersWithMinimalQuantity(20)
                        .stream().allMatch(o -> o.getQuantity() >= 20));

        // przedział cenowy
        BigDecimal min = new BigDecimal("100.00");
        BigDecimal max = new BigDecimal("200.00");
        assertTrue(
                orderRepo.findPriceRangeOrders(min, max)
                        .stream().allMatch(o ->
                                o.getPrice().compareTo(min) >= 0 &&
                                        o.getPrice().compareTo(max) <= 0));
    }

    @Test @org.junit.jupiter.api.Order(4)
    void testDelete() {
        assertDoesNotThrow(() -> orderRepo.removeOrders(order.getId()));

        assertNull(orderRepo.findOrderById(order.getId()));
        assertTrue(orderRepo.getAllOrders()
                .stream().noneMatch(o -> o.getId() == order.getId()));
    }

    @AfterAll
    static void closeAll() {
        orderRepo.close();
        productRepo.close();
        userRepo.close();
    }
}