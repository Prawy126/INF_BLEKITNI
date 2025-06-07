package org.example.database;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generator pliku persistence.xml na podstawie konfiguracji bazy danych.
 */
public class PersistenceConfigGenerator {

    private static final String PERSISTENCE_PATH = "src/main/resources/META-INF/persistence.xml";

    /**
     * Generuje plik persistence.xml na podstawie aktualnej konfiguracji.
     */
    public static void generatePersistenceXml() throws IOException {
        String persistenceXml = """
            <persistence xmlns="https://jakarta.ee/xml/ns/persistence"
                         version="3.1">
                <persistence-unit name="myPU">
                    <properties>
                        <property name="jakarta.persistence.jdbc.url"
                                  value="jdbc:mysql://%s:%s/%s"/>
                        <property name="jakarta.persistence.jdbc.user" value="%s"/>
                        <property name="jakarta.persistence.jdbc.password" value="%s"/>
            
                        <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
                        <property name="hibernate.hbm2ddl.auto" value="update"/>
                    </properties>
                </persistence-unit>
            </persistence>
            """.formatted(
                DatabaseConfig.properties.getProperty("db.host"),
                DatabaseConfig.properties.getProperty("db.port"),
                DatabaseConfig.properties.getProperty("db.name"),
                DatabaseConfig.properties.getProperty("db.user"),
                DatabaseConfig.properties.getProperty("db.password")
        );

        Path path = Paths.get(PERSISTENCE_PATH);
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(persistenceXml);
        }
    }
}
