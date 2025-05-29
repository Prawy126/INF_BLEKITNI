/*
 * Classname: Product
 * Version information: 1.3
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import java.math.BigDecimal;

@Entity
@Table(name = "Produkty")
@Access(AccessType.FIELD)
public class Product {

    // Inicjalizacja logera
    private static final Logger logger = LogManager.getLogger(Product.class);

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

    public Product() {
        logger.debug("Utworzono nową instancję Product (domyślny konstruktor).");
    }

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
        setName(name);
        setCategory(category);
        setPrice(price);

        logger.info("Utworzono produkt: {}, kategoria: {}, cena: {}", name, category, price);
    }

    // === Gettery i settery ===

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        logger.debug("Ustawiono ID produktu: {}", id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            logger.warn("Próba ustawienia pustego lub nieprawidłowego imienia.");
        }
        this.name = name;
        logger.debug("Zaktualizowano nazwę produktu na: {}", name);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.isBlank()) {
            logger.warn("Próba ustawienia pustej lub nieprawidłowej kategorii.");
        }
        this.category = category;
        logger.debug("Zaktualizowano kategorię produktu na: {}", category);
    }

    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Ustawia cenę tylko gdy nie jest null i >= 0
     */
    public void setPrice(BigDecimal price) {
        if (price == null) {
            logger.warn("Próba ustawienia ceny na wartość null.");
            return;
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("Nieprawidłowa cena: {}. Cena musi być większa lub równa zero.", price);
            return;
        }

        this.price = price;
        logger.debug("Zaktualizowano cenę produktu na: {}", price);
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', price=%s}",
                id, name, category, price);
    }
}