/*
 * Classname: ILacz
 * Version information: 1.2
 * Date: 2025-06-07
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

/**
 * Interfejs definiujący parametry połączenia do bazy oraz
 * metodę do wczytywania i wykonywania skryptu SQL.
 */
public interface ILacz {

    /**
     * Nazwa pliku ze strukturą bazy danych
     */
    String STRUKTURA_SQL_FILE = "Struktura.sql";

    /**
     * Nazwa pliku z danymi do bazy danych
     */
    String DANE_SQL_FILE = "Dane.sql";

    /**
     * URL do serwera MySQL (bez wskazania konkretnej bazy)
     */
    default String getMySqlServerUrl() {
        return DatabaseConfig.getMySqlServerUrl();
    }

    /**
     * URL do połączenia z docelową bazą danych
     */
    default String getMySqlDbUrl() {
        return DatabaseConfig.getMySqlDbUrl();
    }

    /**
     * Nazwa bazy danych
     */
    default String getDbName() {
        return DatabaseConfig.getDbName();
    }

    /**
     * Użytkownik bazy MySQL
     */
    default String getMySqlUser() {
        return DatabaseConfig.getDbUser();
    }

    /**
     * Hasło użytkownika bazy MySQL
     */
    default String getMySqlPassword() {
        return DatabaseConfig.getDbPassword();
    }
}