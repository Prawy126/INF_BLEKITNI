import org.example.database.DatabaseConfig;
import org.example.database.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseInitializerTest {

    @BeforeAll
    static void setUp() {
        // Możemy tutaj ustawić specjalną konfigurację testową
        // np. używając bazy H2 zamiast MySQL
    }

    @Test
    @Order(1)
    void testInitialize_createsDatabaseAndTables() throws Exception {
        //uruchomienie inicjalizacji
        DatabaseInitializer.initialize();

        //połączenie z utworzoną bazą
        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getMySqlDbUrl(),
                DatabaseConfig.getDbUser(),
                DatabaseConfig.getDbPassword())) {

            Statement stmt = conn.createStatement();

            // Sprawdzenie czy jakaś przykładowa tabela istnieje
            ResultSet rs = stmt.executeQuery("SELECT * FROM Produkty");
            assertTrue(rs.next(), "Tabela 'produkty' powinna " +
                    "istnieć po inicjalizacji.");
        }
    }

    @Test
    @Order(2)
    void testInitialize_isIdempotent() {
        assertDoesNotThrow(DatabaseInitializer::initialize,
                "Ponowne uruchomienie initialize() nie powinno rzucać wyjątków.");
    }
}