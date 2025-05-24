/*
 * Classname: WarehouseTest
 * Version information: 1.1
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.Product;
import org.example.sys.Warehouse;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WarehouseTest {

    @Test
    void testParameterizedConstructor() {
        Product product = new Product("Laptop", "Elektronika", 1500.00);
        Warehouse warehouse = new Warehouse(product, 10);

        assertEquals(product, warehouse.getProduct());
        assertEquals("Laptop", warehouse.getProduct().getName());
        // Porównujemy BigDecimal, a nie double
        assertEquals(BigDecimal.valueOf(1500.00), warehouse.getProduct().getPrice());
        assertEquals(10, warehouse.getQuantity());
    }

    @Test
    void testSettersAndGetters() {
        Product product = new Product("Monitor", "Elektronika", 799.99);
        Warehouse warehouse = new Warehouse();
        warehouse.setProduct(product);
        warehouse.setQuantity(25);

        assertEquals("Monitor", warehouse.getProduct().getName());
        // BigDecimal.valueOf – tak samo jak w konstruktorze
        assertEquals(BigDecimal.valueOf(799.99), warehouse.getProduct().getPrice());
        assertEquals(25, warehouse.getQuantity());
    }

    @Test
    void testZeroQuantityAndZeroPrice() {
        Product product = new Product("Długopis", "Biuro", 0.00);
        Warehouse warehouse = new Warehouse(product, 0);

        assertEquals(0, warehouse.getQuantity());
        // Porównanie dokładnie tego BigDecimal, którego użyje konstruktor
        assertEquals(BigDecimal.valueOf(0.0), warehouse.getProduct().getPrice());
    }

    @Test
    void testNegativeValues() {
        // Ujemna cena jest ignorowana (setter nie nadpisze price), więc price pozostaje null
        Product product = new Product("Krzesło", "Meble", -50.00);
        Warehouse warehouse = new Warehouse(product, -10);

        assertNull(warehouse.getProduct().getPrice(), "Ujemna cena powinna zostać zignorowana");
        // Ilość ujemna jest dozwolona
        assertEquals(-10, warehouse.getQuantity());
    }

    @Test
    void testToStringContainsFields() {
        Product product = new Product("Biurko", "Meble", 300.00);
        Warehouse warehouse = new Warehouse(product, 5);
        String result = warehouse.toString();

        assertTrue(result.contains("Biurko"));
        assertTrue(result.contains("5"));
    }
}
