/*
 * Classname: TransactionProduct
 * Version information: 1.1
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MapsId;

/**
 * Klasa reprezentująca powiązanie między transakcją a produktem w
 * systemie.
 * Mapowana na tabelę "Transakcje_Produkty" w bazie danych.
 * Przechowuje informacje o transakcji, produkcie oraz ilości produktu.
 * Implementuje relację typu wiele-do-wielu z dodatkowymi atrybutami
 * (ilość produktu w transakcji).
 */
@Entity
@Table(name = "Transakcje_Produkty")
@Access(AccessType.FIELD)
public class TransactionProduct {

    /**
     * Logger do rejestrowania zdarzeń związanych
     * z klasą TransactionProduct.
     */
    private static final Logger logger
            = LogManager.getLogger(TransactionProduct.class);

    /**
     * Złożony identyfikator encji, składający się z ID
     * transakcji i ID produktu.
     */
    @EmbeddedId
    private TransactionProductId id;

    /**
     * Transakcja powiązana z encją.
     * Relacja wiele-do-jednego, wiele powiązań może dotyczyć
     * jednej transakcji.
     */
    @ManyToOne
    @MapsId("transactionId")
    @JoinColumn(name = "Id_transakcji")
    private Transaction transaction;

    /**
     * Produkt powiązany z encją.
     * Relacja wiele-do-jednego, wiele powiązań może dotyczyć
     * jednego produktu.
     */
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "Id_produktu")
    private Product product;

    /**
     * Ilość produktu w transakcji.
     * Wartość musi być większa od zera.
     */
    @Column(name = "Ilosc", nullable = false)
    private int quantity;

    /**
     * Konstruktor domyślny wymagany przez JPA.
     * Inicjalizuje pusty obiekt złożonego identyfikatora.
     * Operacja jest logowana na poziomie DEBUG.
     */
    public TransactionProduct() {
        // ważne: zawsze inicjalizujemy id, aby MapsId mogło wypełnić pola
        this.id = new TransactionProductId();
        logger.debug("Utworzono nową instancję TransactionProduct" +
                " (domyślny konstruktor).");
    }

    /**
     * Konstruktor z parametrami.
     * Tworzy powiązanie między transakcją a produktem z podaną ilością.
     * Wywołuje konstruktor domyślny w celu inicjalizacji identyfikatora.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param transaction transakcja do powiązania
     * @param product produkt do powiązania
     * @param quantity ilość produktu w transakcji, musi być większa od zera
     */
    public TransactionProduct(
            Transaction transaction,
            Product product,
            int quantity
    ) {
        this();  // inicjalizuje id
        setTransaction(transaction);
        setProduct(product);
        setQuantity(quantity);
        logger.info("Utworzono powiązanie produktu ID:{}" +
                        " z transakcją ID:{} (ilość: {})",
                product.getId(), transaction.getId(), quantity);
    }

    /**
     * @return złożony identyfikator powiązania
     */
    public TransactionProductId getId() {
        return id;
    }

    /**
     * @param id nowy złożony identyfikator powiązania
     */
    public void setId(TransactionProductId id) {
        logger.debug("Zaktualizowano ID" +
                " powiązania transakcja-produkt na: {}", id);
        this.id = id;
    }

    /**
     * @return transakcja powiązana z encją
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Ustawia transakcję powiązaną z encją.
     * Aktualizuje również ID transakcji w złożonym identyfikatorze.
     * Operacja jest logowana na poziomie DEBUG lub WARN w przypadku błędu.
     *
     * @param transaction nowa transakcja dla powiązania, nie może być null
     */
    public void setTransaction(Transaction transaction) {
        if (transaction == null) {
            logger.warn("Próba ustawienia transakcji na wartość null.");
            return;
        }
        this.transaction = transaction;
        // synchronizujemy id
        this.id.setTransactionId(transaction.getId());
        logger.debug("Zaktualizowano powiązaną transakcję na ID: {}",
                transaction.getId());
    }

    /**
     * @return produkt powiązany z encją
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Ustawia produkt powiązany z encją.
     * Aktualizuje również ID produktu w złożonym identyfikatorze.
     * Operacja jest logowana na poziomie DEBUG lub WARN w przypadku błędu.
     *
     * @param product nowy produkt dla powiązania, nie może być null
     */
    public void setProduct(Product product) {
        if (product == null) {
            logger.warn("Próba ustawienia produktu na wartość null.");
            return;
        }
        this.product = product;
        // synchronizujemy id
        this.id.setProductId(product.getId());
        logger.debug("Zaktualizowano powiązany produkt na ID: {}",
                product.getId());
    }

    /**
     * @return ilość produktu w transakcji
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Ustawia ilość produktu w transakcji.
     * Wykonuje walidację - ilość musi być większa od zera.
     * W przypadku nieprawidłowej wartości rzuca wyjątek.
     * Operacja jest logowana na poziomie INFO lub WARN w przypadku błędu.
     *
     * @param quantity nowa ilość produktu, musi być większa od zera
     * @throws IllegalArgumentException gdy ilość nie jest większa od zera
     */
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            logger.warn("Nieprawidłowa ilość produktu: {}." +
                    " Ilość musi być większa niż zero.", quantity);
            throw new IllegalArgumentException("Ilość produktu musi" +
                    " być większa niż zero.");
        }
        logger.info("Zaktualizowano ilość produktu z {} na {}",
                this.quantity, quantity);
        this.quantity = quantity;
    }

    /**
     * Zwraca reprezentację tekstową powiązania transakcja-produkt.
     * Zawiera informacje o identyfikatorze, produkcie i ilości.
     *
     * @return tekstowa reprezentacja powiązania
     */
    @Override
    public String toString() {
        return "TransactionProduct{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                '}';
    }
}