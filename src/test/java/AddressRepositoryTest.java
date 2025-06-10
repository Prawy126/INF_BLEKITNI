/*
 * Classname: AddressRepositoryTest
 * Version information: 1.2
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.database.AddressRepository;
import org.example.sys.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Klasa testująca działanie AddressRepository.
 */
public class AddressRepositoryTest {

    private AddressRepository addressRepo;

    /**
     * Przygotowuje nowe repozytorium przed każdym testem.
     */
    @BeforeEach
    void setUp() {
        addressRepo = new AddressRepository();
    }

    /**
     * Zamyka repozytorium po każdym teście, zwalniając zasoby.
     */
    @AfterEach
    void tearDown() {
        addressRepo.close();
    }

    /**
     * Testuje pełen cykl CRUD dla AddressRepository:
     * 1) dodanie nowego addressu,
     * 2) sprawdzenie ustawionego ID,
     * 3) pobranie po ID i weryfikację pól,
     * 4) pobranie listy wszystkich adresów,
     * 5) usunięcie i weryfikację braku wczytanego addressu.
     */
    @Test
    void addressCrudOperationsShouldWorkCorrectly() {
        // === 1. Utworzenie i zapis nowego adresu ===
        Address address = new Address();
        address.setTown("Testowo");
        address.setCity("Miastko");
        address.setZipCode("99-999");
        address.setHouseNumber("10B");
        address.setApartmentNumber("3");

        addressRepo.addAddress(address);

        // po persist powinno się ustawić ID > 0
        assertTrue(address.getId() > 0, "ID adresu powinno " +
                "być ustawione po zapisie");

        // === 2. Pobranie po ID i weryfikacja ===
        Address found = addressRepo.findAddressById(address.getId());
        assertNotNull(found, "Adres powinien zostać znaleziony po ID");
        assertEquals("Testowo", found.getTown(),
                "Miejscowość powinna być zgodna");
        assertEquals("Miastko", found.getCity(),
                "Miasto powinno być zgodne");
        assertEquals("99-999", found.getZipCode(),
                "Kod pocztowy powinien być zgodny");
        assertEquals("10B", found.getHouseNumber(),
                "Numer domu powinien być zgodny");
        assertEquals("3", found.getApartmentNumber(),
                "Numer mieszkania powinien być zgodny");

        // === 3. Pobranie wszystkich adresów ===
        List<Address> all = addressRepo.getAllAddresses();
        assertFalse(all.isEmpty(), "Lista adresów nie powinna" +
                " być pusta");

        // === 4. Usunięcie i weryfikacja usunięcia ===
        addressRepo.removeAddress(address.getId());
        Address deleted = addressRepo.findAddressById(address.getId());
        assertNull(deleted, "Adres powinien zostać usunięty " +
                "z repozytorium");
    }
}
