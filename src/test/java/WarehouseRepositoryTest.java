/*
 * Classname: WarehouseRepositoryTest
 * Version information: 1.3
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.database.ProductRepository;
import org.example.database.WarehouseRepository;
import org.example.sys.Product;
import org.example.sys.Warehouse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.AfterAll;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WarehouseRepositoryTest {

    private static ProductRepository productRepo;
    private static WarehouseRepository warehouseRepo;

    private static Product   testProduct;
    private static Warehouse testWarehouse;

    @BeforeAll
    static void setup() {
        productRepo   = new ProductRepository();
        warehouseRepo = new WarehouseRepository();
    }

    @Test
    @Order(1)
    void testAddProductAndWarehouse() {
        // 1. add a product for FK
        testProduct = new Product("Jogurt truskawkowy",
                "Nabiał", 3.49);
        assertDoesNotThrow(() -> productRepo.addProduct(testProduct),
                "Powinno się dodać produkt bez wyjątku");
        assertTrue(testProduct.getId() > 0,
                "Produkt powinien mieć ID");

        // 2. add stock record
        testWarehouse = new Warehouse(testProduct, 120);
        assertDoesNotThrow(() -> warehouseRepo.addWarehouseState(testWarehouse),
                "Powinno się dodać rekord magazynowy bez wyjątku");
        // no generated ID here (uses product PK), so ensure getProduct().getId() matches
        assertEquals(testProduct.getId(), testWarehouse.getProduct().getId());
    }

    @Test
    @Order(2)
    void testFindAllAndUpdate() {
        // 3. fetch all stock records
        List<Warehouse> all = warehouseRepo.getAllStates();
        assertTrue(all.stream().anyMatch(
                w -> w.getProduct().getId() == testProduct.getId()),
                "Nowy rekord magazynu powinien pojawić się na liście");

        // 4. update quantity
        testWarehouse.setQuantity(100);
        assertDoesNotThrow(() -> warehouseRepo.updateState(testWarehouse),
                "Powinno zaktualizować rekord w magazynie " +
                        "bez wyjątku.");

        Warehouse reloaded = warehouseRepo
                .findStateByProductId(testProduct.getId());
        assertNotNull(reloaded, "Przeładowanie zapisu nie może " +
                "być puste");
        assertEquals(100, reloaded.getQuantity(),
                "Ilość powinna zostać zaktualizowana do 100");
    }

    @Test
    @Order(3)
    void testQueries() {
        // quantity == 100
        List<Warehouse> eq100 = warehouseRepo.findByQuantity(100);
        assertTrue(eq100.stream()
                .allMatch(w -> w.getQuantity() == 100),
                "Wszystko musi mieć ilość 100");

        // quantity < 110
        List<Warehouse> lt110 = warehouseRepo.findByQuantityLowerThan(110);
        assertTrue(lt110.stream()
                .allMatch(w -> w.getQuantity() < 110),
                "Wszystkie muszą mieć ilość < 110");

        // quantity > 50
        List<Warehouse> gt50 = warehouseRepo.findByQuantityGreaterThan(50);
        assertTrue(gt50.stream()
                .allMatch(w -> w.getQuantity() > 50),
                "Wszystkie muszą mieć ilość > 50");

        // between 80 and 120
        List<Warehouse> between = warehouseRepo.findByQuantityBetween(80, 120);
        assertTrue(between.stream()
                        .anyMatch(
                                w -> w.getProduct().getId()
                                        == testProduct.getId()),
                "Rekord powinien pojawić się w zakresie [80,120]");
    }

    @Test
    @Order(4)
    void testDelete() {
        assertDoesNotThrow(() -> warehouseRepo.removeState(testProduct.getId()),
                "Należy usunąć rekord magazynu bez wyjątku");

        Warehouse gone = warehouseRepo
                .findStateByProductId(testProduct.getId());
        assertNull(gone, "Usunięty rekord magazynu nie powinien " +
                "być już znaleziony");

        List<Warehouse> allAfter = warehouseRepo.getAllStates();
        assertTrue(allAfter.stream()
                        .noneMatch(
                                w -> w.getProduct().getId()
                                        == testProduct.getId()),
                "Usunięty rekord nie może pojawić się na liście");
    }

    @AfterAll
    static void tearDown() {
        productRepo.close();
        warehouseRepo.close();
    }
}