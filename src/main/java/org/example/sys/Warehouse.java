/*
 * Classname: Warehouse
 * Version information: 1.1
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = "StanyMagazynowe")
@Access(AccessType.FIELD)
public class Warehouse {

    private static final Logger logger = LogManager.getLogger(Warehouse.class);

    @Id
    @Column(name = "Id_produktu")
    private int productId;

    @OneToOne
    @JoinColumn(name = "Id_produktu", insertable = false, updatable = false)
    private Product product;

    @Column(name = "Ilosc", nullable = false)
    private int quantity;

    public Warehouse() {
        logger.info("Tworzenie nowego obiektu Warehouse (konstruktor domyślny)");
    }

    public Warehouse(Product product, int quantity) {
        this.product = product;
        this.productId = product.getId(); // synchronizacja z kluczem głównym
        this.quantity = quantity;
        logger.info("Tworzenie nowego obiektu Warehouse z parametrami: Id_produktu={}, Ilosc={}", productId, quantity);
    }

    // === Gettery i settery ===

    public int getProductId() {
        logger.debug("Pobieranie Id_produktu: {}", productId);
        return productId;
    }

    public void setProductId(int productId) {
        logger.debug("Ustawianie Id_produktu na: {}", productId);
        this.productId = productId;
    }

    public Product getProduct() {
        logger.debug("Pobieranie produktu: {}", product != null ? product.getName() : "null");
        return product;
    }

    public void setProduct(Product product) {
        logger.debug("Ustawianie produktu: {}", product != null ? product.getName() : "null");
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
        }
    }

    public int getQuantity() {
        logger.debug("Pobieranie ilości: {}", quantity);
        return quantity;
    }

    public void setQuantity(int quantity) {
        logger.debug("Ustawianie ilości na: {}", quantity);
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        logger.trace("Wywołano metodę toString()");
        return "Warehouse{" +
                "product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                '}';
    }
}