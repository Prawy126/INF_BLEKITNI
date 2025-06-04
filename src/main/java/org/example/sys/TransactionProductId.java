/*
 * Classname: TransactionProductId
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Objects;

/**
 * Klasa reprezentująca złożony klucz główny dla encji TransactionProduct.
 * Implementuje interfejs Serializable, co jest wymagane dla kluczy złożonych w JPA.
 * Składa się z identyfikatora transakcji oraz identyfikatora produktu.
 */
@Embeddable
public class TransactionProductId implements Serializable {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą TransactionProductId.
     */
    private static final Logger logger
            = LogManager.getLogger(TransactionProductId.class);

    /**
     * Identyfikator transakcji.
     * Stanowi część złożonego klucza głównego.
     */
    @Column(name = "Id_transakcji")
    private int transactionId;

    /**
     * Identyfikator produktu.
     * Stanowi część złożonego klucza głównego.
     */
    @Column(name = "Id_produktu")
    private int productId;

    /**
     * Konstruktor domyślny wymagany przez JPA.
     * Operacja jest logowana na poziomie INFO.
     */
    public TransactionProductId() {
        logger.info("Tworzenie nowego obiektu" +
                " TransactionProductId (konstruktor domyślny)");
    }

    /**
     * Konstruktor z parametrami.
     * Tworzy złożony identyfikator z podanych parametrów.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param transactionId identyfikator transakcji
     * @param productId identyfikator produktu
     */
    public TransactionProductId(int transactionId, int productId) {
        this.transactionId = transactionId;
        this.productId = productId;
        logger.info("Tworzenie nowego obiektu TransactionProductId z" +
                " parametrami: Id_transakcji={}," +
                " Id_produktu={}", transactionId, productId);
    }

    /**
     * @return identyfikator transakcji
     */
    public int getTransactionId() {
        logger.debug("Pobieranie Id_transakcji: {}", transactionId);
        return transactionId;
    }

    /**
     * @param transactionId nowy identyfikator transakcji
     */
    public void setTransactionId(int transactionId) {
        logger.debug("Ustawianie Id_transakcji na: {}", transactionId);
        this.transactionId = transactionId;
    }

    /**
     * @return identyfikator produktu
     */
    public int getProductId() {
        logger.debug("Pobieranie Id_produktu: {}", productId);
        return productId;
    }

    /**
     * @param productId nowy identyfikator produktu
     */
    public void setProductId(int productId) {
        logger.debug("Ustawianie Id_produktu na: {}", productId);
        this.productId = productId;
    }

    /**
     * Porównuje ten obiekt z innym do równości.
     * Metoda niezbędna do prawidłowego działania JPA z kluczami złożonymi.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @param o obiekt do porównania
     * @return true jeśli obiekty są równe, false w przeciwnym przypadku
     */
    @Override
    public boolean equals(Object o) {
        logger.trace("Wywołano metodę equals()");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionProductId that = (TransactionProductId) o;
        return transactionId
                == that.transactionId && productId == that.productId;
    }

    /**
     * Oblicza kod mieszający dla tego obiektu.
     * Metoda niezbędna do prawidłowego działania JPA z kluczami złożonymi.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return wyliczony kod mieszający
     */
    @Override
    public int hashCode() {
        logger.trace("Wywołano metodę hashCode()");
        return Objects.hash(transactionId, productId);
    }
}