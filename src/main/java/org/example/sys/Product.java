package org.example.sys;

import jakarta.persistence.*;

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

    @Column(name = "Cena", nullable = false, precision = 10, scale = 2)
    private double price;

    public Product() {
    }

    public Product(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', price=%.2f}",
                id, name, category, price);
    }
}
