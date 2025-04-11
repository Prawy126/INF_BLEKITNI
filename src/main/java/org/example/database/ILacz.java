package org.example.database;

//Maciej, Michał

public interface ILacz {

    static final String DB_NAME = "StonkaDB";
    static final String SQL_FILE = "src/main/resources/Stonka.sql";
    static final String MYSQL_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    static final String MYSQL_USER = "root";
    static final String MYSQL_PASSWORD = "twoje_haslo";
}
