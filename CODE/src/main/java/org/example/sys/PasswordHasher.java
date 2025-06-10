/*
 * Classname: PasswordHasher
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Klasa do haszowania haseł z użyciem HMAC i SHA-256.
 * Używa ID użytkownika jako klucza do haszowania.
 */
public class PasswordHasher {

    // Inicjalizacja logera
    private static final Logger logger
            = LogManager.getLogger(PasswordHasher.class);

    // Algorytm HMAC do haszowania
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    /**
     * Haszuje hasło użytkownika z użyciem HMAC i SHA-256.
     *
     * @param password hasło do haszowania
     * @param userId   ID użytkownika (używane jako klucz HMAC)
     * @return hasz hasła w formacie heksadecymalnym
     * @throws NoSuchAlgorithmException jeśli algorytm HMAC nie jest dostępny
     * @throws InvalidKeyException      jeśli klucz HMAC jest nieprawidłowy
     */
    public static String hashPassword(String password, int userId)
            throws NoSuchAlgorithmException, InvalidKeyException {

        if (password == null || password.isEmpty()) {
            logger.warn("Próba haszowania pustego hasła.");
            throw new IllegalArgumentException("Hasło nie może być puste.");
        }

        try {
            // Konwersja ID użytkownika na bajty (klucz HMAC)
            byte[] keyBytes
                    = String.valueOf(userId).getBytes(StandardCharsets.UTF_8);
            SecretKeySpec key = new SecretKeySpec(keyBytes, HMAC_ALGORITHM);

            // Tworzenie instancji MAC
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(key);

            // Obliczanie HMAC dla hasła
            byte[] hmacBytes
                    = mac.doFinal(password.getBytes(StandardCharsets.UTF_8));

            // Konwersja wyniku na heksadecymalny string
            String hashedPassword = bytesToHex(hmacBytes);

            logger.info("Zahaszowano hasło dla użytkownika ID: {}",
                    userId);
            return hashedPassword;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Błąd podczas haszowania hasła dla" +
                    " użytkownika ID: {}", userId, e);
            throw e;
        }
    }

    /**
     * Weryfikuje hasło użytkownika porównując je z przechowywanym haszem.
     *
     * @param storedHash przechowywany hash hasła
     * @param password   hasło do weryfikacji
     * @param userId     ID użytkownika (używane jako klucz HMAC)
     * @return true, jeśli hasło jest poprawne, false w przeciwnym razie
     * @throws NoSuchAlgorithmException jeśli algorytm HMAC nie jest dostępny
     * @throws InvalidKeyException      jeśli klucz HMAC jest nieprawidłowy
     */
    public static boolean verifyPassword(
            String storedHash,
            String password,
            int userId
    ) throws NoSuchAlgorithmException, InvalidKeyException {

        if (storedHash == null || password == null) {
            logger.warn("Nieprawidłowe dane wejściowe do weryfikacji hasła.");
            return false;
        }

        String newHash = hashPassword(password, userId);
        boolean isEqual = MessageDigest.isEqual(
                newHash.getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8)
        );

        if (isEqual) {
            logger.info("Weryfikacja hasła powiodła się dla" +
                    " użytkownika ID: {}", userId);
        } else {
            logger.warn("Weryfikacja hasła " +
                    "NIE powiodła się dla użytkownika ID: {}", userId);
        }

        return isEqual;
    }

    /**
     * Konwertuje tablicę bajtów na string heksadecymalny.
     *
     * @param bytes tablica bajtów do konwersji
     * @return string heksadecymalny
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}