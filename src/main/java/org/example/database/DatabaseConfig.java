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
        loadConfig();
    }

    public static void loadConfig() {
        properties = new Properties();
        Path configPath = Paths.get(CONFIG_FILE);

        try {
            if (!Files.exists(configPath.getParent())) {
                Files.createDirectories(configPath.getParent());
            }

            if (Files.exists(configPath)) {
                try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                    properties.load(fis);
                }
            } else {
                properties.setProperty("db.host", "localhost");
                properties.setProperty("db.port", "3306");
                properties.setProperty("db.name", "StonkaDB");
                properties.setProperty("db.user", "root");
                properties.setProperty("db.password", "");

                saveConfig();
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas ładowania konfiguracji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveConfig() throws IOException {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Database Configuration");
        }
    }

    // Gettery
    public static String getMySqlServerUrl() {
        String host = properties.getProperty("db.host");
        String port = properties.getProperty("db.port");
        return "jdbc:mysql://" + host + ":" + port + "/?useSSL=false&serverTimezone=UTC";
    }

    public static String getMySqlDbUrl() {
        String host = properties.getProperty("db.host");
        String port = properties.getProperty("db.port");
        String dbName = properties.getProperty("db.name");
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&serverTimezone=UTC";
    }

    public static String getDbName() {
        return properties.getProperty("db.name");
    }

    public static String getDbUser() {
        return properties.getProperty("db.user");
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public static void updateConnectionParams(String host, String port, String dbName,
                                              String user, String password) throws IOException {
        properties.setProperty("db.host", host);
        properties.setProperty("db.port", port);
        properties.setProperty("db.name", dbName);
        properties.setProperty("db.user", user);
        properties.setProperty("db.password", password);
        saveConfig();
    }
}