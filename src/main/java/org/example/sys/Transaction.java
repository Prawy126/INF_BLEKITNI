/*
 * Classname: Transaction
 * Version information: 1.2
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Klasa reprezentująca transakcję w systemie.
 * Zawiera informacje o pracowniku, dacie oraz produkt
 */
@Entity
@Table(name = "Transakcje")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika")
    private Employee employee;

    @Temporal(TemporalType.DATE)
    @Column(name = "Data")
    private Date date;

    // Zmieniamy relację z ManyToMany na OneToMany do tabeli pośredniczącej
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionProduct> transactionProducts = new ArrayList<>();

    // === Gettery i settery ===

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<TransactionProduct> getTransactionProducts() {
        return transactionProducts;
    }

    public void setTransactionProducts(List<TransactionProduct> transactionProducts) {
        this.transactionProducts = transactionProducts;
    }

    // Metody pomocnicze do zarządzania relacją
    public void addProduct(Product product, int quantity) {
        // Upewnij się, że produkt jest zarządzany przez sesję
        TransactionProduct transactionProduct = new TransactionProduct(this, product, quantity);
        transactionProducts.add(transactionProduct);
    }

    public void removeProduct(Product product) {
        transactionProducts.removeIf(tp -> tp.getProduct().equals(product));
    }

    // Metoda pomocnicza do zachowania kompatybilności z istniejącym kodem
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        for (TransactionProduct tp : transactionProducts) {
            products.add(tp.getProduct());
        }
        return products;
    }

}