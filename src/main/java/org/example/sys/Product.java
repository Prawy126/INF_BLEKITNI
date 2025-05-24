/*
 * Classname: Product
 * Version information: 1.1
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Produkty")
@Access(AccessType.FIELD)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Nazwa", nullable = false, length = 100)
    private String name;

    @Column(name = "Kategoria", nullable = false, length = 100)
    private String category;

    @Column(name = "Cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public Product() { }

    /**
     * Stary konstruktor – przyjmował double, teraz tylko opakowuje w BigDecimal.
     */
    public Product(String name, String category, double price) {
        this(name, category, BigDecimal.valueOf(price));
    }

    /**
     * Nowy konstruktor – możesz mu podać od razu BigDecimal.
     */
    public Product(String name, String category, BigDecimal price) {
        this.name     = name;
        this.category = category;
        setPrice(price);
    }

    // === Gettery i settery ===

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Ustawia cenę tylko gdy nie jest null i >= 0
     */
    public void setPrice(BigDecimal price) {
        if (price != null && price.compareTo(BigDecimal.ZERO) >= 0) {
            this.price = price;
        }
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', price=%s}",
                id, name, category, price);
    }
}
