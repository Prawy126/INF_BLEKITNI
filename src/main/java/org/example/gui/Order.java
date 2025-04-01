package org.example.gui;

public class Order {
    private int id;
    private String productName;
    private int quantity;
    private String supplier;
    private String orderDate;
    private String status;

    public Order(int id, String productName, int quantity, String supplier, String orderDate, String status) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.supplier = supplier;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Gettery i settery
    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }
}