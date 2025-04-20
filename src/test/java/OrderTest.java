import org.example.sys.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testConstructorAndGetters() {
        Order order = new Order(1, "Laptop", 10, "Tech Supplier", "2023-10-01", "Pending");
        assertEquals(1, order.getId());
        assertEquals("Laptop", order.getProductName());
        assertEquals(10, order.getQuantity());
        assertEquals("Tech Supplier", order.getSupplier());
        assertEquals("2023-10-01", order.getOrderDate());
        assertEquals("Pending", order.getStatus());
    }

    @Test
    void testDefaultValues() {
        Order order = new Order(2, "Smartphone", 5, "Mobile Supplier", "2023-10-02", "Shipped");
        assertEquals(2, order.getId());
        assertEquals("Smartphone", order.getProductName());
        assertEquals(5, order.getQuantity());
        assertEquals("Mobile Supplier", order.getSupplier());
        assertEquals("2023-10-02", order.getOrderDate());
        assertEquals("Shipped", order.getStatus());
    }
}