/*
 * Classname: ProductRepositoryTest
 * Version information: 1.4
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.database.ProductRepository;
import org.example.sys.Product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductRepositoryTest {

    private static ProductRepository repo;
    private static Product p1, p2, p3;

    @BeforeAll
    static void setup() {
        repo = new ProductRepository();
        // create three products
        p1 = new Product("Masło",   "Nabiał",      6.99);
        p2 = new Product("Szampon", "Kosmetyki",  12.49);
        p3 = new Product("Mleko",   "Nabiał",      4.50);

        assertDoesNotThrow(() -> {
            repo.addProduct(p1);
            repo.addProduct(p2);
            repo.addProduct(p3);
        }, "Should add three products without exception");

        assertTrue(p1.getId() > 0, "p1 should have an ID");
        assertTrue(p2.getId() > 0, "p2 should have an ID");
        assertTrue(p3.getId() > 0, "p3 should have an ID");
    }

    @Test
    @Order(1)
    void testFindAll() {
        List<Product> all = repo.getAllProducts();
        // we should have at least our three
        assertTrue(all.size() >= 3, "Should find at least 3 products");
        assertTrue(all.stream().anyMatch(p -> p.getId() == p1.getId()));
        assertTrue(all.stream().anyMatch(p -> p.getId() == p2.getId()));
        assertTrue(all.stream().anyMatch(p -> p.getId() == p3.getId()));
    }

    @Test
    @Order(2)
    void testFindByIdAndUpdate() {
        Product found = repo.findProductById(p1.getId());
        assertNotNull(found, "Should find p1 by its ID");
        assertEquals("Masło", found.getName());

        // update its category
        found.setCategory("Produkty spożywcze");
        assertDoesNotThrow(() -> repo.updateProduct(found));
        Product reloaded = repo.findProductById(p1.getId());
        assertEquals("Produkty spożywcze", reloaded.getCategory());
    }

    @Test
    @Order(3)
    void testUpdatePrice() {
        // update price of p2
        assertDoesNotThrow(() ->
                repo.updateProductPrice(p2.getId(), BigDecimal.valueOf(10.99))
        );
        Product reloaded = repo.findProductById(p2.getId());
        assertEquals(BigDecimal.valueOf(10.99), reloaded.getPrice());
    }


    @Test
    @Order(4)
    void testQueries() {
        // exact category match "Nabiał"
        List<Product> dairy = repo.getProductsByCategory("Nabiał");
        assertTrue(dairy.stream().anyMatch(p -> p.getId() == p3.getId()));
        assertFalse(dairy.stream().anyMatch(p -> p.getId() == p1.getId()));

        // price range [5.00, 11.00]
        List<Product> range = repo.getPriceRangeProducts(
                BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(11.00)
        );
        assertTrue(range.stream().anyMatch(p -> p.getId() == p1.getId()));
        assertTrue(range.stream().anyMatch(p -> p.getId() == p2.getId()));
        assertFalse(range.stream().anyMatch(p -> p.getId() == p3.getId()));

        // delete by category
        int removed = repo.removeProductsFromCategory("Produkty spożywcze");
        assertTrue(removed >= 1, "Should remove at least the p1 retagged earlier");

        // delete single p2
        assertDoesNotThrow(() -> repo.removeProduct(p2.getId()));
        assertNull(repo.findProductById(p2.getId()), "p2 should be deleted");
    }


    @Test
    @Order(5)
    void testAdditionalSearches() {
        // name contains "M"
        List<Product> nameM = repo.findByName("m");
        assertTrue(nameM.stream().anyMatch(p -> p.getId() == p3.getId()));

        // exact price 4.50
        List<Product> priceExact = repo.findByExactPrice(BigDecimal.valueOf(4.50));
        assertTrue(priceExact.stream().anyMatch(p -> p.getId() == p3.getId()));

        // price >= 6.00
        List<Product> priceMin = repo.findByMinPrice(BigDecimal.valueOf(6.00));
        assertTrue(
                priceMin.stream()
                        .anyMatch(p -> p.getPrice().compareTo(BigDecimal.valueOf(6.00)) >= 0)
        );

        // price <= 11.00
        List<Product> priceMax = repo.findByMaxPrice(BigDecimal.valueOf(11.00));
        assertTrue(
                priceMax.stream()
                        .allMatch(p -> p.getPrice().compareTo(BigDecimal.valueOf(11.00)) <= 0)
        );
    }


    @AfterAll
    static void tearDown() {
        repo.close();
    }
}