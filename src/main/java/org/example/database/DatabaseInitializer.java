package org.example.database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer implements ILacz {

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_NAME, MYSQL_URL, MYSQL_PASSWORD)) {
            System.out.println("Tworzenie bazy danych: " + DB_NAME);
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.execute("USE " + DB_NAME);

            System.out.println("Importowanie pliku SQL...");
            executeSqlScript(conn, "src/main/resources/Stonka.sql");

            System.out.println("Baza danych gotowa.");
        } catch (Exception e) {
            System.out.println("Błąd podczas tworzenia bazy danych:");
            e.printStackTrace();
        }
    }

    private static void executeSqlScript(Connection conn, String filePath) throws Exception {
        String sql = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] statements = sql.split(";");

        try (Statement stmt = conn.createStatement()) {
            for (String s : statements) {
                s = s.trim();
                if (!s.isEmpty()) {
                    stmt.execute(s);
                }
            }
        }
    }
}
