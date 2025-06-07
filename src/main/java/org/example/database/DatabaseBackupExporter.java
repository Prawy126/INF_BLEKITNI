/*
 * Classname: DatabaseBackupExporter
 * Version information: 1.1
 * Date: 2025-06-07
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBackupExporter implements ILacz {
    private static final Logger logger
            = LogManager.getLogger(DatabaseBackupExporter.class);

    /**
     * Eksportuje wszystkie tabele z bazy danych do
     * plików CSV w określonym folderze.
     *
     * @param outputFolder Ścieżka do folderu,
     *                     w którym mają zostać zapisane pliki CSV
     * @throws SQLException Jeśli wystąpi błąd podczas dostępu do bazy danych
     * @throws IOException Jeśli wystąpi błąd podczas zapisu do pliku
     */
    public static void exportAllTablesToCsv(String outputFolder)
            throws SQLException,
            IOException {
        // Utworzenie instancji do dostępu do parametrów połączenia
        DatabaseBackupExporter exporter = new DatabaseBackupExporter();

        // Utwórz folder jeśli nie istnieje
        File folder = new File(outputFolder);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Nie można utworzyć katalogu: " +
                    outputFolder);
        }

        try (Connection conn = DriverManager.getConnection(
                exporter.getMySqlDbUrl(),
                exporter.getMySqlUser(),
                exporter.getMySqlPassword())
        ) {
            logger.info("Rozpoczynanie eksportu " +
                    "bazy danych do plików CSV w folderze: {}", outputFolder);

            // Pobierz listę wszystkich tabel w bazie danych
            List<String> tables = getAllTables(conn, exporter.getDbName());

            // Eksportuj każdą tabelę do osobnego pliku CSV
            for (String table : tables) {
                try {
                    if (tableExists(conn, table)) {
                        exportTableToCsv(conn, table, folder);
                    } else {
                        logger.warn("Tabela {} nie istnieje," +
                                " pomijam eksport", table);
                    }
                } catch (Exception e) {
                    logger.error("Błąd podczas eksportu tabeli {}:" +
                            " {}", table, e.getMessage(), e);
                }
            }

            logger.info("Zakończono eksport. " +
                            "Wyeksportowano {} z {} tabel.",
                    tables.size() - countErrors, tables.size());
        }
    }

    private static int countErrors = 0;

    /**
     * Pobiera listę wszystkich tabel w bazie danych.
     *
     * @param conn Połączenie z bazą danych
     * @param dbName Nazwa bazy danych
     * @return Lista nazw tabel
     * @throws SQLException Jeśli wystąpi błąd SQL
     */
    private static List<String> getAllTables(Connection conn, String dbName)
            throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData meta = conn.getMetaData();

        try (ResultSet rs = meta.getTables(dbName,
                null, "%",
                new String[] {"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }

        logger.debug("Znaleziono tabele: {}", tables);
        return tables;
    }

    /**
     * Sprawdza czy tabela o podanej nazwie istnieje w bazie danych.
     *
     * @param conn Połączenie z bazą danych
     * @param tableName Nazwa tabeli
     * @return true jeśli tabela istnieje, false w przeciwnym przypadku
     */
    private static boolean tableExists(Connection conn, String tableName) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null,
                    tableName, new String[] {"TABLE"})) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Błąd podczas sprawdzania istnienia tabeli" +
                    " {}: {}", tableName, e.getMessage());
            return false;
        }
    }

    /**
     * Eksportuje pojedynczą tabelę do pliku CSV.
     *
     * @param conn Połączenie z bazą danych
     * @param tableName Nazwa tabeli do eksportu
     * @param outputFolder Folder docelowy
     * @throws SQLException Jeśli wystąpi błąd SQL
     * @throws IOException Jeśli wystąpi błąd zapisu pliku
     */
    private static void exportTableToCsv(
            Connection conn,
            String tableName,
            File outputFolder
    ) throws SQLException,
            IOException {
        String csvFilePath = new File(outputFolder,
                tableName + ".csv").getAbsolutePath();
        logger.info("Eksportowanie tabeli {} do pliku {}",
                tableName, csvFilePath);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
             FileWriter writer = new FileWriter(csvFilePath)) {

            // Najpierw zapisz nagłówki kolumn
            writeColumnHeaders(rs, writer);

            // Następnie zapisz dane
            writeDataRows(rs, writer);

            logger.debug("Pomyślnie wyeksportowano tabelę {}",
                    tableName);
        } catch (SQLException e) {
            countErrors++;
            logger.error("Błąd SQL podczas eksportu tabeli" +
                    " {}: {}", tableName, e.getMessage());
            throw e;
        } catch (IOException e) {
            countErrors++;
            logger.error("Błąd IO podczas eksportu tabeli" +
                    " {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Zapisuje nagłówki kolumn do pliku CSV.
     *
     * @param rs ResultSet zawierający dane tabeli
     * @param writer Writer do pliku CSV
     * @throws SQLException Jeśli wystąpi błąd SQL
     * @throws IOException Jeśli wystąpi błąd zapisu pliku
     */
    private static void writeColumnHeaders(ResultSet rs,
                                           FileWriter writer
    ) throws SQLException, IOException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        StringBuilder headerLine = new StringBuilder();
        for (int i = 1; i <= columnCount; i++) {
            headerLine.append(meta.getColumnName(i));
            if (i < columnCount) {
                headerLine.append(",");
            }
        }
        writer.write(headerLine.toString());
        writer.write("\n");
    }

    /**
     * Zapisuje wiersze danych do pliku CSV.
     *
     * @param rs ResultSet zawierający dane tabeli
     * @param writer Writer do pliku CSV
     * @throws SQLException Jeśli wystąpi błąd SQL
     * @throws IOException Jeśli wystąpi błąd zapisu pliku
     */
    private static void writeDataRows
    (ResultSet rs,
     FileWriter writer
    ) throws SQLException,
            IOException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        while (rs.next()) {
            StringBuilder line = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                if (value != null) {
                    String stringValue = value.toString()
                            .replace("\"", "\"\"")
                            .replace("\n", "\\n")
                            .replace("\r", "\\r");

                    if (stringValue.contains(",")
                            || stringValue.contains("\"")) {
                        line.append("\"").append(stringValue).append("\"");
                    } else {
                        line.append(stringValue);
                    }
                }
                if (i < columnCount) {
                    line.append(",");
                }
            }
            writer.write(line.toString());
            writer.write("\n");
        }
    }
}