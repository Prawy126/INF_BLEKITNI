/*
 * Classname: TransactionTest
 * Version information: 1.2
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.Product;
import org.example.sys.Transaction;
import org.example.sys.TransactionProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testy jednostkowe dla klasy Transaction.
 */
@DisplayName("TransactionTest")
class TransactionTest {

    private Transaction transaction;
    private Product p1;
    private Product p2;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        // ustawiamy jakąś datę, choć nie jest używana w tych testach
        transaction.setDate(new Date());

        p1 = new Product("Widget", "Gadgets", 9.99);
        p2 = new Product("Gizmo",  "Gadgets", 19.95);

        // w testach CompositeId i TransactionProductTest zależy jeszcze od tego,
        // żeby transaction.getId() zwracało cokolwiek – w razie czego można tu
        // transaction.setId(1);
    }

    /**
     * Testuje dodanie produktu do transakcji:
     * czy powstaje obiekt TransactionProduct,
     * czy przechowuje poprawną ilość i referencję do Transaction.
     */
    @Test
    @DisplayName("addProduct should create a TransactionProduct link")
    void testAddProduct() {
        transaction.addProduct(p1, 3);

        List<TransactionProduct> tps = transaction.getTransactionProducts();
        assertEquals(1, tps.size(),
                "Powinien być dokładnie jeden TransactionProduct");

        TransactionProduct tp = tps.get(0);
        assertEquals(p1, tp.getProduct(), "Product powinien być p1");
        assertEquals(3,  tp.getQuantity(),
                "Quantity powinno być 3");
        assertEquals(transaction, tp.getTransaction(),
                "Back-reference do Transaction powinien być ustawiony");
    }

    /**
     * Testuje usuwanie produktu z transakcji:
     * czy removeProduct usuwa tylko wskazany produkt.
     */
    @Test
    @DisplayName("removeProduct should remove only the specified " +
            "TransactionProduct")
    void testRemoveProduct() {
        transaction.addProduct(p1, 2);
        transaction.addProduct(p2, 4);
        assertEquals(2, transaction.getTransactionProducts().size(),
                "Na początku powinny być dwie pozycje");

        // usuwamy p1
        transaction.removeProduct(p1);
        List<TransactionProduct> after = transaction.getTransactionProducts();
        assertEquals(1, after.size(),
                "Powinna pozostać jedna pozycja po usunięciu");
        assertEquals(p2, after.get(0).getProduct(),
                "Pozostały produkt powinien być p2");
    }

    /**
     * Testuje zwracanie listy czystych obiektów Product:
     * getProducts() powinno odzwierciedlać kolejność dodawania.
     */
    @Test
    @DisplayName("getProducts should return list of Products " +
            "in insertion order")
    void testGetProducts() {
        transaction.addProduct(p1, 1);
        transaction.addProduct(p2, 5);

        List<Product> products = transaction.getProducts();
        assertEquals(2, products.size(),
                "Powinny być dwa produkty w liście");
        assertEquals(p1, products.get(0), "Pierwszy produkt to p1");
        assertEquals(p2, products.get(1), "Drugi produkt to p2");
    }
}
