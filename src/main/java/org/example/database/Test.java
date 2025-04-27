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
            Address adres = new Address();
            adres.setMiejscowosc("Testowo");
            adres.setNumerDomu("10A");
            adres.setNumerMieszkania("5");
            adres.setKodPocztowy("00-000");
            adres.setMiasto("Testowo");

            addressRepo.dodajAdres(adres);
            System.out.println(">>> Dodano adres: " + adres.getMiasto());

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

            System.out.println("\n>>> Lista kasjerów po dodaniu:");
            wypiszKasjerow(userRepo.pobierzKasjerow());

            Employee znaleziony = userRepo.znajdzPoLoginie("mbrzo");
            System.out.println("\n>>> Znaleziony po loginie: " + znaleziony.getImie() + " " + znaleziony.getNazwisko());

            znaleziony.setNazwisko("Brzoza");
            znaleziony.setZarobki(new BigDecimal("3400.00"));
            userRepo.aktualizujPracownika(znaleziony);
            System.out.println(">>> Zaktualizowano nazwisko i pensję");

            Employee poAktualizacji = userRepo.znajdzPoLoginie("mbrzo");
            System.out.println(">>> Po aktualizacji: " + poAktualizacji.getImie() + " " + poAktualizacji.getNazwisko()
                    + ", zarobki: " + poAktualizacji.getZarobki());

            userRepo.usunPracownika((long) poAktualizacji.getId());
            System.out.println(">>> Usunięto pracownika o id = " + poAktualizacji.getId());

            System.out.println("\n>>> Lista kasjerów po usunięciu:");
            wypiszKasjerow(userRepo.pobierzKasjerow());

        } catch (SalaryException e) {
            System.err.println("Błąd walidacji wynagrodzenia: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userRepo.close();
            addressRepo.close();
        }
    }

    /** Pomocnicza metoda wypisująca listę kasjerów */
    private static void wypiszKasjerow(List<Employee> kasjerzy) {
        for (Employee k : kasjerzy) {
            System.out.printf("ID: %-3d Imię: %-10s Nazwisko: %-12s Login: %-8s Zarobki: %s zł%n",
                    k.getId(), k.getImie(), k.getNazwisko(), k.getLogin(), k.getZarobki());
        }
        System.out.println("-----------------------------");
    }
}
