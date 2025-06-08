package org.example.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class DatabaseInitializer implements ILacz {

    /**
     * Inicjalizuje bazę danych.
     */
    public static void initialize() {
        System.out.println("[DB-INIT] Rozpoczynam inicjalizację bazy danych");

        // Upewniamy się, że konfiguracja jest załadowana
        DatabaseConfig.loadConfig();

        DatabaseInitializer initializer = new DatabaseInitializer();
        initializer.createDatabaseIfNotExists();
        initializer.executeStructureScript();
        //initializer.executeDataScript();

        System.out.println("[DB-INIT] Inicjalizacja bazy danych zakończona pomyślnie");
    }

    /**
     * Tworzy bazę danych, jeśli nie istnieje.
     */
    private void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getMySqlServerUrl(),
                DatabaseConfig.getDbUser(),
                DatabaseConfig.getDbPassword())) {

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DatabaseConfig.getDbName());

        } catch (SQLException e) {
            System.err.println("Błąd podczas tworzenia bazy danych: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Wykonuje skrypt struktury bazy danych.
     */
    private void executeStructureScript() {
        executeScript(STRUKTURA_SQL_FILE);
    }

    /**
     * Wykonuje skrypt danych początkowych.
     */
    private void executeDataScript() {
        executeScript(DANE_SQL_FILE);
    }

    /**
     * Wykonuje skrypt SQL z pliku.
     */
    private void executeScript(String filename) {
        try (Connection conn = DriverManager.getConnection(
                getMySqlDbUrl(), getMySqlUser(), getMySqlPassword())) {

            String script = loadSqlScript(filename);
            Statement stmt = conn.createStatement();

            // Dzielenie skryptu na pojedyncze instrukcje i wykonanie ich
            for (String statement : script.split(";")) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }

        } catch (SQLException e) {
            System.err.println("Błąd podczas wykonywania skryptu " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ładuje skrypt SQL z zasobów.
     */
    private String loadSqlScript(String filename) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            return reader.lines().collect(Collectors.joining("\n"));

        } catch (Exception e) {
            System.err.println("Błąd podczas ładowania skryptu " + filename + ": " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}