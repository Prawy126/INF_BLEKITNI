/*
 * Classname: Transaction
 * Version information: 1.3
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Klasa reprezentująca transakcję w systemie.
 * Zawiera informacje o pracowniku, dacie oraz produktach.
 */
@Entity
@Table(name = "Transakcje")
public class Transaction {

    // Inicjalizacja logera
    private static final Logger logger = LogManager.getLogger(Transaction.class);

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
        logger.debug("Zaktualizowano ID transakcji na: {}", id);
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        logger.info("Przypisano pracownika {} do transakcji ID: {}", employee.getLogin(), this.id);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        logger.debug("Zaktualizowano datę transakcji na: {}", date);
    }

    public List<TransactionProduct> getTransactionProducts() {
        return transactionProducts;
    }

    public void setTransactionProducts(List<TransactionProduct> transactionProducts) {
        this.transactionProducts = transactionProducts;
        logger.debug("Zaktualizowano listę produktów dla transakcji ID: {}", this.id);
    }

    // Metody pomocnicze do zarządzania relacją

    /**
     * Dodaje produkt do transakcji z określoną ilością.
     *
     * @param product  Produkt do dodania
     * @param quantity Ilość produktu
     */
    public void addProduct(Product product, int quantity) {
        TransactionProduct transactionProduct = new TransactionProduct(this, product, quantity);
        transactionProducts.add(transactionProduct);
        logger.info("Dodano produkt {} (x{}) do transakcji ID: {}", product.getName(), quantity, this.id);
    }

    /**
     * Usuwa produkt z transakcji.
     *
     * @param product Produkt do usunięcia
     */
    public void removeProduct(Product product) {
        boolean removed = transactionProducts.removeIf(tp -> tp.getProduct().equals(product));
        if (removed) {
            logger.info("Usunięto produkt {} z transakcji ID: {}", product.getName(), this.id);
        } else {
            logger.warn("Nie znaleziono produktu {} do usunięcia z transakcji ID: {}", product.getName(), this.id);
        }
    }

    /**
     * Metoda pomocnicza do zachowania kompatybilności z istniejącym kodem.
     * Zwraca listę produktów przypisanych do tej transakcji.
     *
     * @return Lista produktów
     */
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        for (TransactionProduct tp : transactionProducts) {
            products.add(tp.getProduct());
        }
        logger.debug("Pobrano listę produktów dla transakcji ID: {}", this.id);
        return products;
    }

    /**
     * Oblicza całkowitą wartość transakcji.
     *
     * @return Suma wartości wszystkich produktów
     */
    public double calculateTotalValue() {
        double total = transactionProducts.stream()
                .mapToDouble(tp -> tp.getProduct().getPrice().doubleValue() * tp.getQuantity())
                .sum();
        logger.info("Obliczono całkowitą wartość transakcji {}: {}", this.id, total);
        return total;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", employee=" + (employee != null ? employee.getLogin() : "null") +
                ", date=" + date +
                ", productsCount=" + transactionProducts.size() +
                '}';
    }
}