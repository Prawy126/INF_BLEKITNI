import org.example.sys.Order;
import org.example.sys.Employee;
import org.example.sys.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testConstructorAndGetters() {
        Product produkt = new Product("Laptop", "Elektronika", 2500.00);
        Employee pracownik = new Employee();
        pracownik.setLogin("test_user");
        Date data = new Date();

        Order order = new Order(produkt, pracownik, 10, new BigDecimal("25000.00"), data);

        assertEquals(produkt, order.getProduct());
        assertEquals(pracownik, order.getEmployee());
        assertEquals(10, order.getQuantity());
        assertEquals(new BigDecimal("25000.00"), order.getPrice());
        assertEquals(data, order.getDate());
    }

    @Test
    void testSettersAndToString() {
        Order order = new Order();

        Product produkt = new Product("Tablet", "Elektronika", 1000.00);
        Employee pracownik = new Employee();
        pracownik.setLogin("admin");
        Date data = new Date();

        order.setProduct(produkt);
        order.setEmployee(pracownik);
        order.setQuantity(5);
        order.setPrice(new BigDecimal("5000.00"));
        order.setDate(data);

        assertEquals(produkt, order.getProduct());
        assertEquals(pracownik, order.getEmployee());
        assertEquals(5, order.getQuantity());
        assertEquals(new BigDecimal("5000.00"), order.getPrice());
        assertEquals(data, order.getDate());

        assertTrue(order.toString().contains("Tablet"));
        assertTrue(order.toString().contains("5000.00"));
        assertTrue(order.toString().contains("admin"));
    }
}
