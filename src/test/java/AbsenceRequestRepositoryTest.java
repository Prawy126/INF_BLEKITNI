/*
 * Classname: TestAbsenceRequestRepository
 * Version information: 1.0
 * Date: 2025-05-12
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.AbsenceRequestRepository;
import org.example.database.UserRepository;
import org.example.sys.AbsenceRequest;
import org.example.sys.Employee;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Klasa testująca działanie AbsenceRequestRepository.
 */
public class AbsenceRequestRepositoryTest {

    public static void main(String[] args) {
        AbsenceRequestRepository absenceRepo = new AbsenceRequestRepository();
        UserRepository userRepo = new UserRepository();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse("2025-07-01");
            Date end = sdf.parse("2025-07-10");

            // === 1. Tworzenie przykładowego pracownika ===
            Employee testEmployee = userRepo.pobierzWszystkichPracownikow().get(0); // zakładamy że istnieje

            // === 2. Dodanie nowego wniosku ===
            AbsenceRequest request = new AbsenceRequest();
            request.setApplicationType("Urlop wypoczynkowy");
            request.setStartDate(start);
            request.setEndDate(end);
            request.setDescription("Testowy urlop");
            request.setEmployee(testEmployee);

            absenceRepo.addApplication(request);
            System.out.println(">>> Dodano wniosek o nieobecność.");

            // === 3. Pobranie wszystkich wniosków ===
            System.out.println("\n>>> Lista wszystkich wniosków:");
            wypiszWnioski(absenceRepo.downloadAllApplications());

            // === 4. Aktualizacja ===
            request.setDescription("Zmieniony opis urlopu");
            absenceRepo.updateApplication(request);
            System.out.println(">>> Zaktualizowano wniosek.");

            // === 5. Odczyt po ID ===
            AbsenceRequest loaded = absenceRepo.findApplicationById(request.getId());
            System.out.println(">>> Wniosek po ID: " + loaded);

            // === 6. Usunięcie ===
            absenceRepo.deleteApplication(loaded.getId());
            System.out.println(">>> Usunięto wniosek.");

            // === 7. Lista po usunięciu ===
            System.out.println("\n>>> Lista po usunięciu:");
            wypiszWnioski(absenceRepo.downloadAllApplications());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            absenceRepo.close();
            userRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca wnioski o nieobecność.
     *
     * @param lista lista wniosków
     */
    private static void wypiszWnioski(List<AbsenceRequest> lista) {
        if (lista.isEmpty()) {
            System.out.println("(Brak wniosków)");
        } else {
            for (AbsenceRequest r : lista) {
                System.out.printf("ID: %-3d | Typ: %-25s | Pracownik: %-20s | Od: %s | Do: %s | Opis: %s%n",
                        r.getId(),
                        r.getApplicationType(),
                        r.getEmployee().getName() + " " + r.getEmployee().getSurname(),
                        r.getStartDate(),
                        r.getEndDate(),
                        r.getDescription()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
