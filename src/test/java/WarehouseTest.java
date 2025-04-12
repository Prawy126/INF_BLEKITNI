import org.example.sys.Product;
import org.example.sys.Warehouse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    @Test
    void testConstructorInitialization() {
        Warehouse warehouse = new Warehouse(null);

        assertNotNull(warehouse.getProducts());
        assertTrue(warehouse.getProducts().isEmpty());
    }

    @Test
    void testAddProduct() {
        Warehouse warehouse = new Warehouse(null);
        Product product = new Product("Laptop", "Electronics", 1500.0, 10);

        warehouse.addProduct(product);
        assertEquals(1, warehouse.getProducts().size());
        assertTrue(warehouse.getProducts().contains(product));
    }

    @Test
    void testAddProductWithNull() {
        Warehouse warehouse = new Warehouse(null);

        warehouse.addProduct(null);
        assertTrue(warehouse.getProducts().isEmpty());
    }

    @Test
    void testRemoveProduct() {
        Product product = new Product("Laptop", "Electronics", 1500.0, 10);
        List<Product> products = new ArrayList<>(List.of(product));
        Warehouse warehouse = new Warehouse(products);

        warehouse.removeProduct(product);
        assertTrue(warehouse.getProducts().isEmpty());
    }

    @Test
    void testRemoveNonExistentProduct() {
        Product product1 = new Product("Laptop", "Electronics", 1500.0, 10);
        Product product2 = new Product("Smartphone", "Electronics", 800.0, 5);
        List<Product> products = new ArrayList<>(List.of(product1));
        Warehouse warehouse = new Warehouse(products);

        warehouse.removeProduct(product2);
        assertEquals(1, warehouse.getProducts().size());
        assertTrue(warehouse.getProducts().contains(product1));
    }

    @Test
    void testGetProductByName() {
        Product product = new Product("Laptop", "Electronics", 1500.0, 10);
        Warehouse warehouse = new Warehouse(List.of(product));
        Product result = warehouse.getProduct("Laptop");

        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    void testGetProductByNameCaseInsensitive() {
        Product product = new Product("Laptop", "Electronics", 1500.0, 10);
        Warehouse warehouse = new Warehouse(List.of(product));
        Product result = warehouse.getProduct("laptop");

        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    void testGetProductByNonExistentName() {
        Warehouse warehouse = new Warehouse(null);
        Product result = warehouse.getProduct("NonExistent");

        assertNull(result);
    }

    @Test
    void testUpdateProduct() {
        Product product = new Product("Laptop", "Electronics", 1500.0, 10);
        Warehouse warehouse = new Warehouse(List.of(product));

        warehouse.updateProduct(product, "Updated Laptop", "Updated Category", 2000.0, 5);
        assertEquals("Updated Laptop", product.getName());
        assertEquals("Updated Category", product.getCategory());
        assertEquals(2000.0, product.getPrice());
        assertEquals(5, product.getQuantity());
    }

    @Test
    void testGetProductsByCategory() {
        Product product1 = new Product("Laptop", "Electronics", 1500.0, 10);
        Product product2 = new Product("Smartphone", "Electronics", 800.0, 5);
        Product product3 = new Product("Chair", "Furniture", 200.0, 3);
        Warehouse warehouse = new Warehouse(List.of(product1, product2, product3));
        List<Product> electronics = warehouse.getProductsByCategory("Electronics");

        assertEquals(2, electronics.size());
        assertTrue(electronics.contains(product1));
        assertTrue(electronics.contains(product2));
    }

    @Test
    void testGetProductsByNonExistentCategory() {
        Product product1 = new Product("Laptop", "Electronics", 1500.0, 10);
        Product product2 = new Product("Smartphone", "Electronics", 800.0, 5);
        Warehouse warehouse = new Warehouse(List.of(product1, product2));
        List<Product> nonExistentCategory = warehouse.getProductsByCategory("Furniture");

        assertTrue(nonExistentCategory.isEmpty());
    }

    @Test
    void testGetQuantity() {
        Product product = new Product("Laptop", "Electronics", 1500.0, 10);
        Warehouse warehouse = new Warehouse(List.of(product));

        int quantity = warehouse.getQuantity(product);
        assertEquals(10, quantity);
    }

    @Test
    void testGetQuantityForNullProduct() {
        Warehouse warehouse = new Warehouse(null);

        int quantity = warehouse.getQuantity(null);

        assertEquals(0, quantity);
    }

    @Test
    void testToString() {
        Product product1 = new Product("Laptop", "Electronics", 1500.0, 10);
        Product product2 = new Product("Smartphone", "Electronics", 800.0, 5);
        Warehouse warehouse = new Warehouse(List.of(product1, product2));
        String result = warehouse.toString();
        String expected = "Warehouse inventory:\n" +
                "Product{name='Laptop', category='Electronics', price=1500,00, quantity=10}\n" +
                "Product{name='Smartphone', category='Electronics', price=800,00, quantity=5}\n";

        assertEquals(expected, result);
    }
}