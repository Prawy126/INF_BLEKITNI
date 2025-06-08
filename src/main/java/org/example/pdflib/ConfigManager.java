/*
 * Classname: ConfigManager
 * Version information: 1.3
 * Date: 2025-06-06
 * Copyright notice: © BŁĘKITNI
 */

package org.example.pdflib;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.utils.AppPaths;

/**
 * Prosty menedżer konfiguracji oparty na pliku properties.
 */
public class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static Properties props = new Properties();

    static {
        loadProperties();
    }

    /**
     * Ładuje właściwości z pliku konfiguracyjnego.
     */
    private static void loadProperties() {
        Path configFile = AppPaths.getConfigFile(CONFIG_FILE);

        try {
            if (Files.exists(configFile)) {
                try (InputStream input = Files.newInputStream(configFile)) {
                    props.load(input);
                    logger.debug("Wczytano konfigurację z {}", configFile);
                }
            } else {
                // Ustaw domyślne wartości
                props.setProperty("pdf.output.path", AppPaths.getReportsDirectory().toString());
                props.setProperty("pdf.logo.path", AppPaths.getResourceFile("logo.png").toString());
                props.setProperty("logging.enabled", "true");

                saveProperties();
                logger.info("Utworzono domyślny plik konfiguracyjny w {}", configFile);
            }
        } catch (IOException e) {
            logger.error("Nie można wczytać pliku konfiguracyjnego: {}", configFile, e);
        }
    }

    /**
     * Zapisuje właściwości do pliku konfiguracyjnego.
     */
    private static void saveProperties() {
        Path configFile = AppPaths.getConfigFile(CONFIG_FILE);

        try {
            Files.createDirectories(configFile.getParent());
            try (OutputStream output = Files.newOutputStream(configFile)) {
                props.store(output, "Ustawienia aplikacji");
                logger.debug("Zapisano konfigurację do {}", configFile);
            }
        } catch (IOException e) {
            logger.error("Nie można zapisać pliku konfiguracyjnego: {}", configFile, e);
        }
    }

    public static String getReportPath() {
        String path = props.getProperty("pdf.output.path",
                AppPaths.getReportsDirectory().toString());
        logger.debug("Pobrano ścieżkę raportów: {}", path);
        return path;
    }

    public static boolean isLoggingEnabled() {
        String value = props.getProperty("logging.enabled", "true");
        logger.trace("Wczytano wartość logging.enabled: {}", value);
        return Boolean.parseBoolean(value);
    }

    public static void setLoggingEnabled(boolean on) {
        props.setProperty("logging.enabled", Boolean.toString(on));
        saveProperties();
        logger.info("Logowanie zostało {}.", on ? "włączone" : "wyłączone");
    }

    public static void setReportPath(String path) {
        props.setProperty("pdf.output.path", path);
        saveProperties();
        logger.info("Zaktualizowano ścieżkę wyjściową PDF na: {}", path);
    }

    /**
     * Pobiera ścieżkę do pliku z logo.
     */
    public static String getLogoPath() {
        String path = props.getProperty("pdf.logo.path",
                AppPaths.getResourceFile("logo.png").toString());
        logger.debug("Pobrano ścieżkę do logo: {}", path);
        return path;
    }

    /**
     * Ustawia ścieżkę do logo i zapisuje ją do config.properties.
     */
    public static void setLogoPath(String path) {
        props.setProperty("pdf.logo.path", path);
        saveProperties();
        logger.info("Zaktualizowano ścieżkę do logo PDF na: {}", path);
    }
}