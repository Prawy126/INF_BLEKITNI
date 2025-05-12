/*
 * Classname: TestProductRepository
 * Version information: 1.0
 * Date: 2025-05-12
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.example.sys.Product;

import java.util.List;

/**
 * Klasa testująca działanie ProductRepository.
 */
public class TestProductRepository {

    public static void main(String[] args) {
        ProductRepository productRepo = new ProductRepository();

        try {
            // === 1. Dodanie nowego produktu ===
            Product produkt = new Product("Jogurt truskawkowy", "Nabiał", 3.49, 120);
            productRepo.dodajProdukt(produkt);
            System.out.println(">>> Dodano produkt!");

            // === 2. Lista wszystkich produktów ===
            System.out.println("\n>>> Lista produktów:");
            wypiszProdukty(productRepo.pobierzWszystkieProdukty());

            // === 3. Aktualizacja produktu ===
            produkt.setPrice(3.99);
            produkt.setQuantity(100);
            productRepo.aktualizujProdukt(produkt);
            System.out.println(">>> Zaktualizowano produkt.");

            // === 4. Pobranie po ID ===
            Product znaleziony = productRepo.znajdzProduktPoId(produkt.getId());
            System.out.println(">>> Produkt po ID: " + znaleziony);

            // === 5. Usunięcie produktu ===
            productRepo.usunProdukt(znaleziony.getId());
            System.out.println(">>> Usunięto produkt.");

            // === 6. Lista po usunięciu ===
            System.out.println("\n>>> Lista produktów po usunięciu:");
            wypiszProdukty(productRepo.pobierzWszystkieProdukty());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            productRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca produkty.
     *
     * @param produkty lista produktów
     */
    private static void wypiszProdukty(List<Product> produkty) {
        if (produkty.isEmpty()) {
            System.out.println("(Brak produktów)");
        } else {
            for (Product p : produkty) {
                System.out.printf("ID: %-3d Nazwa: %-20s Kategoria: %-15s Cena: %-6.2f Ilość: %-4d%n",
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getPrice(),
                        p.getQuantity()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
