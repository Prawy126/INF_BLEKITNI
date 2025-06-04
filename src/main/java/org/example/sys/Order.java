/*
 * Classname: Order
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Klasa reprezentująca zamówienie w systemie.
 * Mapowana na tabelę "Zamowienia" w bazie danych.
 * Zawiera informacje o produkcie, pracowniku, ilości, cenie i dacie zamówienia.
 * Implementuje logikę biznesową związaną z zarządzaniem zamówieniami,
 * w tym walidację ceny i ilości.
 */
@Entity
@Table(name = "Zamowienia")
@Access(AccessType.FIELD)
public class Order {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą Order.
     */
    private static final Logger logger = LogManager.getLogger(Order.class);

    /**
     * Unikalny identyfikator zamówienia generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    /**
     * Produkt, który jest przedmiotem zamówienia.
     * Relacja wiele-do-jednego, gdzie wiele zamówień może dotyczyć jednego produktu.
     */
    @ManyToOne
    @JoinColumn(name = "Id_produktu", nullable = false)
    private Product product;

    /**
     * Pracownik realizujący zamówienie.
     * Relacja wiele-do-jednego, gdzie wiele zamówień może być przypisanych do jednego pracownika.
     */
    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee employee;

    /**
     * Ilość zamawianego produktu.
     * Wartość musi być większa od zera.
     */
    @Column(name = "Ilosc", nullable = false)
    private int quantity;

    /**
     * Cena zamówienia.
     * Wartość musi być większa od zera.
     * Zapisywana z precyzją do dwóch miejsc po przecinku.
     */
    @Column(name = "Cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Data złożenia zamówienia.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "Data", nullable = false)
    private Date date;

    /**
     * Domyślny konstruktor.
     * Tworzy pusty obiekt zamówienia.
     * Operacja jest logowana na poziomie DEBUG.
     */
    public Order() {
        logger.debug("Utworzono nowe zamówienie (domyślny konstruktor).");
    }

    /**
     * Konstruktor z parametrami.
     * Tworzy zamówienie na podstawie przekazanych parametrów.
     * Wykonuje walidację ilości i ceny - obie muszą być większe od zera.
     * W przypadku błędnych danych rzuca wyjątek IllegalArgumentException.
     * Operacja jest logowana na poziomie INFO lub ERROR w przypadku błędu.
     *
     * @param product  Produkt zamówienia
     * @param employee Pracownik realizujący zamówienie
     * @param quantity Ilość zamówionego produktu, musi być większa od zera
     * @param price    Cena zamówienia, musi być większa od zera
     * @param date     Data zamówienia
     * @throws IllegalArgumentException gdy ilość lub cena nie są większe od zera
     */
    public Order(Product product,
                 Employee employee,
                 int quantity,
                 BigDecimal price,
                 Date date)
    {
        try {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Ilość musi " +
                        "być większa od zera.");
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Cena musi " +
                        "być większa od zera.");
            }

            this.product = product;
            this.employee = employee;
            this.quantity = quantity;
            this.price = price;
            this.date = date;

            logger.info("Utworzono zamówienie dla produktu: " +
                            "{}, ilość: {}, cena: {}",
                    product != null
                            ? product.getName() : "null", quantity, price);
        } catch (IllegalArgumentException e) {
            logger.error("Błąd podczas tworzenia zamówienia: {}",
                    e.getMessage(), e);
            throw e; // Rzuć wyjątek ponownie, jeśli to krytyczne
        }
    }

    /**
     * Pobiera identyfikator zamówienia.
     *
     * @return unikalny identyfikator zamówienia
     */
    public int getId() {
        return id;
    }

    /**
     * Pobiera produkt zamówienia.
     *
     * @return obiekt produktu zamówienia
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Ustawia produkt zamówienia.
     * Operacja jest logowana na poziomie DEBUG.
     *
     * @param product nowy produkt zamówienia
     */
    public void setProduct(Product product) {
        this.product = product;
        logger.debug("Ustawiono produkt: {}",
                product != null ? product.getName() : "null");
    }

    /**
     * Pobiera pracownika realizującego zamówienie.
     *
     * @return pracownik przypisany do zamówienia
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Ustawia pracownika realizującego zamówienie.
     * Operacja jest logowana na poziomie DEBUG.
     *
     * @param employee nowy pracownik realizujący zamówienie
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
        logger.debug("Ustawiono pracownika: {}",
                employee != null ? employee.getLogin() : "null");
    }

    /**
     * Pobiera ilość zamawianego produktu.
     *
     * @return ilość produktu
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Ustawia ilość zamawianego produktu.
     * Wykonuje walidację - ilość musi być większa od zera.
     * W przypadku nieprawidłowej wartości rzuca wyjątek IllegalArgumentException.
     * Operacja jest logowana na poziomie INFO lub WARN w przypadku błędu.
     *
     * @param quantity nowa ilość produktu, musi być większa od zera
     * @throws IllegalArgumentException gdy ilość nie jest większa od zera
     */
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            logger.warn("Próba ustawienia nieprawidłowej ilości: {}",
                    quantity);
            throw new IllegalArgumentException("Ilość musi być" +
                    " większa od zera.");
        }
        this.quantity = quantity;
        logger.info("Zaktualizowano ilość produktu na: {}", quantity);
    }

    /**
     * Pobiera cenę zamówienia.
     *
     * @return cena zamówienia
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Ustawia cenę zamówienia.
     * Wykonuje walidację - cena musi być większa od zera.
     * W przypadku nieprawidłowej wartości rzuca wyjątek IllegalArgumentException.
     * Operacja jest logowana na poziomie INFO lub WARN w przypadku błędu.
     *
     * @param price nowa cena zamówienia, musi być większa od zera
     * @throws IllegalArgumentException gdy cena nie jest większa od zera lub jest null
     */
    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Próba ustawienia nieprawidłowej ceny: {}",
                    price);
            throw new IllegalArgumentException("Cena musi być" +
                    " większa od zera.");
        }
        this.price = price;
        logger.info("Zaktualizowano cenę zamówienia na: {}", price);
    }

    /**
     * Pobiera datę zamówienia.
     *
     * @return data złożenia zamówienia
     */
    public Date getDate() {
        return date;
    }

    /**
     * Ustawia datę zamówienia.
     * Operacja jest logowana na poziomie DEBUG.
     *
     * @param date nowa data zamówienia
     */
    public void setDate(Date date) {
        this.date = date;
        logger.debug("Zaktualizowano datę zamówienia na: {}", date);
    }

    /**
     * Zwraca reprezentację tekstową zamówienia.
     * Zawiera informacje o identyfikatorze, produkcie, pracowniku,
     * ilości, cenie oraz dacie zamówienia.
     *
     * @return tekstowa reprezentacja zamówienia
     */
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", product=" + (product != null
                ? product.getName() : "null") +
                ", employee=" + (employee != null
                ? employee.getLogin() : "null") +
                ", quantity=" + quantity +
                ", price=" + price +
                ", date=" + date +
                '}';
    }
}