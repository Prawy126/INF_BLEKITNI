/*
 * Classname: DatabaseInitializer
 * Version information: 1.1
 * Date: 2025-04-11
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer implements ILacz {

    public static void initialize() {
        // Połączenie do serwera MySQL (bez wskazania konkretnej bazy)
        try (Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, MYSQL_USER, MYSQL_PASSWORD)) {

            System.out.println("Tworzenie bazy danych: " + DB_NAME);

            // Tworzenie bazy danych
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

        } catch (Exception e) {
            System.out.println("Błąd podczas tworzenia bazy danych:");
            e.printStackTrace();
            return;
        }

        // Połączenie do właściwej bazy danych + import danych
        try (Connection conn = DriverManager.getConnection(MYSQL_DB_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            System.out.println("Importowanie pliku SQL...");
            executeSqlScript(conn, "src/main/resources/Stonka.sql");
            System.out.println("Baza danych gotowa.");
        } catch (Exception e) {
            System.out.println("Błąd podczas importowania danych:");
            e.printStackTrace();
        }
    }

    /**
     * Wykonuje skrypt SQL wczytany z pliku.
     *
     * @param conn     Połączenie z bazą danych
     * @param filePath Ścieżka do pliku SQL
     * @throws Exception w przypadku błędu odczytu lub wykonania zapytania
     */
    private static void executeSqlScript(Connection conn, String filePath) throws Exception {
        String sql = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] statements = sql.split(";");

        try (Statement stmt = conn.createStatement()) {
            for (String s : statements) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }
}
