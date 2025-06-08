package org.example.database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.sql.DataSource;
import java.net.URL;
import java.util.*;

/**
 * Dostawca fabryki EntityManagerFactory dla całej aplikacji.
 * Implementuje wzorzec Singleton z leniwą inicjalizacją.
 * Używa programowej konfiguracji zamiast persistence.xml.
 */
public class EMFProvider {

    private static EntityManagerFactory emf = null;

    public static boolean isInitialized() {
        return emf != null && emf.isOpen();
    }

    /**
     * Zwraca instancję EntityManagerFactory.
     * Przy pierwszym wywołaniu tworzy instancję programowo.
     *
     * @return współdzielona instancja EntityManagerFactory
     */
    public static EntityManagerFactory get() {
        if (emf == null) {
            System.out.println("[EMF] Tworzenie EntityManagerFactory programowo");
            try {
                // Utwórz mapę właściwości połączenia
                Map<String, String> properties = new HashMap<>();

                // Konfiguracja połączenia
                String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        DatabaseConfig.properties.getProperty("db.host"),
                        DatabaseConfig.properties.getProperty("db.port"),
                        DatabaseConfig.properties.getProperty("db.name"));

                properties.put("jakarta.persistence.jdbc.url", jdbcUrl);
                properties.put("jakarta.persistence.jdbc.user", DatabaseConfig.properties.getProperty("db.user"));
                properties.put("jakarta.persistence.jdbc.password", DatabaseConfig.properties.getProperty("db.password"));
                properties.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");

                // Konfiguracja Hibernate
                properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
                properties.put("hibernate.hbm2ddl.auto", "update");
                properties.put("hibernate.show_sql", "false");
                properties.put("hibernate.format_sql", "false");

                // Utwórz własną implementację PersistenceUnitInfo
                PersistenceUnitInfo persistenceUnitInfo = new PersistenceUnitInfo() {
                    @Override
                    public String getPersistenceUnitName() {
                        return "myPU";
                    }

                    @Override
                    public String getPersistenceProviderClassName() {
                        return HibernatePersistenceProvider.class.getName();
                    }

                    @Override
                    public PersistenceUnitTransactionType getTransactionType() {
                        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
                    }

                    @Override
                    public DataSource getJtaDataSource() {
                        return null;
                    }

                    @Override
                    public DataSource getNonJtaDataSource() {
                        return null;
                    }

                    @Override
                    public List<String> getMappingFileNames() {
                        return Collections.emptyList();
                    }

                    @Override
                    public List<URL> getJarFileUrls() {
                        return Collections.emptyList();
                    }

                    @Override
                    public URL getPersistenceUnitRootUrl() {
                        return null;
                    }

                    @Override
                    public List<String> getManagedClassNames() {
                        // Lista wszystkich klas encyjnych w projekcie
                        List<String> managedClasses = new ArrayList<>();

                        // Dodaj tutaj wszystkie klasy encyjne, które używasz w projekcie
                        managedClasses.add("org.example.sys.Employee");
                        managedClasses.add("org.example.sys.Person");
                        managedClasses.add("org.example.sys.Admin");
                        managedClasses.add("org.example.sys.Manager");
                        managedClasses.add("org.example.sys.Cashier");
                        managedClasses.add("org.example.sys.Logistician");
                        managedClasses.add("org.example.sys.Address");
                        managedClasses.add("org.example.sys.Product");
                        managedClasses.add("org.example.sys.Warehouse");
                        managedClasses.add("org.example.sys.Transaction");
                        managedClasses.add("org.example.sys.TransactionProduct");
                        managedClasses.add("org.example.sys.TransactionProductId");
                        managedClasses.add("org.example.sys.AbsenceRequest");
                        managedClasses.add("org.example.sys.EmpTask");
                        managedClasses.add("org.example.sys.TaskEmployee");
                        managedClasses.add("org.example.sys.TaskEmployeeId");
                        managedClasses.add("org.example.sys.TechnicalIssue");
                        managedClasses.add("org.example.sys.Report");
                        managedClasses.add("org.example.sys.Order");
                        managedClasses.add("org.example.sys.PasswordResetToken");

                        // dodaj pozostałe klasy encyjne, które używasz

                        return managedClasses;
                    }

                    @Override
                    public boolean excludeUnlistedClasses() {
                        return false;
                    }

                    @Override
                    public SharedCacheMode getSharedCacheMode() {
                        return SharedCacheMode.UNSPECIFIED;
                    }

                    @Override
                    public ValidationMode getValidationMode() {
                        return ValidationMode.AUTO;
                    }

                    @Override
                    public Properties getProperties() {
                        Properties props = new Properties();
                        props.putAll(properties);
                        return props;
                    }

                    @Override
                    public String getPersistenceXMLSchemaVersion() {
                        return "3.1";
                    }

                    @Override
                    public ClassLoader getClassLoader() {
                        return Thread.currentThread().getContextClassLoader();
                    }

                    @Override
                    public void addTransformer(ClassTransformer classTransformer) {
                        // Nie implementujemy transformacji klas
                    }

                    @Override
                    public ClassLoader getNewTempClassLoader() {
                        return null;
                    }
                };

                // Utwórz EntityManagerFactory
                HibernatePersistenceProvider provider = new HibernatePersistenceProvider();
                emf = provider.createContainerEntityManagerFactory(persistenceUnitInfo, properties);

                System.out.println("[EMF] EntityManagerFactory utworzone pomyślnie");
            } catch (Exception e) {
                System.err.println("[EMF] BŁĄD podczas tworzenia EntityManagerFactory: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
        return emf;
    }

    /**
     * Zamyka EntityManagerFactory.
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            System.out.println("[EMF] Zamykanie EntityManagerFactory");
            emf.close();
            emf = null;
        }
    }
}