package org.example.database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer implements ILacz {

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, MYSQL_USER, MYSQL_PASSWORD)) {

            System.out.println("Tworzenie bazy danych: " + DB_NAME);
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.close(); // zamykamy ten statement

        } catch (Exception e) {
            System.out.println("Błąd podczas tworzenia bazy danych:");
            e.printStackTrace();
            return;
        }

        try (Connection conn = DriverManager.getConnection(MYSQL_DB_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            System.out.println("Importowanie pliku SQL...");
            executeSqlScript(conn, SQL_FILE);
            System.out.println("Baza danych gotowa.");
        } catch (Exception e) {
            System.out.println("Błąd podczas importowania danych:");
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
