/*
 * Classname: EmailSender
 * Version information: 1.1
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// Importy Log4j2
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasa do wysyłania wiadomości e-mail.
 * Używa protokołu SMTP do wysyłania wiadomości.
 */
public class EmailSender {

    private static final Logger log = LogManager.getLogger(EmailSender.class);

    /**
     * Tworzy i wysyła wiadomość e-mail.
     *
     * @param toEmailAddress   adres e-mail odbiorcy
     * @param fromEmailAddress adres e-mail nadawcy
     * @param password         hasło do konta nadawcy
     * @param subject          temat wiadomości
     * @param bodyText         treść wiadomości
     * @throws MessagingException jeśli wystąpi błąd podczas tworzenia lub wysyłania wiadomości
     */
    public static void sendEmail(String toEmailAddress,
                                 String fromEmailAddress,
                                 String password,
                                 String subject,
                                 String bodyText)
            throws MessagingException {
        log.debug("Rozpoczynanie wysyłania e-maila – start");
        log.debug("Parametry: do='{}', od='{}', temat='{}'", toEmailAddress, fromEmailAddress, subject);
        log.trace("Treść wiadomości: {}", bodyText);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        log.info("Ustawiono konfigurację SMTP");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                log.debug("Autoryzacja SMTP dla użytkownika: '{}'", fromEmailAddress);
                return new PasswordAuthentication(fromEmailAddress, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmailAddress));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddress));
        message.setSubject(subject);
        message.setText(bodyText);

        log.info("Wiadomość utworzona – wyślij do: {}", toEmailAddress);

        try {
            Transport.send(message);
            log.info("E-mail został pomyślnie wysłany do: {}", toEmailAddress);
        } catch (MessagingException e) {
            log.error("Błąd podczas wysyłania wiadomości do {}: {}", toEmailAddress, e.getMessage(), e);
            throw e;
        } finally {
            log.debug("Zakończono proces wysyłania e-maila");
        }
    }

    /**
     * Wysyła wiadomość e-mail z kodem resetowania hasła.
     *
     * @param email     adres e-mail odbiorcy
     * @param resetCode kod resetowania hasła
     */
    public static void sendResetEmail(String email, String resetCode) {
        log.info("Wysyłanie e-maila z kodem resetowania hasła do: {}", email);

        String subject = "Kod resetowania hasła";
        String body = "Twój kod resetowania hasła to: " + resetCode;

        try {
            sendEmail(email, "noreply@twojaaplikacja.pl", "dummyPassword", subject, body);
            log.debug("sendResetEmail() – wywołano sendEmail()");
        } catch (MessagingException e) {
            log.error("Nie udało się wysłać e-maila z kodem resetowania do {}: {}", email, e.getMessage(), e);
        }
    }
}