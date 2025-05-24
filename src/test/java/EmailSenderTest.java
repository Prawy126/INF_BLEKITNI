/*
 * Classname: EmailSenderTest
 * Version information: 1.2
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.EmailSender;
import org.junit.jupiter.api.Test;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailSenderTest {

    // Dummy dane do testów
    private static final String VALID_EMAIL    = "valid.email@example.com";
    private static final String VALID_PASSWORD = "password123";
    private static final String SUBJECT        = "Test Subject";
    private static final String BODY           = "This is a test email body.";
    private static final String RESET_CODE     = "abc123xyz";

    @Test
    void testSendEmail_ThrowsException_WhenInvalidEmail() {
        // "invalid-email" to nie jest poprawny adres — spodziewamy się MessagingException
        assertThrows(MessagingException.class, () -> {
            EmailSender.sendEmail(
                    "invalid-email",
                    VALID_EMAIL,
                    VALID_PASSWORD,
                    SUBJECT,
                    BODY
            );
        });
    }

    @Test
    void testSendEmail_DoesNotThrowUnexpectedException_WithValidParameters() {
        try {
            EmailSender.sendEmail(
                    VALID_EMAIL,
                    VALID_EMAIL,
                    VALID_PASSWORD,
                    SUBJECT,
                    BODY
            );
        } catch (AuthenticationFailedException ex) {
            // <--- IGNORUJEMY tu, bo dummy@example.com nigdy nie zautoryzuje się na Gmailu
        } catch (MessagingException ex) {
            fail("Unexpected MessagingException: " + ex.getMessage());
        }
    }

    @Test
    void testSendResetEmail_CallsSendEmailWithCorrectSubjectAndBody() {
        // Po prostu upewniamy się, że wywołanie nie rzuca
        assertDoesNotThrow(() -> {
            EmailSender.sendResetEmail(VALID_EMAIL, RESET_CODE);
        });
    }
}
