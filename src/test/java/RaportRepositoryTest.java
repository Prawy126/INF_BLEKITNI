/*
 * Classname: TestRaportRepository
 * Version information: 1.0
 * Date: 2025-05-15
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.RaportRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.sys.Raport;

import java.time.LocalDate;
import java.util.List;

/**
 * Klasa testująca działanie RaportRepository.
 */
public class RaportRepositoryTest {

    public static void main(String[] args) {
        RaportRepository raportRepo = new RaportRepository();
        UserRepository userRepo = new UserRepository(); // aby pobrać pracownika

        try {
            // === 1. Przykładowy pracownik do raportu ===
            List<Employee> pracownicy = userRepo.getAllEmployess();
            if (pracownicy.isEmpty()) {
                System.out.println("Brak pracowników w bazie. Dodaj przynajmniej jednego przed testami.");
                return;
            }
            Employee pracownik = pracownicy.get(0);

            // === 2. Dodawanie raportów ===
            Raport r1 = new Raport(
                    "Raport sprzedaży",
                    LocalDate.of(2025, 4, 1),
                    LocalDate.of(2025, 4, 30),
                    pracownik,
                    "raporty/sprzedaz_0425.pdf"
            );

            Raport r2 = new Raport(
                    "Raport pracowników",
                    LocalDate.of(2025, 5, 1),
                    LocalDate.of(2025, 5, 10),
                    pracownik,
                    "raporty/pracownicy_0525.pdf"
            );

            raportRepo.dodajRaport(r1);
            raportRepo.dodajRaport(r2);
            System.out.println(">>> Dodano raporty.");

            // === 3. Pobieranie wszystkich raportów ===
            System.out.println("\n>>> Lista wszystkich raportów:");
            wypiszRaporty(raportRepo.pobierzWszystkieRaporty());

            // === 4. Aktualizacja ===
            r1.setTypRaportu("Raport sprzedaży — zmodyfikowany");
            r1.setSciezkaPliku("raporty/zmieniony_sprzedaz.pdf");
            raportRepo.aktualizujRaport(r1);
            System.out.println(">>> Zaktualizowano raport r1.");

            // === 5. Pobranie po ID ===
            Raport znaleziony = raportRepo.znajdzRaportPoId(r1.getId());
            System.out.println(">>> Raport po ID: " + znaleziony.getTypRaportu() + " | " + znaleziony.getSciezkaPliku());

            // === 6. Usunięcie ===
            raportRepo.usunRaport(r2.getId());
            System.out.println(">>> Usunięto raport r2.");

            // === 7. Lista po usunięciu ===
            System.out.println("\n>>> Raporty po usunięciu:");
            wypiszRaporty(raportRepo.pobierzWszystkieRaporty());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            raportRepo.close();
            userRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca raporty.
     *
     * @param raporty lista raportów
     */
    private static void wypiszRaporty(List<Raport> raporty) {
        if (raporty.isEmpty()) {
            System.out.println("(Brak raportów)");
        } else {
            for (Raport r : raporty) {
                System.out.println("[" + r.getId() + "] "
                        + r.getTypRaportu()
                        + " | " + r.getDataPoczatku()
                        + " → " + r.getDataZakonczenia()
                        + " | plik: " + r.getSciezkaPliku());
            }
        }
        System.out.println("-----------------------------");
    }
}
