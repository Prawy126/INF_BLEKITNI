/*
 * Classname: TransactionProduct
 * Version information: 1.1
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MapsId;

@Entity
@Table(name = "Transakcje_Produkty")
@Access(AccessType.FIELD)
public class TransactionProduct {

    // Inicjalizacja logera
    private static final Logger logger = LogManager.getLogger(TransactionProduct.class);

    @EmbeddedId
    private TransactionProductId id;

    @ManyToOne
    @MapsId("transactionId")
    @JoinColumn(name = "Id_transakcji")
    private Transaction transaction;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "Id_produktu")
    private Product product;

    @Column(name = "Ilosc", nullable = false)
    private int quantity;

    // Konstruktory

    public TransactionProduct() {
        logger.debug("Utworzono nową instancję TransactionProduct (domyślny konstruktor).");
    }

    public TransactionProduct(Transaction transaction, Product product, int quantity) {
        this.id = new TransactionProductId(transaction.getId(), product.getId());
        this.transaction = transaction;
        this.product = product;
        this.quantity = quantity;

        logger.info("Utworzono powiązanie produktu ID:{} z transakcją ID:{} (ilość: {})",
                product.getId(), transaction.getId(), quantity);
    }

    // Gettery i settery

    public TransactionProductId getId() {
        return id;
    }

    public void setId(TransactionProductId id) {
        logger.debug("Zaktualizowano ID powiązania transakcja-produkt na: {}", id);
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        if (transaction != null) {
            logger.debug("Zaktualizowano powiązaną transakcję na ID: {}", transaction.getId());
        } else {
            logger.warn("Próba ustawienia transakcji na wartość null.");
        }
        this.transaction = transaction;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        if (product != null) {
            logger.debug("Zaktualizowano powiązany produkt na ID: {}", product.getId());
        } else {
            logger.warn("Próba ustawienia produktu na wartość null.");
        }
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            logger.warn("Nieprawidłowa ilość produktu: {}. Ilość musi być większa niż zero.", quantity);
            throw new IllegalArgumentException("Ilość produktu musi być większa niż zero.");
        }

        logger.info("Zaktualizowano ilość produktu z {} na {}", this.quantity, quantity);
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "TransactionProduct{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                '}';
    }
}