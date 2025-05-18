package org.example.database;

import org.example.sys.Product;
import org.example.sys.Warehouse;

import java.util.List;

/**
 * Klasa testująca działanie WarehouseRepository.
 */
public class TestWarehouseRepository {

    public static void main(String[] args) {
        WarehouseRepository repo = new WarehouseRepository();

        try {
            // === 1. Dodanie nowego stanu magazynowego ===
            Product produkt = new Product("Jogurt truskawkowy", "Nabiał", 3.49);
            Warehouse stan = new Warehouse(produkt, 120);
            repo.dodajStanMagazynowy(stan);
            System.out.println(">>> Dodano stan magazynowy!");

            // === 2. Lista wszystkich stanów ===
            System.out.println("\n>>> Lista stanów magazynowych:");
            wypiszStany(repo.pobierzWszystkieStany());

            // === 3. Aktualizacja ===
            produkt.setPrice(3.99); // aktualizacja ceny produktu
            stan.setIlosc(100);     // aktualizacja ilości w magazynie
            repo.aktualizujStan(stan);
            System.out.println(">>> Zaktualizowano stan magazynowy.");

            // === 4. Pobranie po ID ===
            Warehouse znaleziony = repo.znajdzStanPoIdProduktu(produkt.getId());
            System.out.println(">>> Stan po ID produktu: " + znaleziony);

            // === 5. Usunięcie ===
            repo.usunStan(produkt.getId());
            System.out.println(">>> Usunięto stan magazynowy.");

            // === 6. Lista po usunięciu ===
            System.out.println("\n>>> Lista po usunięciu:");
            wypiszStany(repo.pobierzWszystkieStany());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            repo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca stany magazynowe.
     *
     * @param stany lista stanów magazynowych
     */
    private static void wypiszStany(List<Warehouse> stany) {
        if (stany.isEmpty()) {
            System.out.println("(Brak danych o stanach magazynowych)");
        } else {
            for (Warehouse p : stany) {
                System.out.printf("ID: %-3d Nazwa: %-20s Cena: %-6.2f Ilość: %-4d%n",
                        p.getProdukt().getId(),
                        p.getProdukt().getName(),
                        p.getProdukt().getPrice(),
                        p.getIlosc()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
