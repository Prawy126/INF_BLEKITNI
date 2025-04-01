package org.example.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class DatabaseInitializer {

    private static final String DB_NAME = "stonka";
    private static final String SQL_FILE = "src/main/resources/Stonka.sql";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";

    public static void initialize() {
        try {
            // 1. Połączenie z serwerem MySQL
            Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
            Statement statement = connection.createStatement();

            // 2. Tworzenie bazy jeśli nie istnieje
            System.out.println("Tworzenie bazy danych: " + DB_NAME);
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            statement.close();
            connection.close();

            // 3. Połączenie z utworzoną bazą danych
            Connection dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&serverTimezone=UTC", MYSQL_USER, MYSQL_PASSWORD);
            Statement dbStatement = dbConnection.createStatement();

            // 4. Wczytywanie zapytań z pliku SQL
            System.out.println("Importowanie pliku SQL: " + SQL_FILE);
            BufferedReader reader = new BufferedReader(new FileReader(SQL_FILE));
            StringBuilder queryBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                queryBuilder.append(line);
                if (line.trim().endsWith(";")) {
                    dbStatement.execute(queryBuilder.toString());
                    queryBuilder.setLength(0);
                }
            }

            reader.close();
            dbStatement.close();
            dbConnection.close();

            System.out.println("Baza danych została załadowana pomyślnie!");

        } catch (Exception e) {
            System.err.println("Błąd podczas tworzenia bazy danych:");
            e.printStackTrace();
        }
    }
}
