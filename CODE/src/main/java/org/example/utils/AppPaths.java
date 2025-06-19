package org.example.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasa zarządzająca wszystkimi ścieżkami używanymi przez aplikację.
 */
public class AppPaths {
    // Unikaj użycia loggera w zmiennej statycznej - może powodować cykl inicjalizacji
    private static final String APP_NAME = "Stonka";

    // Nazwy folderów aplikacji
    private static final String LOGS_DIR = "logs";
    private static final String REPORTS_DIR = "reports";
    private static final String BACKUP_DIR = "backups";
    private static final String BACKUP_CSV_DIR = "backup-csv";
    private static final String CONFIG_DIR = "config";
    private static final String RESOURCES_DIR = "resources";

    // Zmienne ścieżek - wypełniane podczas inicjalizacji
    private static boolean initialized = false;
    private static boolean isInstalled = false;
    private static boolean isRunningFromJar = false;
    private static Path applicationHome;
    private static Path userDataDir;
    private static Path logsDir;
    private static Path reportsDir;
    private static Path backupDir;
    private static Path backupCsvDir;
    private static Path configDir;
    private static Path resourcesDir;

    /**
     * Inicjalizuje ścieżki aplikacji przed użyciem loggera.
     */
    static {
        try {
            initialize();
        } catch (Exception e) {
            System.err.println("BŁĄD podczas inicjalizacji AppPaths: "
                    + e.getMessage());
            e.printStackTrace();
            // Ustaw fallbackowe ścieżki
            applicationHome = Paths.get(".");
            userDataDir = Paths.get(".");
            logsDir = Paths.get("logs");
            reportsDir = Paths.get("reports");
            backupDir = Paths.get("backups");
            backupCsvDir = Paths.get("backup-csv");
            configDir = Paths.get("config");
            resourcesDir = Paths.get("resources");
        }

        // Ustaw właściwość systemową dla Log4j - PRZED utworzeniem loggera
        System.setProperty("app.logs.dir", logsDir.toString());
    }

    // Logger inicjalizowany po ustawieniu ścieżek
    private static final Logger logger = LogManager.getLogger(AppPaths.class);

    /**
     * Inicjalizuje ścieżki aplikacji.
     */
    private static void initialize() {
        if (initialized) return;

        System.out.println("Inicjalizacja ścieżek aplikacji");

        // 1. Wykryj tryb uruchomienia
        String classpath = System.getProperty("java.class.path");
        isRunningFromJar = classpath.toLowerCase().contains(".jar");

        try {
            // 2. Określ katalog domowy aplikacji
            if (isRunningFromJar) {
                String jarPath = AppPaths.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI().getPath();
                File jarFile = new File(jarPath);
                applicationHome = jarFile.getParentFile().toPath();

                // Sprawdź, czy jest zainstalowana
                isInstalled = applicationHome.toString()
                        .contains("Program Files")
                        || applicationHome.toString()
                        .contains("ProgramFiles")
                        || applicationHome.toString()
                        .contains("Program Files (x86)")
                        || applicationHome.toString().contains("/usr/local")
                        || applicationHome.toString().contains("/opt");
            } else {
                // Aplikacja działa z IDE
                applicationHome = Paths.get(System.getProperty("user.dir"));
            }

            // 3. Określ katalog danych użytkownika
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                String appData = System.getenv("LOCALAPPDATA");
                if (appData == null) {
                    appData = System.getProperty("user.home")
                            + "\\AppData\\Local";
                }
                userDataDir = Paths.get(appData, APP_NAME);
            }
            else if (os.contains("mac")) {
                userDataDir = Paths.get(System.getProperty("user.home"),
                        "Library", "Application Support", APP_NAME);
            }
            else {
                String xdgDataHome = System.getenv("XDG_DATA_HOME");
                if (xdgDataHome == null) {
                    xdgDataHome = System.getProperty("user.home")
                            + "/.local/share";
                }
                userDataDir = Paths.get(xdgDataHome, APP_NAME.toLowerCase());
            }

            // 4. Zdefiniuj wszystkie ścieżki
            if (isInstalled) {
                logsDir = userDataDir.resolve(LOGS_DIR);
                reportsDir = userDataDir.resolve(REPORTS_DIR);
                backupDir = userDataDir.resolve(BACKUP_DIR);
                backupCsvDir = userDataDir.resolve(BACKUP_CSV_DIR);
            } else {
                logsDir = applicationHome.resolve(LOGS_DIR);
                reportsDir = applicationHome.resolve(REPORTS_DIR);
                backupDir = applicationHome.resolve(BACKUP_DIR);
                backupCsvDir = applicationHome.resolve(BACKUP_CSV_DIR);
            }

            configDir = applicationHome.resolve(CONFIG_DIR);

            if (isRunningFromJar) {
                resourcesDir = applicationHome.resolve(RESOURCES_DIR);
            } else {
                resourcesDir = Paths.get("src/main/resources");
            }

            // 5. Utwórz wymagane katalogi
            createDirectory(logsDir);
            createDirectory(reportsDir);
            createDirectory(backupDir);
            createDirectory(backupCsvDir);
            createDirectory(configDir);
            createDirectory(resourcesDir);

            initialized = true;

            System.out.println("Ścieżki aplikacji zainicjalizowane " +
                    "pomyślnie:");
            System.out.println("Katalog domowy: " + applicationHome);
            System.out.println("Katalog danych użytkownika: " + userDataDir);
            System.out.println("Katalog logów: " + logsDir);
        }
        catch (Exception e) {
            System.err.println("BŁĄD podczas inicjalizacji ścieżek aplikacji: "
                    + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Inicjalizacja ścieżek nieudana", e);
        }
    }

    /**
     * Tworzy katalog, jeśli nie istnieje.
     */
    private static void createDirectory(Path dir) {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                System.out.println("Utworzono katalog: " + dir);
            }
        } catch (Exception e) {
            System.err.println("Nie można utworzyć katalogu: " + dir);
            e.printStackTrace();
        }
    }

    // Gettery dla ścieżek - nie wykonują już dodatkowej inicjalizacji

    public static Path getLogsDirectory() {
        return logsDir;
    }

    public static Path getReportsDirectory() {
        return reportsDir;
    }

    public static Path getBackupDirectory() {
        return backupDir;
    }

    public static Path getBackupCsvDirectory() {
        return backupCsvDir;
    }

    public static Path getConfigDirectory() {
        return configDir;
    }

    public static Path getResourcesDirectory() {
        return resourcesDir;
    }

    public static Path getConfigFile(String filename) {
        return configDir.resolve(filename);
    }

    public static Path getLogFile(String filename) {
        return logsDir.resolve(filename);
    }

    public static Path getReportFile(String filename) {
        return reportsDir.resolve(filename);
    }

    public static Path getBackupFile(String filename) {
        return backupDir.resolve(filename);
    }

    public static Path getBackupCsvFile(String filename) {
        return backupCsvDir.resolve(filename);
    }

    public static Path getResourceFile(String filename) {
        return resourcesDir.resolve(filename);
    }

    // Metody pomocnicze
    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean isInstalled() {
        return isInstalled;
    }
}