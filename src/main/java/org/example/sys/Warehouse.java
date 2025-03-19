package org.example.sys;

import java.util.List;

public class Warehouse {
    private String name;
    private String category;
    List<Product> products;

    public Warehouse(List<Product> product){
        products = product;
    }
    
}
