/*
 * Classname: DatabaseInitializer
 * Version information: 1.0
 * Date: 2025-04-06
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Klasa odpowiedzialna za inicjalizację bazy danych.
 * Tworzy bazę danych oraz wykonuje skrypt SQL inicjalizacyjny.
 */
public class DatabaseInitializer {

    // Nazwa bazy danych
    private static final String DB_NAME = "StonkaDB";

    // URL połączenia z serwerem MySQL
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC";

    // Dane logowania do bazy danych
    private static final String USER = "root";
    private static final String PASS = "twoje_haslo";

    /**
     * Inicjalizuje bazę danych:
     * - tworzy bazę danych, jeśli nie istnieje
     * - wykonuje skrypt SQL z pliku
     */
    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(
                DB_URL,
                USER,
                PASS)) {

            System.out.println("Tworzenie bazy danych: " + DB_NAME);

            Statement stmt = conn.createStatement();

            // Tworzenie bazy danych jeśli jeszcze nie istnieje
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            // Ustawienie kontekstu na nowo utworzoną bazę
            stmt.execute("USE " + DB_NAME);

            System.out.println("Importowanie pliku SQL...");
            executeSqlScript(conn, "src/main/resources/Stonka.sql");

            System.out.println("Baza danych gotowa.");

        } catch (Exception e) {
            System.out.println("Błąd podczas tworzenia bazy danych:");
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
    private static void executeSqlScript(
            Connection conn,
            String filePath
    ) throws Exception {

        // Wczytaj cały plik SQL jako jeden łańcuch znaków
        String sql = new String(Files.readAllBytes(Paths.get(filePath)));

        // Podziel zapytania po średniku
        String[] statements = sql.split(";");

        try (Statement stmt = conn.createStatement()) {
            for (String s : statements) {
                String trimmed = s.trim(); // Usuń białe znaki
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed); // Wykonaj zapytanie jeśli nie jest puste
                }
            }
        }
    }
}
