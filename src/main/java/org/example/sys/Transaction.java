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
 * Mapowana na tabelę "Transakcje" w bazie danych.
 * Zawiera informacje o pracowniku, dacie oraz produktach.
 * Obsługuje relację z produktami poprzez tabelę pośredniczącą TransactionProduct.
 */
@Entity
@Table(name = "Transakcje")
public class Transaction {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą Transaction.
     */
    private static final Logger logger
            = LogManager.getLogger(Transaction.class);

    /**
     * Unikalny identyfikator transakcji generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Pracownik powiązany z transakcją.
     * Relacja wiele-do-jednego, gdzie wiele transakcji może być przypisanych
     * do jednego pracownika.
     */
    @ManyToOne
    @JoinColumn(name = "Id_pracownika")
    private Employee employee;

    /**
     * Data wykonania transakcji.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "Data")
    private Date date;

    /**
     * Lista powiązań między transakcją a produktami.
     * Relacja jeden-do-wielu z tabelą pośredniczącą.
     * Kaskadowe operacje zapewniają, że usunięcie transakcji
     * powoduje usunięcie przypisanych produktów.
     */
    @OneToMany(mappedBy = "transaction",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionProduct> transactionProducts = new ArrayList<>();

    /**
     * @return identyfikator transakcji
     */
    public int getId() {
        return id;
    }

    /**
     * @param id nowy identyfikator transakcji
     */
    public void setId(int id) {
        this.id = id;
        logger.debug("Zaktualizowano ID transakcji na: {}", id);
    }

    /**
     * @return pracownik powiązany z transakcją
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * @param employee nowy pracownik dla transakcji
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
        logger.info("Przypisano pracownika {} do transakcji ID: {}",
                employee.getLogin(), this.id);
    }

    /**
     * @return data transakcji
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date nowa data transakcji
     */
    public void setDate(Date date) {
        this.date = date;
        logger.debug("Zaktualizowano datę transakcji na: {}", date);
    }

    /**
     * @return lista powiązań transakcja-produkt
     */
    public List<TransactionProduct> getTransactionProducts() {
        return transactionProducts;
    }

    /**
     * @param transactionProducts nowa lista powiązań transakcja-produkt
     */
    public void setTransactionProducts(
            List<TransactionProduct> transactionProducts
    ) {
        this.transactionProducts = transactionProducts;
        logger.debug("Zaktualizowano listę produktów" +
                " dla transakcji ID: {}", this.id);
    }

    /**
     * Dodaje produkt do transakcji z określoną ilością.
     * Tworzy nowy obiekt TransactionProduct reprezentujący powiązanie
     * między transakcją a produktem.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param product  produkt do dodania
     * @param quantity ilość produktu
     */
    public void addProduct(Product product, int quantity) {
        TransactionProduct transactionProduct =
                new TransactionProduct(this, product, quantity);
        transactionProducts.add(transactionProduct);
        logger.info("Dodano produkt {} (x{}) do transakcji ID: {}",
                product.getName(), quantity, this.id);
    }

    /**
     * Usuwa produkt z transakcji.
     * Wyszukuje i usuwa obiekt TransactionProduct powiązany z podanym produktem.
     * Operacja jest logowana na poziomie INFO lub WARN w przypadku niepowodzenia.
     *
     * @param product produkt do usunięcia
     */
    public void removeProduct(Product product) {
        boolean removed = transactionProducts.removeIf(tp
                -> tp.getProduct().equals(product));
        if (removed) {
            logger.info("Usunięto produkt {} z transakcji ID: {}",
                    product.getName(), this.id);
        } else {
            logger.warn("Nie znaleziono produktu {}" +
                            " do usunięcia z transakcji ID: {}",
                    product.getName(), this.id);
        }
    }

    /**
     * Metoda pomocnicza do zachowania kompatybilności z istniejącym kodem.
     * Zwraca listę produktów przypisanych do tej transakcji bez informacji o ilości.
     * Operacja jest logowana na poziomie DEBUG.
     *
     * @return lista produktów w transakcji
     */
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        for (TransactionProduct tp : transactionProducts) {
            products.add(tp.getProduct());
        }
        logger.debug("Pobrano listę produktów dla transakcji ID: {}",
                this.id);
        return products;
    }

    /**
     * Oblicza całkowitą wartość transakcji.
     * Sumuje wartości wszystkich produktów uwzględniając ich ilości.
     * Operacja jest logowana na poziomie INFO.
     *
     * @return suma wartości wszystkich produktów w transakcji
     */
    public double calculateTotalValue() {
        double total = transactionProducts.stream()
                .mapToDouble(tp
                        -> tp.getProduct().getPrice().doubleValue()
                        * tp.getQuantity())
                .sum();
        logger.info("Obliczono całkowitą wartość transakcji {}: {}",
                this.id, total);
        return total;
    }

    /**
     * Zwraca reprezentację tekstową transakcji.
     * Zawiera informacje o ID, pracowniku, dacie oraz liczbie produktów.
     *
     * @return tekstowa reprezentacja transakcji
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", employee=" + (employee != null
                ? employee.getLogin() : "null") +
                ", date=" + date +
                ", productsCount=" + transactionProducts.size() +
                '}';
    }
}