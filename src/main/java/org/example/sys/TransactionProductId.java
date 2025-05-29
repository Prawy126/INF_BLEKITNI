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

@Embeddable
public class TransactionProductId implements Serializable {

    private static final Logger logger = LogManager.getLogger(TransactionProductId.class);

    @Column(name = "Id_transakcji")
    private int transactionId;

    @Column(name = "Id_produktu")
    private int productId;

    // Konstruktory

    public TransactionProductId() {
        logger.info("Tworzenie nowego obiektu TransactionProductId (konstruktor domyślny)");
    }

    public TransactionProductId(int transactionId, int productId) {
        this.transactionId = transactionId;
        this.productId = productId;
        logger.info("Tworzenie nowego obiektu TransactionProductId z parametrami: Id_transakcji={}, Id_produktu={}", transactionId, productId);
    }

    // Gettery i settery

    public int getTransactionId() {
        logger.debug("Pobieranie Id_transakcji: {}", transactionId);
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        logger.debug("Ustawianie Id_transakcji na: {}", transactionId);
        this.transactionId = transactionId;
    }

    public int getProductId() {
        logger.debug("Pobieranie Id_produktu: {}", productId);
        return productId;
    }

    public void setProductId(int productId) {
        logger.debug("Ustawianie Id_produktu na: {}", productId);
        this.productId = productId;
    }

    // Equals i hashCode

    @Override
    public boolean equals(Object o) {
        logger.trace("Wywołano metodę equals()");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionProductId that = (TransactionProductId) o;
        return transactionId == that.transactionId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        logger.trace("Wywołano metodę hashCode()");
        return Objects.hash(transactionId, productId);
    }
}