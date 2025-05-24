/*
 * Classname: TransactionProductTest
 * Version information: 1.2
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.TransactionProduct;
import org.example.sys.TransactionProductId;
import org.example.sys.Transaction;
import org.example.sys.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testy jednostkowe dla klasy TransactionProduct.
 */
class TransactionProductTest {

    /**
     * Testuje domyślny konstruktor i gettery po użyciu setterów.
     */
    @Test
    void testDefaultConstructorAndSetters() {
        TransactionProduct tp = new TransactionProduct();

        Transaction tx = new Transaction();
        tx.setId(10);
        Product prod = new Product("Test", "Cat", 1.23);
        prod.setId(20);

        tp.setTransaction(tx);
        tp.setProduct(prod);
        tp.setQuantity(5);

        assertNotNull(tp.getTransaction(), "transaction powinien być nie-null");
        assertEquals(10, tp.getTransaction().getId(), "getTransaction().getId() powinno zwrócić 10");
        assertNotNull(tp.getProduct(), "product powinien być nie-null");
        assertEquals(20, tp.getProduct().getId(), "getProduct().getId() powinno zwrócić 20");
        assertEquals(5, tp.getQuantity(), "getQuantity() powinno zwrócić 5");
    }

    /**
     * Testuje konstruktor pełny (transaction, product, quantity) i poprawność id.
     */
    @Test
    void testFullConstructorAndCompositeId() {
        Transaction tx = new Transaction();
        tx.setId(7);
        Product prod = new Product("X", "Y", 9.99);
        prod.setId(11);

        TransactionProduct tp = new TransactionProduct(tx, prod, 42);

        TransactionProductId expectedId = new TransactionProductId(7, 11);
        assertEquals(expectedId, tp.getId(), "id powinno być (7,11)");
        assertEquals(42, tp.getQuantity(), "quantity powinno być 42");
    }

    /**
     * Testuje poprawność setter/getter dla pola id.
     */
    @Test
    void testSetAndGetId() {
        TransactionProduct tp = new TransactionProduct();
        TransactionProductId id = new TransactionProductId(3, 4);
        tp.setId(id);
        assertEquals(id, tp.getId(), "getId() powinno zwrócić ustawione id");
    }

    /**
     * Testuje modyfikację ilości (quantity).
     */
    @Test
    void testUpdateQuantity() {
        TransactionProduct tp = new TransactionProduct();
        tp.setQuantity(1);
        assertEquals(1, tp.getQuantity(), "quantity powinno być 1");

        tp.setQuantity(99);
        assertEquals(99, tp.getQuantity(), "quantity powinno zostać zaktualizowane do 99");
    }
}
