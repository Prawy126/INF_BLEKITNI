/*
 * Classname: AbsenceRequestRepositoryTest
 * Version information: 1.5
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.example.database.AbsenceRequestRepository;
import org.example.database.UserRepository;
import org.example.sys.AbsenceRequest;
import org.example.sys.AbsenceRequest.RequestStatus;
import org.example.sys.Employee;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Klasa testująca działanie AbsenceRequestRepository.
 */
public class AbsenceRequestRepositoryTest {

    private static AbsenceRequestRepository absenceRepo;
    private static UserRepository userRepo;
    private static Employee testEmployee;
    private static SimpleDateFormat sdf;
    private static EntityManagerFactory emFactory;

    @BeforeAll
    static void initAll() throws Exception {
        absenceRepo = new AbsenceRequestRepository();
        userRepo    = new UserRepository();
        sdf         = new SimpleDateFormat("yyyy-MM-dd");

        // Pobieramy pierwszego pracownika z bazy
        testEmployee = userRepo.getAllEmployees().get(0);

        // Opróżniamy tabelę Wnioski_o_nieobecnosc i jednocześnie
        // rozciągamy kolumnę Status do VARCHAR(20), żeby pomieścić nazwy enumów
        emFactory = Persistence.createEntityManagerFactory("myPU");
        EntityManager em = emFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // 1) usuń dane z tabeli
            em.createQuery("DELETE FROM AbsenceRequest").executeUpdate();
            // 2) zmień typ kolumny Status (MySQL) na VARCHAR(20)
            em.createNativeQuery(
                    "ALTER TABLE Wnioski_o_nieobecnosc " +
                            "MODIFY COLUMN `Status` VARCHAR(20) NOT NULL"
            ).executeUpdate();
            tx.commit();
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @AfterAll
    static void tearDownAll() {
        absenceRepo.close();
        userRepo.close();
        if (emFactory.isOpen()) emFactory.close();
    }

    /**
     * Testuje dodawanie nowego wniosku oraz pobieranie wszystkich wniosków.
     */
    @Test
    void testAddRequestAndGetAllRequests() throws Exception {
        Date start = sdf.parse("2025-07-01");
        Date end   = sdf.parse("2025-07-10");

        AbsenceRequest request = new AbsenceRequest();
        request.setRequestType("Vacation");
        request.setStartDate(start);
        request.setEndDate(end);
        request.setDescription("Test vacation");
        request.setEmployee(testEmployee);
        request.setStatus(RequestStatus.PENDING);

        absenceRepo.addRequest(request);

        List<AbsenceRequest> all = absenceRepo.getAllRequests();
        Assertions.assertFalse(all.isEmpty(), "Lista wniosków nie może być pusta po dodaniu");

        AbsenceRequest found = all.stream()
                .filter(r -> r.getId() == request.getId())
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(found, "Dodany wniosek powinien być na liście");
        Assertions.assertEquals("Test vacation",
                found.getDescription(),
                "Opis powinien być zgodny");
    }

    /**
     * Testuje pobieranie wniosku po jego identyfikatorze.
     */
    @Test
    void testFindRequestById() throws Exception {
        Date start = sdf.parse("2025-08-01");
        Date end   = sdf.parse("2025-08-05");

        AbsenceRequest request = new AbsenceRequest();
        request.setRequestType("Sick Leave");
        request.setStartDate(start);
        request.setEndDate(end);
        request.setDescription("Test sick leave");
        request.setEmployee(testEmployee);
        request.setStatus(RequestStatus.PENDING);

        absenceRepo.addRequest(request);

        AbsenceRequest loaded = absenceRepo.findRequestById(request.getId());
        Assertions.assertNotNull(loaded, "Wniosek powinien zostać znaleziony po ID");

        Assertions.assertEquals("Test sick leave",
                loaded.getDescription(),
                "Opis wniosku powinien być zgodny");
        Assertions.assertEquals("Sick Leave",
                loaded.getRequestType(),
                "Typ wniosku powinien być zgodny");
    }

    /**
     * Testuje aktualizację istniejącego wniosku.
     */
    @Test
    void testUpdateRequest() throws Exception {
        Date start = sdf.parse("2025-09-01");
        Date end   = sdf.parse("2025-09-07");

        AbsenceRequest request = new AbsenceRequest();
        request.setRequestType("Business Trip");
        request.setStartDate(start);
        request.setEndDate(end);
        request.setDescription("Initial description");
        request.setEmployee(testEmployee);
        request.setStatus(RequestStatus.PENDING);

        absenceRepo.addRequest(request);

        // Modyfikujemy opis i zapisujemy
        request.setDescription("Updated description");
        absenceRepo.updateRequest(request);

        AbsenceRequest loaded = absenceRepo.findRequestById(request.getId());
        Assertions.assertNotNull(loaded, "Po aktualizacji wniosek powinien nadal istnieć");
        Assertions.assertEquals("Updated description",
                loaded.getDescription(),
                "Opis wniosku powinien być zaktualizowany");
    }

    /**
     * Testuje usunięcie wniosku o nieobecność po jego identyfikatorze.
     */
    @Test
    void testRemoveRequest() throws Exception {
        Date start = sdf.parse("2025-10-01");
        Date end   = sdf.parse("2025-10-02");

        AbsenceRequest request = new AbsenceRequest();
        request.setRequestType("Conference");
        request.setStartDate(start);
        request.setEndDate(end);
        request.setDescription("Test conference");
        request.setEmployee(testEmployee);
        request.setStatus(RequestStatus.PENDING);

        absenceRepo.addRequest(request);

        int id = request.getId();
        absenceRepo.removeRequest(id);

        AbsenceRequest loaded = absenceRepo.findRequestById(id);
        Assertions.assertNull(loaded, "Usunięty wniosek nie powinien zostać znaleziony");
    }
}
