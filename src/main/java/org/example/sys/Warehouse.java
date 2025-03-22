package org.example.sys;

import java.util.List;

public class Warehouse {
    private String name;
    private String category;
    private List<Product> products;

    public Warehouse(List<Product> product){
        products = product;
    }
    public Product getProduct(String name){
        for(Product product: products){
            if(product.getName().equals(name)){
                return product;
            }
        }
        return null;
    }

    public void addProduct(Product product){
        products.add(product);
    }
    public void removeProduct(Product product){
        products.remove(product);
    }
    public void updateProduct(Product product, String name, String category, float price, int quantity){
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setQuantity(quantity);
    }

    public List<Product> getProducts() {
        return products;
    }

    public int getQuantity(Product product){
        return product.getQuantity();
    }
    
}
