/*
 * Classname: TestTechnicalIssueRepository
 * Version information: 1.0
 * Date: 2025-05-15
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.TechnicalIssueRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.sys.TechnicalIssue;

import java.time.LocalDate;
import java.util.List;

/**
 * Klasa testująca działanie TechnicalIssueRepository.
 */
public class TechnicalIssueRepositoryTest {

    public static void main(String[] args) {
        TechnicalIssueRepository issueRepo = new TechnicalIssueRepository();
        UserRepository userRepo = new UserRepository();

        try {
            // Pobierz pracownika do przypisania zgłoszenia
            List<Employee> pracownicy = userRepo.getAllEmployess();
            if (pracownicy.isEmpty()) {
                System.out.println("Brak pracowników w bazie. Dodaj pracownika przed testem.");
                return;
            }

            Employee employee = pracownicy.get(0); // wybierz pierwszego

            // === 1. Dodanie nowego zgłoszenia ===
            TechnicalIssue issue = new TechnicalIssue();
            issue.setType("Awaria terminala");
            issue.setDescription("Terminal płatniczy nie działa.");
            issue.setDateSubmitted(LocalDate.now());
            issue.setStatus("Nowe");
            issue.setEmployee(employee);

            issueRepo.addIssue(issue);
            System.out.println(">>> Dodano zgłoszenie!");

            // === 2. Wyświetlenie wszystkich zgłoszeń ===
            System.out.println("\n>>> Lista zgłoszeń:");
            wypiszZgloszenia(issueRepo.getAllIssues());

            // === 3. Odczyt zgłoszenia po ID ===
            TechnicalIssue znalezione = issueRepo.findIssueById(issue.getId());
            System.out.println("\n>>> Zgłoszenie po ID: " + znalezione);

            // === 4. Aktualizacja zgłoszenia ===
            issue.setStatus("W trakcie");
            issue.setDescription("Zgłoszenie przekazane do serwisu.");
            issueRepo.updateIssue(issue);
            System.out.println(">>> Zaktualizowano zgłoszenie.");

            // === 5. Wyświetlenie po aktualizacji ===
            System.out.println("\n>>> Lista po aktualizacji:");
            wypiszZgloszenia(issueRepo.getAllIssues());

            // === 6. Usunięcie zgłoszenia ===
            issueRepo.removeIssue(issue);
            System.out.println(">>> Usunięto zgłoszenie.");

            // === 7. Lista po usunięciu ===
            System.out.println("\n>>> Lista po usunięciu:");
            wypiszZgloszenia(issueRepo.getAllIssues());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            issueRepo.close();
            userRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca zgłoszenia.
     *
     * @param zgloszenia lista zgłoszeń
     */
    private static void wypiszZgloszenia(List<TechnicalIssue> zgloszenia) {
        if (zgloszenia.isEmpty()) {
            System.out.println("(Brak zgłoszeń)");
        } else {
            for (TechnicalIssue z : zgloszenia) {
                System.out.printf("ID: %-3d Typ: %-20s Status: %-15s Data: %-10s\n",
                        z.getId(),
                        z.getType(),
                        z.getStatus(),
                        z.getDateSubmitted()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
