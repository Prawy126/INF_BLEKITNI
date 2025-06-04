/*
 * Classname: DatabaseInitializer
 * Version information: 1.3
 * Date: 2025-06-02
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class DatabaseInitializer implements ILacz {
    private static final Logger logger
            = LogManager.getLogger(DatabaseInitializer.class);

    /**
     * Tworzy bazę danych (jeśli nie istnieje)
     * i importuje do niej struktury oraz dane
     * na podstawie plików SQL.
     * <p>
     * Najpierw łączy się z serwerem MySQL bez wskazania konkretnej bazy,
     * tworzy bazę (jeśli nie istnieje), a następnie:
     * 1. Tworzy strukturę tabel (jeśli nie istnieją)
     * 2. Wstawia dane początkowe (tylko jeśli tabela Pracownicy jest pusta)
     * </p>
     */
    public static void initialize() {
        logger.info("Rozpoczynam inicjalizację bazy danych");

        try {
            // Krok 1: Sprawdź czy baza istnieje, jeśli nie - utwórz ją
            createDatabaseIfNotExists();

            // Krok 2: Połącz się z bazą i utwórz strukturę
            // tabel (jeśli nie istnieją)
            createTablesStructure();

            // Krok 3: Wstaw dane początkowe (tylko jeśli tabela
            // Pracownicy jest pusta)
            insertInitialData();

            logger.info("Inicjalizacja bazy danych zakończona pomyślnie");
        } catch (Exception e) {
            logger.error("Błąd podczas inicjalizacji bazy danych:" +
                    " {}", e.getMessage(), e);
            throw new RuntimeException("Błąd inicjalizacji bazy danych", e);
        }
    }

    /**
     * Sprawdza czy baza danych istnieje, a jeśli nie - tworzy ją.
     *
     * @throws SQLException gdy wystąpi błąd podczas połączenia lub tworzenia bazy
     */
    private static void createDatabaseIfNotExists() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                MYSQL_SERVER_URL,
                MYSQL_USER,
                MYSQL_PASSWORD)
        ) {
            logger.debug("Połączenie do serwera MySQL nawiązane: {}",
                    MYSQL_SERVER_URL);

            boolean databaseExists = databaseExists(conn, DB_NAME);

            if (!databaseExists) {
                logger.info("Baza danych '{}' nie istnieje, tworzę...",
                        DB_NAME);
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " +
                            DB_NAME);
                    logger.info("Baza danych '{}' została utworzona",
                            DB_NAME);
                }
            } else {
                logger.info("Baza danych '{}' już istnieje", DB_NAME);
            }
        }
    }

    /**
     * Tworzy strukturę tabel w bazie danych na podstawie pliku Struktura.sql.
     *
     * @throws Exception gdy wystąpi błąd podczas wykonywania skryptu SQL
     */
    private static void createTablesStructure() throws Exception {
        try (Connection conn = DriverManager.getConnection(MYSQL_DB_URL,
                MYSQL_USER,
                MYSQL_PASSWORD)
        ) {
            logger.debug("Połączenie do bazy danych '{}' nawiązane",
                    DB_NAME);

            // Sprawdź czy tabela Pracownicy już istnieje
            boolean tablesExist = tableExists(conn, "Pracownicy");

            if (!tablesExist) {
                logger.info("Tabele nie istnieją, tworzę strukturę...");
                executeSqlScript(conn, STRUKTURA_SQL_FILE);
                logger.info("Struktura tabel została utworzona");
            } else {
                logger.info("Struktura tabel już istnieje, pomijam tworzenie");
            }
        } catch (SQLException e) {
            logger.error("Błąd podczas tworzenia struktury tabel: {}",
                    e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Wstawia dane początkowe do bazy danych na podstawie pliku Dane.sql,
     * ale tylko jeśli tabela Pracownicy jest pusta.
     *
     * @throws Exception gdy wystąpi błąd podczas wykonywania skryptu SQL
     */
    private static void insertInitialData() throws Exception {
        try (Connection conn = DriverManager.getConnection(
                MYSQL_DB_URL,
                MYSQL_USER,
                MYSQL_PASSWORD)
        ) {
            logger.debug("Połączenie do bazy danych '{}'" +
                    " nawiązane do wstawienia danych", DB_NAME);

            // Sprawdź czy tabela Pracownicy zawiera jakiekolwiek dane
            boolean hasData = false;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) " +
                         "FROM Pracownicy")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasData = true;
                }
            } catch (SQLException e) {
                // Jeśli tabela nie istnieje lub wystąpił inny błąd,
                // zakładamy że nie ma danych
                logger.warn("Błąd podczas sprawdzania danych" +
                        " w tabeli Pracownicy: {}", e.getMessage());
            }

            if (!hasData) {
                logger.info("Tabela Pracownicy jest pusta, " +
                        "wstawiam dane początkowe...");
                try {
                    executeSqlScript(conn, DANE_SQL_FILE);
                    logger.info("Dane początkowe zostały wstawione");
                } catch (SQLException e) {
                    logger.error("Błąd podczas wstawiania danych: {}",
                            e.getMessage(), e);
                    throw e;
                }
            } else {
                logger.info("Tabela Pracownicy już zawiera dane," +
                        " pomijam wstawianie danych");
            }
        } catch (Exception e) {
            logger.error("Błąd podczas inicjalizacji danych:" +
                    " {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Sprawdza czy baza danych o podanej nazwie istnieje.
     *
     * @param conn połączenie z serwerem bazy danych
     * @param dbName nazwa bazy danych
     * @return true jeśli baza istnieje, false w przeciwnym przypadku
     * @throws SQLException gdy wystąpi błąd podczas sprawdzania
     */
    private static boolean databaseExists(
            Connection conn,
            String dbName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getCatalogs()) {
            while (rs.next()) {
                if (dbName.equals(rs.getString(1))) return true;
            }
        }
        return false;
    }

    /**
     * Sprawdza czy tabela o podanej nazwie istnieje w bazie danych.
     *
     * @param conn połączenie z bazą danych
     * @param tableName nazwa tabeli
     * @return true jeśli tabela istnieje, false w przeciwnym przypadku
     * @throws SQLException gdy wystąpi błąd podczas sprawdzania
     */
    private static boolean tableExists(
            Connection conn,
            String tableName
    ) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null,
                null, tableName, new String[] {"TABLE"})) {
            return rs.next();
        }
    }

    /**
     * Wczytuje i wykonuje kolejne polecenia SQL zawarte w
     * podanym pliku.
     *
     * @param conn     aktywne połączenie do bazy,
     *                w której mają zostać wykonane zapytania
     * @param filePath ścieżka do pliku SQL zawierającego
     *                skrypt DDL lub DML
     * @throws Exception gdy odczyt pliku lub wykonanie
     * zapytań zakończy się niepowodzeniem
     */
    private static void executeSqlScript(
            Connection conn,
            String filePath
    ) throws Exception {
        logger.debug("Wczytywanie skryptu SQL z zasobów:" +
                " {}", filePath);
        try (InputStream is
                     = DatabaseInitializer
                .class
                .getResourceAsStream("/" + filePath)) {
            if (is == null) {
                throw new FileNotFoundException("Plik " +
                        filePath + " nie został znaleziony w zasobach!");
            }
            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            String[] statements = sql.split(";");

            try (Statement stmt = conn.createStatement()) {
                for (String s : statements) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) {
                        try {
                            logger.trace("Wykonuję zapytanie SQL: {}",
                                    trimmed.replaceAll("\\s+",
                                            " "));
                            stmt.execute(trimmed);
                        } catch (SQLException e) {
                            // Obsługa konkretnych błędów SQL
                            if (e.getMessage().contains("Duplicate column")
                                    || e.getMessage()
                                    .contains("Duplicate entry")
                                    || e.getMessage()
                                    .contains("already exists")) {
                                logger.warn("Ignoruję błąd duplikatu:" +
                                        " {}", e.getMessage());
                            } else {
                                // Dla innych błędów rzucamy wyjątek
                                throw e;
                            }
                        }
                    }
                }
            }
        }
        logger.debug("Wykonano wszystkie polecenia " +
                "SQL z pliku {}", filePath);
    }
}