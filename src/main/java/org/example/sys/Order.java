/*
 * Classname: Order
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Klasa reprezentująca zamówienie w systemie.
 * Zawiera informacje o produkcie, pracowniku, ilości, cenie i dacie zamówienia.
 */
@Entity
@Table(name = "Zamowienia")
@Access(AccessType.FIELD)
public class Order {

    // Inicjalizacja logera
    private static final Logger logger = LogManager.getLogger(Order.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "Id_produktu", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee employee;

    @Column(name = "Ilosc", nullable = false)
    private int quantity;

    @Column(name = "Cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Temporal(TemporalType.DATE)
    @Column(name = "Data", nullable = false)
    private Date date;

    /**
     * Domyślny konstruktor.
     */
    public Order() {
        logger.debug("Utworzono nowe zamówienie (domyślny konstruktor).");
    }

    /**
     * Konstruktor z parametrami.
     *
     * @param product  Produkt zamówienia
     * @param employee Pracownik realizujący zamówienie
     * @param quantity Ilość zamówionego produktu
     * @param price    Cena zamówienia
     * @param date     Data zamówienia
     */
    public Order(Product product, Employee employee, int quantity, BigDecimal price, Date date) {
        try {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Ilość musi być większa od zera.");
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Cena musi być większa od zera.");
            }

            this.product = product;
            this.employee = employee;
            this.quantity = quantity;
            this.price = price;
            this.date = date;

            logger.info("Utworzono zamówienie dla produktu: {}, ilość: {}, cena: {}",
                    product != null ? product.getName() : "null", quantity, price);
        } catch (IllegalArgumentException e) {
            logger.error("Błąd podczas tworzenia zamówienia: {}", e.getMessage(), e);
            throw e; // Rzuć wyjątek ponownie, jeśli to krytyczne
        }
    }

    // === Gettery i settery ===

    public int getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        logger.debug("Ustawiono produkt: {}", product != null ? product.getName() : "null");
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        logger.debug("Ustawiono pracownika: {}", employee != null ? employee.getLogin() : "null");
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            logger.warn("Próba ustawienia nieprawidłowej ilości: {}", quantity);
            throw new IllegalArgumentException("Ilość musi być większa od zera.");
        }
        this.quantity = quantity;
        logger.info("Zaktualizowano ilość produktu na: {}", quantity);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Próba ustawienia nieprawidłowej ceny: {}", price);
            throw new IllegalArgumentException("Cena musi być większa od zera.");
        }
        this.price = price;
        logger.info("Zaktualizowano cenę zamówienia na: {}", price);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        logger.debug("Zaktualizowano datę zamówienia na: {}", date);
    }

    /**
     * Zwraca reprezentację tekstową zamówienia.
     *
     * @return Reprezentacja tekstowa zamówienia
     */
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") +
                ", employee=" + (employee != null ? employee.getLogin() : "null") +
                ", quantity=" + quantity +
                ", price=" + price +
                ", date=" + date +
                '}';
    }
}