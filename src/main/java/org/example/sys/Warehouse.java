package org.example.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Warehouse {

    private List<Product> products;

    public Warehouse(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
    }

    public Product getProduct(String name) {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    public void addProduct(Product product) {
        if (product != null && !products.contains(product)) {
            products.add(product);
        }
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public void updateProduct(Product product, String name, String category, double price, int quantity) {
        if (product != null) {
            product.setName(name);
            product.setCategory(category);
            product.setPrice(price);
            product.setQuantity(quantity);
        }
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public int getQuantity(Product product) {
        return product != null ? product.getQuantity() : 0;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> filtered = new ArrayList<>();
        for (Product product : products) {
            if (Objects.equals(product.getCategory(), category)) {
                filtered.add(product);
            }
        }
        return filtered;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Warehouse inventory:\n");
        for (Product product : products) {
            sb.append(product.toString()).append("\n");
        }
        return sb.toString();
    }
}
