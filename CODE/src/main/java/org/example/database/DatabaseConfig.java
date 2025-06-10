package org.example.database;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "config/database.properties";
    static Properties properties = new Properties();

    static {
        System.out.println("[DB-CONFIG] Inicjalizacja klasy DatabaseConfig - " +
                "rozpoczęcie ładowania konfiguracji");
        loadConfig();
    }

    public static void loadConfig() {
        System.out.println("[DB-CONFIG] Rozpoczęcie ładowania konfiguracji z: "
                + CONFIG_FILE);
        properties = new Properties();
        Path configPath = Paths.get(CONFIG_FILE);

        try {
            if (!Files.exists(configPath.getParent())) {
                System.out.println("[DB-CONFIG] Katalog konfiguracyjny nie " +
                        "istnieje, tworzę: " + configPath.getParent());
                Files.createDirectories(configPath.getParent());
            }

            if (Files.exists(configPath)) {
                System.out.println("[DB-CONFIG] Plik konfiguracyjny istnieje," +
                        " ładuję istniejącą konfigurację");
                try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                    properties.load(fis);
                    System.out.println("[DB-CONFIG] Załadowano konfigurację: " +
                            "db.host=" + properties.getProperty("db.host")
                            + ", db.port=" + properties.getProperty("db.port")
                            + ", db.name=" + properties.getProperty("db.name")
                            + ", db.user=" + properties.getProperty("db.user")
                            + ", db.password=" +
                            (properties.getProperty("db.password").isEmpty() ?
                                    "[PUSTE]" : "[USTAWIONE]"));
                }
            } else {
                System.out.println("[DB-CONFIG] Plik konfiguracyjny nie " +
                        "istnieje, tworzę domyślną konfigurację");
                properties.setProperty("db.host", "localhost");
                properties.setProperty("db.port", "3306");
                properties.setProperty("db.name", "StonkaDB");
                properties.setProperty("db.user", "root");
                properties.setProperty("db.password", "");
                System.out.println("[DB-CONFIG] Ustawiono domyślną " +
                        "konfigurację: db.host=localhost, db.port=3306, " +
                        "db.name=StonkaDB, db.user=root, " +
                        "db.password=[USTAWIONE]");

                saveConfig();
            }

            // Usunięto wywołanie generatora persistence.xml
            System.out.println("[DB-CONFIG] Konfiguracja bazy danych gotowa " +
                    "do użycia przez EMFProvider");

        } catch (IOException e) {
            System.err.println("[DB-CONFIG] BŁĄD podczas " +
                    "ładowania konfiguracji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveConfig() throws IOException {
        System.out.println("[DB-CONFIG] Zapisywanie konfiguracji do " +
                "pliku: " + CONFIG_FILE);
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Database Configuration");
            System.out.println("[DB-CONFIG] Konfiguracja zapisana pomyślnie");

            // Usunięto wywołanie generatora persistence.xml
            System.out.println("[DB-CONFIG] Zmiany konfiguracji będą " +
                    "zastosowane przy następnym użyciu EMFProvider");

            // Opcjonalnie: resetuj EMFProvider aby zastosować nowe ustawienia
            if (EMFProvider.isInitialized()) {
                System.out.println("[DB-CONFIG] Resetowanie " +
                        "EntityManagerFactory aby zastosować nowe ustawienia");
                EMFProvider.close();
            }

        } catch (IOException e) {
            System.err.println("[DB-CONFIG] BŁĄD podczas zapisywania " +
                    "konfiguracji: " + e.getMessage());
            throw e;
        }
    }

    public static String getMySqlServerUrl() {
        String host = properties.getProperty("db.host");
        String port = properties.getProperty("db.port");
        String url = "jdbc:mysql://" + host + ":" + port + "/?allowPublic" +
                "KeyRetrieval=true&useSSL=false&serverTimezone=UTC";
        System.out.println("[DB-CONFIG] Wygenerowano URL serwera: " + url);
        return url;
    }

    public static String getMySqlDbUrl() {
        String host = properties.getProperty("db.host");
        String port = properties.getProperty("db.port");
        String dbName = properties.getProperty("db.name");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
        System.out.println("[DB-CONFIG] Wygenerowano URL bazy danych: " + url);
        return url;
    }

    public static String getDbName() {
        String dbName = properties.getProperty("db.name");
        System.out.println("[DB-CONFIG] Pobrano nazwę bazy " +
                "danych: " + dbName);
        return dbName;
    }

    public static String getDbUser() {
        String user = properties.getProperty("db.user");
        System.out.println("[DB-CONFIG] Pobrano użytkownika" +
                " bazy danych: " + user);
        return user;
    }

    public static String getDbPassword() {
        String password = properties.getProperty("db.password");
        System.out.println("[DB-CONFIG] Pobrano hasło bazy danych: " +
                (password.isEmpty() ? "[PUSTE]" : "[USTAWIONE]"));
        return password;
    }

    public static void updateConnectionParams(String host, String port,
                                              String dbName,
                                              String user,
                                              String password
    ) throws IOException {
        System.out.println("[DB-CONFIG] Aktualizacja parametrów połączenia:");
        System.out.println("[DB-CONFIG]   - Host: " + host);
        System.out.println("[DB-CONFIG]   - Port: " + port);
        System.out.println("[DB-CONFIG]   - DB Name: " + dbName);
        System.out.println("[DB-CONFIG]   - User: " + user);
        System.out.println("[DB-CONFIG]   - Password: " + (password.isEmpty() ?
                "[PUSTE]" : "[USTAWIONE]"));

        properties.setProperty("db.host", host);
        properties.setProperty("db.port", port);
        properties.setProperty("db.name", dbName);
        properties.setProperty("db.user", user);
        properties.setProperty("db.password", password);
        saveConfig();
    }
}