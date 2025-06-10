/*
 * Classname: TransactionProductIdTest
 * Version information: 1.2
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.TransactionProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Testy jednostkowe dla klasy TransactionProductId.
 */
@DisplayName("TransactionProductIdTest")
class TransactionProductIdTest {

    /**
     * Testuje konstruktor z argumentami oraz działanie getterów.
     */
    @Test
    @DisplayName("constructor and getters")
    void testConstructorAndGetters() {
        TransactionProductId id = new TransactionProductId(
                123, 456);

        assertEquals(123, id.getTransactionId(),
                "getTransactionId() powinien zwrócić wartość " +
                        "z konstruktora");
        assertEquals(456, id.getProductId(),
                "getProductId() powinien zwrócić wartość " +
                        "z konstruktora");
    }

    /**
     * Testuje setter’y i ponowne odczytanie wartości przez getter’y.
     */
    @Test
    @DisplayName("setters and getters")
    void testSettersAndGetters() {
        TransactionProductId id = new TransactionProductId();
        id.setTransactionId(11);
        id.setProductId(22);

        assertEquals(11, id.getTransactionId(),
                "setTransactionId powinien ustawić wartość");
        assertEquals(22, id.getProductId(),
                "setProductId powinien ustawić wartość");
    }

    /**
     * Testuje poprawność metody equals dla obiektów równych
     * i nierównych.
     */
    @Test
    @DisplayName("equals and hashCode for equal instances")
    void testEqualsAndHashCode_Equal() {
        TransactionProductId id1 = new TransactionProductId(1, 2);
        TransactionProductId id2 = new TransactionProductId(1, 2);

        assertEquals(id1, id2,
                "Równe wartości pól powinny być equal()");
        assertEquals(id1.hashCode(), id2.hashCode(),
                "hashCode() również powinien być taki sam " +
                        "dla równych obiektów");
    }

    /**
     * Testuje, że equals zwraca false dla różnych transactionId
     * lub productId.
     */
    @Test
    @DisplayName("equals returns false for different values")
    void testEquals_Different() {
        TransactionProductId id1 = new TransactionProductId(
                1, 2);
        TransactionProductId idDifferentTx = new TransactionProductId(
                99, 2);
        TransactionProductId idDifferentPr = new TransactionProductId(
                1, 88);

        assertNotEquals(id1, idDifferentTx,
                "Różne transactionId powinno dać not equal");
        assertNotEquals(id1, idDifferentPr,
                "Różne productId powinno dać not equal");
        assertNotEquals(idDifferentTx, null,
                "Porównanie z null zwraca false");
        assertNotEquals(idDifferentPr, "some string",
                "Porównanie z innym typem zwraca false");
    }
}
