/*
 * Classname: EmailValidator
 * Version information: 1.1
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasa EmailValidator służy do walidacji adresów e-mail.
 * Zawiera metodę isValid, która sprawdza poprawność adresu e-mail.
 */
public class EmailValidator {

    private static final Logger logger = LogManager.getLogger(EmailValidator.class);

    /**
     * Sprawdza, czy podany adres e-mail jest poprawny.
     *
     * @param email Adres e-mail do sprawdzenia.
     * @return true, jeśli adres e-mail jest poprawny, false w przeciwnym razie.
     */
    public static boolean isValid(String email) {
        logger.debug("Rozpoczynanie walidacji adresu e-mail: {}", email);

        if (email == null) {
            logger.warn("Adres e-mail jest null");
            return false;
        }

        if (!email.contains("@")) {
            logger.info("Brak znaku '@' w adresie e-mail: {}", email);
            return false;
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            logger.info("Nieprawidłowa liczba części po podziale '@': {}", email);
            return false;
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        logger.trace("Podzielono na lokalną część: '{}', domenę: '{}'", localPart, domainPart);

        if (localPart.isEmpty() || localPart.length() > 64) {
            logger.info("Nieprawidłowa długość lokalnej części (1–64 znaki): {}", localPart);
            return false;
        }

        if (domainPart.length() < 3 || domainPart.length() > 253) {
            logger.info("Nieprawidłowa długość domeny (3–253 znaki): {}", domainPart);
            return false;
        }

        if (!domainPart.contains(".")) {
            logger.info("Domena nie zawiera kropki: {}", domainPart);
            return false;
        }

        if (domainPart.startsWith(".") || domainPart.endsWith(".")) {
            logger.info("Domena zaczyna się lub kończy kropką: {}", domainPart);
            return false;
        }

        String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._%+-";
        for (char c : localPart.toCharArray()) {
            if (allowedChars.indexOf(c) == -1) {
                logger.info("Znaleziono niedozwolony znak w lokalnej części: '{}', znak: '{}'", localPart, c);
                return false;
            }
        }

        logger.info("Adres e-mail jest poprawny: {}", email);
        return true;
    }
}