package org.example.sys;

public class Product {
    private String name;
    private String category;
    private float price;
    private int quantity;

    public Product(String name, String category, float price, int quantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }
    public Product(String name, String category, float price) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = 0;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
