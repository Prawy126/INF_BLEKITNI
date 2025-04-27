package org.example.database;

import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.wyjatki.SalaryException;

import java.math.BigDecimal;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        UserRepository userRepo = new UserRepository();
        AddressRepository addressRepo = new AddressRepository();

        try {
            /* === 1. Tworzenie i dodanie adresu === */
            Address adres = new Address();
            adres.setMiejscowosc("Testowo");
            adres.setNumerDomu("10A");
            adres.setNumerMieszkania("5");
            adres.setKodPocztowy("00-000");
            adres.setMiasto("Testowo");

            addressRepo.dodajAdres(adres);
            System.out.println(">>> Dodano adres: " + adres.getMiasto());

            /* === 2. Dodanie nowego pracownika === */
            Employee nowyKasjer = new Employee(
                    "Michał",
                    "Brzozowski",
                    26,
                    adres,
                    "mbrzo",
                    "tajnehaslo",
                    "Kasjer",
                    new BigDecimal("3200.00")
            );

            userRepo.dodajPracownika(nowyKasjer);
            System.out.println(">>> Dodano pracownika: " + nowyKasjer.getLogin());

            /* === 3. Pobieranie kasjerów === */
            System.out.println("\n>>> Lista kasjerów po dodaniu:");
            wypiszPracownikow(userRepo.pobierzKasjerow());

            /* === 4. Wyszukiwanie po loginie === */
            Employee znalezionyPoLoginie = userRepo.znajdzPoLoginie("mbrzo");
            System.out.println("\n>>> Znaleziony po loginie: " + znalezionyPoLoginie.getImie() + " " + znalezionyPoLoginie.getNazwisko());

            /* === 5. Wyszukiwanie po loginie i haśle === */
            Employee znalezionyPoLoginieIHasle = userRepo.znajdzPoLoginieIHasle("mbrzo", "tajnehaslo");
            if (znalezionyPoLoginieIHasle != null) {
                System.out.println(">>> Znaleziony użytkownik (login + hasło): " + znalezionyPoLoginieIHasle.getImie() + " " + znalezionyPoLoginieIHasle.getNazwisko());
            } else {
                System.out.println(">>> Nie znaleziono użytkownika przy logowaniu!");
            }

            /* === 6. Aktualizacja pracownika === */
            znalezionyPoLoginie.setNazwisko("Brzoza");
            znalezionyPoLoginie.setZarobki(new BigDecimal("3400.00"));
            userRepo.aktualizujPracownika(znalezionyPoLoginie);
            System.out.println(">>> Zaktualizowano nazwisko i pensję.");

            Employee poAktualizacji = userRepo.znajdzPoLoginie("mbrzo");
            System.out.println(">>> Po aktualizacji: " + poAktualizacji.getImie() + " " + poAktualizacji.getNazwisko()
                    + ", zarobki: " + poAktualizacji.getZarobki());

            /* === 7. Pobieranie wszystkich pracowników === */
            System.out.println("\n>>> Lista wszystkich pracowników:");
            wypiszPracownikow(userRepo.pobierzWszystkichPracownikow());

            /* === 8. Usuwanie pracownika === */
            userRepo.usunPracownika(poAktualizacji);
            System.out.println(">>> Usunięto pracownika o id = " + poAktualizacji.getId());

            /* === 9. Sprawdzenie kasjerów po usunięciu === */
            System.out.println("\n>>> Lista kasjerów po usunięciu:");
            wypiszPracownikow(userRepo.pobierzKasjerow());

        } catch (SalaryException e) {
            System.err.println("Błąd walidacji wynagrodzenia: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /* Zamknięcie repozytoriów */
            userRepo.close();
            addressRepo.close();
        }
    }

    /** Pomocnicza metoda wypisująca listę pracowników */
    private static void wypiszPracownikow(List<Employee> pracownicy) {
        if (pracownicy.isEmpty()) {
            System.out.println("(Brak wyników)");
            return;
        }
        for (Employee e : pracownicy) {
            System.out.printf("ID: %-3d Imię: %-10s Nazwisko: %-12s Login: %-8s Zarobki: %8.2f zł%n",
                    e.getId(), e.getImie(), e.getNazwisko(), e.getLogin(), e.getZarobki());
        }
        System.out.println("-----------------------------");
    }
}
