/*
 * Classname: EmailSender
 * Version information: 1.1
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import static org.example.sys.Login.generateRandomCode;

/**
 * Klasa do wysyłania wiadomości e-mail.
 * Używa protokołu SMTP do wysyłania wiadomości.
 * Obsługuje wysyłanie standardowych wiadomości e-mail oraz
 * wiadomości z kodem resetowania hasła.
 */
public class EmailSender {

    /**
     * Logger do rejestrowania zdarzeń związanych z wysyłaniem e-maili.
     */
    private static final Logger logger
            = LogManager.getLogger(EmailSender.class);

    /**
     * Tworzy i wysyła wiadomość e-mail.
     * Konfiguruje połączenie SMTP, uwierzytelnia użytkownika
     * i wysyła wiadomość.
     * Proces jest szczegółowo logowany na różnych poziomach.
     *
     * @param toEmailAddress   adres e-mail odbiorcy
     * @param fromEmailAddress adres e-mail nadawcy
     * @param password         hasło do konta nadawcy
     * @param subject          temat wiadomości
     * @param bodyText         treść wiadomości
     * @throws MessagingException jeśli wystąpi błąd podczas tworzenia
     * lub wysyłania wiadomości
     */
    public static void sendEmail(String toEmailAddress,
                                 String fromEmailAddress,
                                 String password,
                                 String subject,
                                 String bodyText)
            throws MessagingException {
        logger.debug("Rozpoczynanie wysyłania e-maila – start");
        logger.debug("Parametry: do='{}', od='{}', temat='{}'",
                toEmailAddress, fromEmailAddress, subject);
        logger.trace("Treść wiadomości: {}", bodyText);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        logger.info("Ustawiono konfigurację SMTP");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        logger.debug("Autoryzacja SMTP dla " +
                                        "użytkownika: '{}'",
                                fromEmailAddress);
                        return new PasswordAuthentication(fromEmailAddress,
                                password);
                    }
                });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmailAddress));
        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(toEmailAddress));
        message.setSubject(subject);
        message.setText(bodyText);

        logger.info("Wiadomość utworzona – wyślij do: {}",
                toEmailAddress);

        try {
            Transport.send(message);
            logger.info("E-mail został pomyślnie wysłany do: {}",
                    toEmailAddress);
        } catch (MessagingException e) {
            logger.error("Błąd podczas wysyłania wiadomości do {}: {}",
                    toEmailAddress, e.getMessage(), e);
            throw e;
        } finally {
            logger.debug("Zakończono proces wysyłania e-maila");
        }
    }

    /**
     * Wysyła wiadomość e-mail z kodem resetowania hasła do użytkownika.
     * Proces obejmuje:
     * 1. Generowanie losowego 6-cyfrowego kodu
     * 2. Znalezienie użytkownika w bazie danych po adresie e-mail
     * 3. Utworzenie i zapisanie tokenu resetowania hasła z zakodowanym kodem
     * 4. Wysłanie wiadomości e-mail z kodem resetowania
     *
     * Każdy krok procesu jest odpowiednio logowany.
     *
     * @param email adres e-mail użytkownika, dla którego generowany jest
     *              kod resetowania
     */
    public static void sendResetEmail(String email) {
        logger.info("Wysyłanie e-maila z kodem resetowania" +
                " hasła do: {}", email);

        // 1. Wygeneruj kod i przygotuj treść wiadomości
        String resetCode = generateRandomCode(6); //        6-cyfrowy kod
        String subject   = "Kod resetowania hasła - Stonka";
        String body      = "Twój kod resetowania hasła to: " +
                resetCode + "\n\n" +
                "Kod jest ważny przez 15 minut.\n" +
                "Jeśli to nie Ty żądałeś resetu hasła, zignoruj tę wiadomość.";

        UserRepository userRepo = new UserRepository();
        try {
            Employee employee = userRepo.findByEmail(email).stream()
                    .filter(emp -> !emp.isDeleted())
                    .findFirst()
                    .orElse(null);

            if (employee == null) {
                logger.warn("Nie znaleziono użytkownika o emailu: {}",
                        email);
                return;                 // ← nie ma kogo weryfikować
            }

            PasswordResetToken token = new PasswordResetToken();
            token.setUserId((employee.getId()));
            token.setResetCodeHash(BCrypt.hashpw(resetCode, BCrypt.gensalt()));
            token.setExpirationTime(LocalDateTime.now().plusMinutes(15));
            token.setUsed(false);
            token.setCreatedAt(LocalDateTime.now());

            boolean saved = userRepo.savePasswordResetToken(token);
            if (!saved) {
                logger.error("Token nie został zapisany – " +
                        "przerwano wysyłkę e-maila");
                return;
            }

            String[] credentials    = readEmailAndPasswordFromFile();
            String fromEmailAddress = credentials[0];
            String smtpPassword     = credentials[1];

            try {
                sendEmail(email, fromEmailAddress, smtpPassword, subject, body);
                logger.debug("E-mail z kodem resetowym wysłany do: {}",
                        email);
            } catch (MessagingException e) {
                logger.error("Nie udało się wysłać e-maila do {}: {}",
                        email, e.getMessage(), e);
            }

        } catch (Exception e) {
            logger.error("Błąd podczas wysyłania kodu resetowego: {}",
                    e.getMessage(), e);
        } finally {
            userRepo.close();
        }
    }

    /**
     * Weryfikuje wprowadzony kod resetowania hasła.
     * Proces obejmuje:
     * 1. Znalezienie użytkownika po adresie e-mail
     * 2. Pobranie aktywnych tokenów resetowania dla użytkownika
     * 3. Weryfikację kodu przy użyciu algorytmu BCrypt
     * 4. Oznaczenie użytego tokenu jako wykorzystanego
     *
     * Każdy krok procesu jest odpowiednio logowany.
     *
     * @param email adres e-mail użytkownika
     * @param code kod resetowania hasła wprowadzony przez użytkownika
     * @return true jeśli kod jest prawidłowy i token został oznaczony
     * jako użyty,
     *         false w przeciwnym przypadku
     */
    public static boolean verifyResetCode(String email, String code) {
        logger.info("Weryfikacja kodu resetującego dla: {}", email);

        if (email == null
                || code == null
                || email.isEmpty()
                || code.isEmpty()) {
            logger.warn("Nieprawidłowe dane wejściowe");
            return false;
        }

        UserRepository userRepo = new UserRepository();
        try {
            // 1. Znajdź użytkownika po emailu
            Employee employee = userRepo.findByEmail(email).stream()
                    .filter(emp -> !emp.isDeleted())
                    .findFirst()
                    .orElse(null);

            if (employee == null) {
                logger.warn("Nie znaleziono użytkownika o emailu: {}",
                        email);
                return false;
            }

            // 2. Znajdź wszystkie aktywne tokeny dla tego użytkownika
            List<PasswordResetToken> validTokens
                    = userRepo.findValidTokensByUserId((long) employee.getId());

            if (validTokens.isEmpty()) {
                logger.warn("Brak aktywnych tokenów dla użytkownika");
                return false;
            }

            // 3. Sprawdź każdy token (maksymalnie jeden powinien być ważny)
            for (PasswordResetToken token : validTokens) {
                // 4. Sprawdź zgodność kodu przy użyciu BCrypt
                if (BCrypt.checkpw(code, token.getResetCodeHash())) {
                    // 5. Zaznacz token jako użyty
                    if (userRepo.markTokenAsUsed(token)) {
                        logger.info("Kod poprawny i został oznaczony" +
                                " jako użyty");
                        return true;
                    } else {
                        logger.warn("Nie udało się oznaczyć tokenu" +
                                " jako użytego");
                        return false;
                    }
                }
            }

            logger.warn("Kod nie pasuje do żadnego tokenu");
            return false;

        } catch (Exception e) {
            logger.error("Błąd podczas weryfikacji kodu: {}",
                    e.getMessage(), e);
            return false;
        } finally {
            userRepo.close();
        }
    }

    /**
     * Odczytuje dane uwierzytelniające SMTP z pliku PASS.txt.
     * Plik powinien zawierać dwie linie:
     * 1. Adres e-mail nadawcy
     * 2. Hasło do konta SMTP
     *
     * Weryfikuje czy plik istnieje oraz czy zawiera prawidłowe dane.
     *
     * @return tablica dwóch elementów: [adres email, hasło]
     * @throws Exception jeśli plik nie istnieje, ma nieprawidłowy format lub
     *                   zawiera puste dane
     */
    private static String[] readEmailAndPasswordFromFile() throws Exception {
        Path path = Paths.get("PASS.txt");
        if (!Files.exists(path)) {
            logger.error("Brak pliku PASS.txt w katalogu projektu");
            throw new RuntimeException("Brak pliku konfiguracyjnego: PASS.txt");
        }

        List<String> lines = Files.readAllLines(path);
        if (lines.size() < 2) {
            logger.error("Plik PASS.txt ma nieprawidłową liczbę linii");
            throw new RuntimeException("Plik PASS.txt musi zawierać 2 linie:" +
                    " email i hasło");
        }

        String email = lines.get(0).trim();
        String password = lines.get(1).trim();

        if (email.isEmpty() || password.isEmpty()) {
            logger.error("Email lub hasło w PASS.txt są puste");
            throw new RuntimeException("Email lub hasło w PASS.txt są puste");
        }

        return new String[]{email, password};
    }
}