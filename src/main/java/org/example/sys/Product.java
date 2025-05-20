/*
 * Classname: Product
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.*;

/**
 * Klasa reprezentująca produkt w systemie.
 * Zawiera informacje o nazwie, kategorii i cenie produktu.
 */
@Entity
@Table(name = "Produkty")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Nazwa", nullable = false, length = 100)
    private String name;

    @Column(name = "Kategoria", nullable = false, length = 100)
    private String category;

    @Column(name = "Cena", nullable = false)
    private double price;

    /**
     * Domyślny konstruktor.
     * Używany przez Hibernate do tworzenia instancji klasy.
     */
    public Product() {
    }

    /**
     * Konstruktor z parametrami.
     * Umożliwia ustawienie nazwy, kategorii i ceny produktu.
     *
     * @param name     Nazwa produktu
     * @param category Kategoria produktu
     * @param price    Cena produktu
     */
    public Product(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    // === Gettery i settery ===

    /**
     * Zwraca identyfikator produktu.
     *
     * @return Identyfikator produktu
     */
    public int getId() {
        return id;
    }

    /**
     * Ustawia identyfikator produktu.
     *
     * @param id Identyfikator produktu
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Zwraca nazwę produktu.
     *
     * @return Nazwa produktu
     */
    public String getName() {
        return name;
    }

    /**
     * Ustawia nazwę produktu.
     *
     * @param name Nazwa produktu
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Zwraca kategorię produktu.
     *
     * @return Kategoria produktu
     */
    public String getCategory() {
        return category;
    }

    /**
     * Ustawia kategorię produktu.
     *
     * @param category Kategoria produktu
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Zwraca cenę produktu.
     *
     * @return Cena produktu
     */
    public double getPrice() {
        return price;
    }

    /**
     * Ustawia cenę produktu.
     *
     * @param price Cena produktu
     */
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    /**
     * Zwraca reprezentację tekstową produktu.
     *
     * @return Reprezentacja tekstowa produktu
     */
    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', price=%.2f}",
                id, name, category, price);
    }
}
