/*
 * Classname: Product
 * Version information: 1.3
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import java.math.BigDecimal;

/**
 * Klasa reprezentująca produkt w systemie.
 * Mapowana na tabelę "Produkty" w bazie danych.
 * Zawiera informacje o nazwie, kategorii i cenie produktu.
 * Implementuje logikę walidacji danych produktu.
 */
@Entity
@Table(name = "Produkty")
@Access(AccessType.FIELD)
public class Product {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą Product.
     */
    private static final Logger logger = LogManager.getLogger(Product.class);

    /**
     * Unikalny identyfikator produktu generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    /**
     * Nazwa produktu.
     * Pole nie może być puste (nullable=false).
     * Maksymalna długość: 100 znaków.
     */
    @Column(name = "Nazwa", nullable = false, length = 100)
    private String name;

    /**
     * Kategoria produktu.
     * Pole nie może być puste (nullable=false).
     * Maksymalna długość: 100 znaków.
     */
    @Column(name = "Kategoria", nullable = false, length = 100)
    private String category;

    /**
     * Cena produktu.
     * Pole nie może być puste (nullable=false).
     * Zapisywana z precyzją do dwóch miejsc po przecinku.
     * Wartość musi być większa lub równa zero.
     */
    @Column(name = "Cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Domyślny konstruktor.
     * Tworzy pusty obiekt produktu.
     * Operacja jest logowana na poziomie DEBUG.
     */
    public Product() {
        logger.debug("Utworzono nową instancję " +
                " (domyślny konstruktor).");
    }

    /**
     * Konstruktor przyjmujący cenę jako wartość typu double.
     * Zachowany dla wstecznej kompatybilności.
     * Przekazuje parametry do głównego konstruktora,
     * konwertując cenę na BigDecimal.
     *
     * @param name     nazwa produktu
     * @param category kategoria produktu
     * @param price    cena produktu jako double
     */
    public Product(String name, String category, double price) {
        this(name, category, BigDecimal.valueOf(price));
    }

    /**
     * Główny konstruktor parametrowy.
     * Tworzy produkt na podstawie przekazanych parametrów.
     * Wywołuje metody walidujące dla wszystkich pól.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param name     nazwa produktu, nie może być pusta
     * @param category kategoria produktu, nie może być pusta
     * @param price    cena produktu jako BigDecimal, musi być
     *                większa lub równa zero
     */
    public Product(String name, String category, BigDecimal price) {
        setName(name);
        setCategory(category);
        setPrice(price);

        logger.info("Utworzono produkt: {}," +
                " kategoria: {}, cena: {}", name, category, price);
    }

    /**
     * Pobiera identyfikator produktu.
     *
     * @return unikalny identyfikator produktu
     */
    public int getId() {
        return id;
    }

    /**
     * Ustawia identyfikator produktu.
     * Operacja jest logowana na poziomie DEBUG.
     *
     * @param id nowy identyfikator produktu
     */
    public void setId(int id) {
        this.id = id;
        logger.debug("Ustawiono ID produktu: {}", id);
    }

    /**
     * Pobiera nazwę produktu.
     *
     * @return nazwa produktu
     */
    public String getName() {
        return name;
    }

    /**
     * Ustawia nazwę produktu.
     * Weryfikuje czy nazwa nie jest pusta.
     *
     * @param name nowa nazwa produktu
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            logger.warn("Próba ustawienia pustego lub" +
                    " nieprawidłowego imienia.");
        }
        this.name = name;
        logger.debug("Zaktualizowano nazwę produktu na: {}", name);
    }

    /**
     * Pobiera kategorię produktu.
     *
     * @return kategoria produktu
     */
    public String getCategory() {
        return category;
    }

    /**
     * Ustawia kategorię produktu.
     * Weryfikuje czy kategoria nie jest pusta.
     *
     * @param category nowa kategoria produktu
     */
    public void setCategory(String category) {
        if (category == null || category.isBlank()) {
            logger.warn("Próba ustawienia pustej lub" +
                    " nieprawidłowej kategorii.");
        }
        this.category = category;
        logger.debug("Zaktualizowano kategorię" +
                " produktu na: {}", category);
    }

    /**
     * Pobiera cenę produktu.
     *
     * @return cena produktu jako BigDecimal
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Ustawia cenę produktu.
     *
     * @param price nowa cena produktu, musi być większa lub równa zero
     *             i nie może być null
     */
    public void setPrice(BigDecimal price) {
        if (price == null) {
            logger.warn("Próba ustawienia ceny na wartość null.");
            return;
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("Nieprawidłowa cena: {}." +
                    " Cena musi być większa lub równa zero.", price);
            return;
        }

        this.price = price;
        logger.debug("Zaktualizowano cenę produktu na: {}", price);
    }

    /**
     * Zwraca reprezentację tekstową produktu.
     * Zawiera informacje o identyfikatorze, nazwie, kategorii i cenie produktu.
     *
     * @return tekstowa reprezentacja produktu
     */
    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s'," +
                        " category='%s', price=%s}",
                id, name, category, price);
    }
}