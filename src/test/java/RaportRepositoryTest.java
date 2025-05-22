/*
 * Classname: TestRaportRepository
 * Version information: 1.0
 * Date: 2025-05-15
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.ReportRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.sys.Report;

import java.time.LocalDate;
import java.util.List;

/**
 * Klasa testująca działanie RaportRepository.
 */
public class RaportRepositoryTest {

    public static void main(String[] args) {
        ReportRepository raportRepo = new ReportRepository();
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
            Report r1 = new Report(
                    "Raport sprzedaży",
                    LocalDate.of(2025, 4, 1),
                    LocalDate.of(2025, 4, 30),
                    pracownik,
                    "raporty/sprzedaz_0425.pdf"
            );

            Report r2 = new Report(
                    "Raport pracowników",
                    LocalDate.of(2025, 5, 1),
                    LocalDate.of(2025, 5, 10),
                    pracownik,
                    "raporty/pracownicy_0525.pdf"
            );

            raportRepo.addReport(r1);
            raportRepo.addReport(r2);
            System.out.println(">>> Dodano raporty.");

            // === 3. Pobieranie wszystkich raportów ===
            System.out.println("\n>>> Lista wszystkich raportów:");
            wypiszRaporty(raportRepo.getAllReports());

            // === 4. Aktualizacja ===
            r1.setReportType("Raport sprzedaży — zmodyfikowany");
            r1.setFilePath("raporty/zmieniony_sprzedaz.pdf");
            raportRepo.updateReport(r1);
            System.out.println(">>> Zaktualizowano raport r1.");

            // === 5. Pobranie po ID ===
            Report znaleziony = raportRepo.findReportById(r1.getId());
            System.out.println(">>> Raport po ID: " + znaleziony.getReportType() + " | " + znaleziony.getFilePath());

            // === 6. Usunięcie ===
            raportRepo.removeReport(r2.getId());
            System.out.println(">>> Usunięto raport r2.");

            // === 7. Lista po usunięciu ===
            System.out.println("\n>>> Raporty po usunięciu:");
            wypiszRaporty(raportRepo.getAllReports());

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
    private static void wypiszRaporty(List<Report> raporty) {
        if (raporty.isEmpty()) {
            System.out.println("(Brak raportów)");
        } else {
            for (Report r : raporty) {
                System.out.println("[" + r.getId() + "] "
                        + r.getReportType()
                        + " | " + r.getStartDate()
                        + " → " + r.getEndTime()
                        + " | plik: " + r.getFilePath());
            }
        }
        System.out.println("-----------------------------");
    }
}
