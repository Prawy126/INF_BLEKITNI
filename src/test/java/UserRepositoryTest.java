/*
 * Classname: TestUserRepository
 * Version information: 1.0
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.AddressRepository;
import org.example.database.UserRepository;
import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.wyjatki.SalaryException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Klasa testująca działanie UserRepository i AddressRepository.
 */
public class UserRepositoryTest {

    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository();
        AddressRepository addressRepo = new AddressRepository();

        try {
            // === 1. Tworzenie i dodanie adresu ===
            Address adres = new Address();
            adres.setTown("Testowo");
            adres.setHouseNumber("10A");
            adres.setApartmentNumber("5");
            adres.setZipCode("00-000");
            adres.setCity("Testowo");

            addressRepo.addAddress(adres);
            System.out.println(">>> Dodano adres: " + adres.getCity());

            // === 2. Dodanie nowego pracownika ===
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

            userRepo.addEmployee(nowyKasjer);
            System.out.println(
                    ">>> Dodano pracownika: " + nowyKasjer.getLogin()
            );

            // === 3. Pobieranie kasjerów ===
            System.out.println("\n>>> Lista kasjerów po dodaniu:");
            wypiszPracownikow(userRepo.getCashiers());

            // === 4. Wyszukiwanie po loginie ===
            Employee znalezionyPoLoginie = userRepo.findByLogin("mbrzo");
            System.out.println(
                    "\n>>> Znaleziony po loginie: " +
                            znalezionyPoLoginie.getName() + " " +
                            znalezionyPoLoginie.getSurname()
            );

            // === 5. Wyszukiwanie po loginie i haśle ===
            Employee znalezionyPoLoginieIHasle = userRepo
                    .findByLoginAndPassword("mbrzo", "tajnehaslo");
            if (znalezionyPoLoginieIHasle != null) {
                System.out.println(
                        ">>> Znaleziony użytkownik (login + hasło): " +
                                znalezionyPoLoginieIHasle.getName() + " " +
                                znalezionyPoLoginieIHasle.getSurname()
                );
            } else {
                System.out.println(
                        ">>> Nie znaleziono użytkownika przy logowaniu!"
                );
            }

            // === 6. Aktualizacja pracownika ===
            znalezionyPoLoginie.setSurname("Brzoza");
            znalezionyPoLoginie.setSalary(new BigDecimal("3400.00"));
            userRepo.updateEmployee(znalezionyPoLoginie);
            System.out.println(">>> Zaktualizowano nazwisko i pensję.");

            Employee poAktualizacji = userRepo.findByLogin("mbrzo");
            System.out.println(
                    ">>> Po aktualizacji: " + poAktualizacji.getName() + " " +
                            poAktualizacji.getSurname() + ", zarobki: " +
                            poAktualizacji.getSalary()
            );

            // === 7. Pobieranie wszystkich pracowników ===
            System.out.println("\n>>> Lista wszystkich pracowników:");
            wypiszPracownikow(userRepo.getAllEmployess());

            // === 8. Usuwanie pracownika ===
            userRepo.removeEmployee(poAktualizacji);
            System.out.println(
                    ">>> Usunięto pracownika o id = " +
                            poAktualizacji.getId()
            );

            // === 9. Sprawdzenie kasjerów po usunięciu ===
            System.out.println("\n>>> Lista kasjerów po usunięciu:");
            wypiszPracownikow(userRepo.getCashiers());

        } catch (SalaryException e) {
            System.err.println(
                    "Błąd walidacji wynagrodzenia: " + e.getMessage()
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Zamknięcie repozytoriów
            userRepo.close();
            addressRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca listę pracowników.
     *
     * @param pracownicy lista pracowników do wypisania
     */
    private static void wypiszPracownikow(List<Employee> pracownicy) {
        if (pracownicy.isEmpty()) {
            System.out.println("(Brak wyników)");
            return;
        }
        for (Employee e : pracownicy) {
            System.out.printf(
                    "ID: %-3d Imię: %-10s Nazwisko: %-12s Login: %-8s "
                            + "Zarobki: %8.2f zł%n",
                    e.getId(),
                    e.getName(),
                    e.getSurname(),
                    e.getLogin(),
                    e.getSalary()
            );
        }
        System.out.println("-----------------------------");
    }
}
