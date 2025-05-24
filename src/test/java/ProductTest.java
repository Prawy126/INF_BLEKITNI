

import org.example.sys.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla klasy Product.
 */
@DisplayName("ProductTest")
class ProductTest {

    /**
     * Tworzy przykładowy obiekt Product do testów.
     */
    private Product createTestProduct() {
        return new Product("Chleb", "Pieczywo", 3.49);
    }

    /**
     * Testuje, czy konstruktor parametryczny ustawia poprawnie pola name, category i price.
     */
    @Test
    @DisplayName("constructor should initialize fields correctly")
    void testParameterizedConstructor() {
        Product product = createTestProduct();

        assertEquals("Chleb", product.getName(),    "Nazwa powinna być 'Chleb'");
        assertEquals("Pieczywo", product.getCategory(), "Kategoria powinna być 'Pieczywo'");
        assertEquals(BigDecimal.valueOf(3.49), product.getPrice(), "Cena powinna być 3.49");
    }

    /**
     * Testuje, czy metoda toString zawiera wartości pól.
     */
    @Test
    @DisplayName("toString should contain all fields")
    void testToString() {
        Product product = createTestProduct();
        String s = product.toString();
        assertTrue(s.contains("Chleb"),     "toString powinien zawierać 'Chleb'");
        assertTrue(s.contains("Pieczywo"),  "toString powinien zawierać 'Pieczywo'");
        assertTrue(s.contains("3.49"),      "toString powinien zawierać '3.49'");
    }

    /**
     * Testuje settery i gettery dla pola name, category i price (po BigDecimal).
     */
    @Test
    @DisplayName("setters and getters for name, category, price should work")
    void testSettersAndGetters() {
        Product product = new Product();
        product.setName("Mleko");
        product.setCategory("Nabiał");
        product.setPrice(BigDecimal.valueOf(2.99));

        assertEquals("Mleko", product.getName(),      "Name setter/getter nie działa");
        assertEquals("Nabiał", product.getCategory(), "Category setter/getter nie działa");
        assertEquals(BigDecimal.valueOf(2.99), product.getPrice(), "Price setter/getter nie działa");
    }

    /**
     * Testuje, że nie można ustawić ceny na wartość ujemną.
     */
    @Test
    @DisplayName("setPrice should ignore negative values")
    void testSetPriceNegative() {
        Product product = createTestProduct();
        product.setPrice(BigDecimal.valueOf(-100.0));  // walidacja: nie powinno się ustawić
        assertNotEquals(BigDecimal.valueOf(-100.0), product.getPrice(),
                "Cena nie może być ujemna");
    }

    /**
     * Testuje, że można ustawić cenę na zero.
     */
    @Test
    @DisplayName("setPrice should accept zero value")
    void testSetPriceZero() {
        Product product = createTestProduct();
        product.setPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, product.getPrice(),
                "Cena może być zerowa");
    }

    /**
     * Testuje zmianę nazwy i kategorii.
     */
    @Test
    @DisplayName("setName and setCategory should update values")
    void testChangeNameAndCategory() {
        Product product = createTestProduct();
        product.setName("Jogurt");
        product.setCategory("Nabiał");

        assertEquals("Jogurt", product.getName(),
                "Name powinien zostać zmieniony na 'Jogurt'");
        assertEquals("Nabiał", product.getCategory(),
                "Category powinien zostać zmieniony na 'Nabiał'");
    }

    /**
     * Testuje setter/getter dla pola id.
     */
    @Test
    @DisplayName("setId and getId should work correctly")
    void testSetAndGetId() {
        Product product = createTestProduct();
        product.setId(123);
        assertEquals(123, product.getId(), "Id powinno zostać ustawione na 123");
    }
}
