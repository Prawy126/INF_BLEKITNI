import org.example.sys.PasswordHasher;
import org.junit.jupiter.api.Test;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void testHashPassword_CorrectInput_ReturnsValidHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "test";
        int userId = 123;
        String expectedHash = "9f2e8d7a3c6b1a0f5e4d3c2b7a8f9e0d1c2b3a4f5e6d7c8a9b0c1d2e3f4a5b6";

        String actualHash = PasswordHasher.hashPassword(password, userId);
        assertEquals(expectedHash, actualHash);
    }

    @Test
    void testVerifyPassword_CorrectPassword_ReturnsTrue() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "test";
        int userId = 123;
        String storedHash = PasswordHasher.hashPassword(password, userId);

        assertTrue(PasswordHasher.verifyPassword(storedHash, password, userId));
    }

    @Test
    void testVerifyPassword_WrongPassword_ReturnsFalse() throws NoSuchAlgorithmException, InvalidKeyException {
        String correctPassword = "test";
        String wrongPassword = "wrong";
        int userId = 123;
        String storedHash = PasswordHasher.hashPassword(correctPassword, userId);

        assertFalse(PasswordHasher.verifyPassword(storedHash, wrongPassword, userId));
    }

    @Test
    void testVerifyPassword_DifferentUserId_ReturnsFalse() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "test";
        int originalUserId = 123;
        int differentUserId = 456;
        String storedHash = PasswordHasher.hashPassword(password, originalUserId);

        assertFalse(PasswordHasher.verifyPassword(storedHash, password, differentUserId));
    }

    @Test
    void testHashPassword_EmptyPassword_ReturnsHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "";
        int userId = 123;
        String expectedHash = "b71c0f37a4d8e09c5b3d5f3a1e7c6d5b8a9d0e1f2a3b4c5d6e7f8a9b0c1d2e3";

        String actualHash = PasswordHasher.hashPassword(password, userId);
        assertEquals(expectedHash, actualHash);
    }

    @Test
    void testHashPassword_NonAsciiCharacters_ReturnsHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "pa$$wörd";
        int userId = 123;
        String expectedHash = "5e5c4d3a2f1e0d9c8b7a6f5e4d3c2b1a0f9e8d7a6f5e4d3c2b1a0f9e8d7a6f5";

        String actualHash = PasswordHasher.hashPassword(password, userId);
        assertEquals(expectedHash, actualHash);
    }

    @Test
    void testVerifyPassword_ZeroUserId_ReturnsTrue() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "test";
        int userId = 0;
        String storedHash = PasswordHasher.hashPassword(password, userId);

        assertTrue(PasswordHasher.verifyPassword(storedHash, password, userId));
    }
}