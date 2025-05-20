/*
 * Classname: Transaction
 * Version information: 1.0
 * Date: 2025-05-20
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Transakcje")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika")
    private Employee pracownik;

    @Temporal(TemporalType.DATE)
    private Date data;

    // Zmieniamy relację z ManyToMany na OneToMany do tabeli pośredniczącej
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionProduct> transactionProducts = new ArrayList<>();

    // === Gettery i settery ===

    public int getId() {
        return id;
    }

    public Employee getPracownik() {
        return pracownik;
    }

    public void setPracownik(Employee pracownik) {
        this.pracownik = pracownik;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
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
    public List<Product> getProdukty() {
        List<Product> products = new ArrayList<>();
        for (TransactionProduct tp : transactionProducts) {
            products.add(tp.getProduct());
        }
        return products;
    }
}