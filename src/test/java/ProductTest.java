import org.example.sys.Product;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testParameterizedConstructor() {
        Product product = new Product("Laptop", "Electronics", 1500.0, 10);

        assertEquals("Laptop", product.getName());
        assertEquals("Electronics", product.getCategory());
        assertEquals(1500.0, product.getPrice());
        assertEquals(10, product.getQuantity());
    }

    @Test
    void testConstructorWithDefaultQuantity() {
        Product product = new Product("Smartphone", "Electronics", 800.0);

        assertEquals("Smartphone", product.getName());
        assertEquals("Electronics", product.getCategory());
        assertEquals(800.0, product.getPrice());
        assertEquals(0, product.getQuantity());
    }

    @Test
    void testSettersAndGetters() {
        Product product = new Product("Book", "Stationery", 20.0, 5);
        product.setName("Notebook");
        product.setCategory("Office Supplies");
        product.setPrice(15.0);
        product.setQuantity(20);

        assertEquals("Notebook", product.getName());
        assertEquals("Office Supplies", product.getCategory());
        assertEquals(15.0, product.getPrice());
        assertEquals(20, product.getQuantity());
    }

    @Test
    void testPriceValidation() {
        Product product = new Product("Chair", "Furniture", 100.0, 5);
        product.setPrice(-50.0);
        assertEquals(100.0, product.getPrice(), 0.01);
    }

    @Test
    void testQuantityValidation() {
        Product product = new Product("Table", "Furniture", 200.0, 3);
        product.setQuantity(-10);
        assertEquals(3, product.getQuantity());
    }

    @Test
    void testToString() {
        Product product = new Product("Desk", "Furniture", 300.0, 2);
        String result = product.toString();
        String expected = "Product{name='Desk', category='Furniture', price=300,00, quantity=2}";
        assertEquals(expected, result);
    }

    @Test
    void testEdgeCasesForPriceAndQuantity() {
        Product product = new Product("Pen", "Stationery", 1.0, 1);
        product.setPrice(0.0);
        product.setQuantity(0);

        assertEquals(0.0, product.getPrice(), 0.01);
        assertEquals(0, product.getQuantity());
    }
}