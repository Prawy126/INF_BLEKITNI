/*
 * Classname: Warehouse
 * Version information: 1.1
 * Date: 2025-06-04
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

/**
 * Klasa reprezentująca stan magazynowy dla produktów w systemie.
 * Mapowana na tabelę "StanyMagazynowe" w bazie danych.
 * Przechowuje informacje o produkcie i jego dostępnej ilości w magazynie.
 * Jako klucz główny wykorzystuje identyfikator produktu
 * (relacja jeden-do-jednego).
 */
@Entity
@Table(name = "StanyMagazynowe")
@Access(AccessType.FIELD)
public class Warehouse {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą Warehouse.
     */
    private static final Logger logger = LogManager.getLogger(Warehouse.class);

    /**
     * Identyfikator produktu.
     * Stanowi klucz główny encji, a także jest
     * kluczem obcym do tabeli Produkty.
     */
    @Id
    @Column(name = "Id_produktu")
    private int productId;

    /**
     * Produkt powiązany z rekordem stanu magazynowego.
     * Relacja jeden-do-jednego z encją Product.
     * Pole jest zmapowane na tę samą kolumnę co
     * productId, ale nie bierze udziału
     * w operacjach INSERT i UPDATE.
     */
    @OneToOne
    @JoinColumn(name = "Id_produktu", insertable = false, updatable = false)
    private Product product;

    /**
     * Ilość produktu dostępna w magazynie.
     * Pole nie może być puste (nullable=false).
     */
    @Column(name = "Ilosc", nullable = false)
    private int quantity;

    /**
     * Konstruktor domyślny wymagany przez JPA.
     * Operacja jest logowana na poziomie INFO.
     */
    public Warehouse() {
        logger.info("Tworzenie nowego obiektu" +
                " Warehouse (konstruktor domyślny)");
    }

    /**
     * Konstruktor z parametrami.
     * Tworzy nowy stan magazynowy dla podanego produktu z określoną ilością.
     * Automatycznie synchronizuje identyfikator produktu z kluczem głównym.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param product produkt, którego stan magazynowy jest tworzony
     * @param quantity ilość produktu dostępna w magazynie
     */
    public Warehouse(Product product, int quantity) {
        this.product = product;
        this.productId = product.getId(); // synchronizacja z kluczem głównym
        this.quantity = quantity;
        logger.info("Tworzenie nowego obiektu Warehouse" +
                        " z parametrami: Id_produktu={}, Ilosc={}",
                productId, quantity);
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
     * @return produkt powiązany ze stanem magazynowym
     */
    public Product getProduct() {
        logger.debug("Pobieranie produktu: {}", product != null
                ? product.getName() : "null");
        return product;
    }

    /**
     * Ustawia produkt powiązany ze stanem magazynowym.
     * Dodatkowo synchronizuje identyfikator produktu z kluczem głównym.
     *
     * @param product nowy produkt do powiązania
     */
    public void setProduct(Product product) {
        logger.debug("Ustawianie produktu: {}", product != null
                ? product.getName() : "null");
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
        }
    }

    /**
     * @return ilość dostępna w magazynie
     */
    public int getQuantity() {
        logger.debug("Pobieranie ilości: {}", quantity);
        return quantity;
    }

    /**
     * @param quantity nowa ilość dostępna w magazynie
     */
    public void setQuantity(int quantity) {
        logger.debug("Ustawianie ilości na: {}", quantity);
        this.quantity = quantity;
    }

    /**
     * Zwraca reprezentację tekstową stanu magazynowego.
     * Zawiera informacje o produkcie i jego ilości.
     * Operacja jest logowana na poziomie TRACE.
     *
     * @return tekstowa reprezentacja stanu magazynowego
     */
    @Override
    public String toString() {
        logger.trace("Wywołano metodę toString()");
        return "Warehouse{" +
                "product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                '}';
    }
}