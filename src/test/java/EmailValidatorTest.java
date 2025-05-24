/*
 * Classname: EmailValidatorTest
 * Version information: 1.2
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.EmailValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailValidatorTest {

    @Test
    void testValidEmails() {
        assertTrue(EmailValidator.isValid("user@example.com"));
        assertTrue(EmailValidator.isValid("john.doe@domain.co.uk"));
        assertTrue(EmailValidator.isValid("anna123+work@mail.org"));
        assertTrue(EmailValidator.isValid("a@a.a")); // Minimalna długość domeny: 3 (np. a.a)
    }

    @Test
    void testInvalidEmailsMissingAtSymbol() {
        assertFalse(EmailValidator.isValid("invalidemail.com"));
        assertFalse(EmailValidator.isValid("anotheremail"));
        assertFalse(EmailValidator.isValid(null));
    }

    @Test
    void testInvalidEmailsWrongLocalPartLength() {
        // Za krótki
        assertFalse(EmailValidator.isValid("@example.com"));

        // Za długi (65 znaków)
        StringBuilder longLocal = new StringBuilder();
        for (int i = 0; i < 65; i++) longLocal.append("a");
        assertFalse(EmailValidator.isValid(longLocal + "@example.com"));
    }

    @Test
    void testInvalidEmailsWrongDomainLength() {
        // Za krótki (<3 znaków w domenie)
        assertFalse(EmailValidator.isValid("user@ab"));

        // Za długi (>253 znaków w domenie)
        StringBuilder longDomain = new StringBuilder("user@");
        // musimy przekroczyć 253 znaki w samej części domeny
        for (int i = 0; i < 254; i++) longDomain.append("a");
        assertFalse(EmailValidator.isValid(longDomain.toString()));
    }

    @Test
    void testInvalidEmailsDomainStartsOrEndsWithDot() {
        assertFalse(EmailValidator.isValid("user@.example.com"));
        assertFalse(EmailValidator.isValid("user@example.com."));
        assertFalse(EmailValidator.isValid("user@.com"));
    }

    @Test
    void testInvalidEmailsDomainWithoutDot() {
        assertFalse(EmailValidator.isValid("user@examplecom"));
        assertFalse(EmailValidator.isValid("user@domain"));
    }

    @Test
    void testInvalidEmailsIllegalCharactersInLocalPart() {
        assertFalse(EmailValidator.isValid("user#name@example.com"));
        assertFalse(EmailValidator.isValid("user!name@example.com"));
        assertFalse(EmailValidator.isValid("user@name@example.com")); // więcej niż jedno @
        assertFalse(EmailValidator.isValid("user name@example.com")); // spacja
        assertFalse(EmailValidator.isValid("user\"name@example.com")); // cudzysłów
    }
}
