/*
 * Classname: ConfigManager
 * Version information: 1.2
 * Date: 2025-06-06
 * Copyright notice: © BŁĘKITNI
 */

package org.example.pdflib;

import java.io.*;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Prosty menedżer konfiguracji oparty na pliku properties.
 */
public class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static Properties props = new Properties();

    public static String getReportPath() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
            String path = props.getProperty("pdf.output.path", "");
            logger.debug("Ustawiono ścieżkę raportów: {}", path);
            return path;
        } catch (IOException e) {
            logger.error("Nie można wczytać pliku konfiguracyjnego: {}", CONFIG_FILE, e);
            return "";
        }
    }

    public static boolean isNotificationsEnabled() {
        String value = props.getProperty("notifications.enabled", "true");
        logger.trace("Wczytano wartość notifications.enabled: {}", value);
        return Boolean.parseBoolean(value);
    }

    public static void setNotificationsEnabled(boolean on) {
        props.setProperty("notifications.enabled", Boolean.toString(on));
        save();
        logger.info("Powiadomienia zostały {}.", on ? "włączone" : "wyłączone");
    }

    public static boolean isLoggingEnabled() {
        String value = props.getProperty("logging.enabled", "true");
        logger.trace("Wczytano wartość logging.enabled: {}", value);
        return Boolean.parseBoolean(value);
    }

    public static void setLoggingEnabled(boolean on) {
        props.setProperty("logging.enabled", Boolean.toString(on));
        save();
        logger.info("Logowanie zostało {}.", on ? "włączone" : "wyłączone");
    }

    private static void save() {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Ustawienia aplikacji");
            logger.debug("Zapisano zmiany w pliku konfiguracyjnym.");
        } catch (IOException e) {
            logger.error("Nie można zapisać pliku konfiguracyjnego: {}", CONFIG_FILE, e);
        }
    }

    public static void setReportPath(String path) {
        Properties props = new Properties();
        try {
            File file = new File(CONFIG_FILE);
            if (file.exists()) {
                try (InputStream input = new FileInputStream(file)) {
                    props.load(input);
                }
            }

            props.setProperty("pdf.output.path", path);

            try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
                props.store(output, null);
                logger.info("Zaktualizowano ścieżkę wyjściową PDF na: {}", path);
            }

        } catch (IOException e) {
            logger.error("Nie można ustawić ścieżki wyjściowej PDF.", e);
        }
    }

    /**
     * Pobiera ścieżkę do pliku z logo (jeśli nie ma w properties, zwraca domyślną).
     */
    public static String getLogoPath() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
            return props.getProperty("pdf.logo.path", "src/main/resources/logo.png");
        } catch (IOException e) {
            logger.error("Nie można wczytać pliku konfiguracyjnego: {}", CONFIG_FILE, e);
            return "src/main/resources/logo.png";
        }
    }

    /**
     * Ustawia ścieżkę do logo i zapisuje ją do config.properties.
     */
    public static void setLogoPath(String path) {
        Properties props = new Properties();
        try {
            File file = new File(CONFIG_FILE);
            if (file.exists()) {
                try (InputStream input = new FileInputStream(file)) {
                    props.load(input);
                }
            }

            props.setProperty("pdf.logo.path", path);

            try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
                props.store(output, null);
                logger.info("Zaktualizowano ścieżkę do logo PDF na: {}", path);
            }
        } catch (IOException e) {
            logger.error("Nie można ustawić ścieżki do logo PDF.", e);
        }
    }
}