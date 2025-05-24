/*
 * Classname: EmailSender
 * Version information: 1.0
 * Date: 2025-05-24
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
        log.debug("sendEmail() – start, to='{}', from='{}', subject='{}'",
                toEmailAddress, fromEmailAddress, subject);

        // Konfiguracja serwera SMTP (Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.auth",           "true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host",           "smtp.gmail.com");
        props.put("mail.smtp.port",           "587");
        log.debug("SMTP properties: {}", props);

        // Utworzenie sesji
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                log.debug("Providing credentials for '{}'", fromEmailAddress);
                return new PasswordAuthentication(fromEmailAddress, password);
            }
        });

        // Utworzenie wiadomości
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmailAddress));
        email.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddress));
        email.setSubject(subject);
        email.setText(bodyText);
        log.info("Email built: from='{}', to='{}', subject='{}'",
                fromEmailAddress, toEmailAddress, subject);

        // Wysłanie wiadomości
        try {
            Transport.send(email);
            log.info("sendEmail() – wysłano poprawnie");
        } catch (MessagingException e) {
            log.error("sendEmail() – błąd podczas wysyłania", e);
            throw e;
        } finally {
            log.debug("sendEmail() – koniec");
        }
    }

    /**
     * Wysyła wiadomość e-mail z kodem resetowania hasła.
     *
     * @param email     adres e-mail odbiorcy
     * @param resetCode kod resetowania hasła
     */
    public static void sendResetEmail(String email, String resetCode) {
        log.debug("sendResetEmail() – start, email='{}'", email);

        String subject = "Kod resetowania hasła";
        String body    = "Twój kod resetowania hasła to: " + resetCode;
        log.info("Reset email: subject='{}', body='[skrócone]'", subject);

        try {
            sendEmail(email, email, /* dummy pass */ "", subject, body);
            log.info("sendResetEmail() – wywołano sendEmail()");
        } catch (MessagingException e) {
            log.error("sendResetEmail() – nie udało się wysłać", e);
        } finally {
            log.debug("sendResetEmail() – koniec");
        }
    }
}
