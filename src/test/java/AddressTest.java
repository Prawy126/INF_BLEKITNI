/*
 * Classname: AddressTest
 * Version information: 1.2
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.Address;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address address;

    @BeforeEach
    void setup() {
        address = new Address();
    }

    @Test
    void testSetAndGetMiejscowosc() {
        address.setTown("Warszawa");
        assertEquals("Warszawa", address.getTown());
    }

    @Test
    void testSetAndGetNumerDomu() {
        address.setHouseNumber("15B");
        assertEquals("15B", address.getHouseNumber());
    }

    @Test
    void testSetAndGetNumerMieszkania() {
        address.setApartmentNumber("8");
        assertEquals("8", address.getApartmentNumber());
    }

    @Test
    void testSetAndGetKodPocztowy() {
        address.setZipCode("00-123");
        assertEquals("00-123", address.getZipCode());
    }

    @Test
    void testSetAndGetMiasto() {
        address.setCity("Kraków");
        assertEquals("Kraków", address.getCity());
    }

    @Test
    void testToStringWithMieszkanie() {
        address.setTown("Poznań");
        address.setHouseNumber("10A");
        address.setApartmentNumber("4");
        address.setZipCode("60-123");
        address.setCity("Poznań");

        String expected = "Poznań, ul. 10A/4, 60-123 Poznań";
        assertEquals(expected, address.toString());
    }

    @Test
    void testToStringWithoutMieszkanie() {
        address.setTown("Wrocław");
        address.setHouseNumber("7");
        address.setApartmentNumber(""); // brak mieszkania
        address.setZipCode("50-001");
        address.setCity("Wrocław");

        String expected = "Wrocław, ul. 7, 50-001 Wrocław";
        assertEquals(expected, address.toString());
    }
}