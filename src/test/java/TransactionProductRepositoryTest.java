import org.example.database.TransactionProductRepository;
import org.example.sys.TransactionProduct;
import org.example.sys.TransactionProductId;
import org.junit.jupiter.api.*;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prosty test integracyjny dla TransactionProductRepository bez użycia Mockito.
 * Używa wbudowanej bazy H2 (in-memory), skonfigurowanej w persistence.xml pod persistence-unit "testPU".
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionProductRepositoryTest {

    private TransactionProductRepository repository;

    @BeforeAll
    void setUpAll() {
        // Tworzymy repozytorium z persistence-unit "testPU" (H2 in-memory)
        // W persistence.xml powinien być blok:
        // <persistence-unit name="testPU" transaction-type="RESOURCE_LOCAL">
        //   <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        //   <properties>
        //     <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"/>
        //     <property name="jakarta.persistence.jdbc.user" value="sa"/>
        //     <property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/>
        //     <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
        //   </properties>
        // </persistence-unit>
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU");
        repository = new TransactionProductRepository(emf);
    }

    @AfterAll
    void tearDownAll() {
        repository.close();
    }

    /**
     * Testuje dodanie i odczyt jednej pozycji.
     * Wybiera się prostą encję z kluczem złożonym i weryfikuje,
     * że po zapisie można ją znaleźć po id.
     */
    @Test
    void testDodajIZnajdzPoId() {
        // przygotuj encję
        TransactionProductId id = new TransactionProductId(100, 200);
        TransactionProduct tp = new TransactionProduct();
        tp.setId(id);
        tp.setQuantity(42);

        // kiedy: dodajemy
        repository.addTransactionProduct(tp);

        // weryfikujemy: możemy znaleźć tę samą pozycję
        TransactionProduct loaded = repository.findById(100, 200);
        assertNotNull(loaded, "Encja powinna być w bazie");
        assertEquals(42, loaded.getQuantity());
    }

    /**
     * Testuje, że pobieranie listy pozycji po transactionId działa.
     */
    @Test
    void testPobierzPoTransakcji() {
        // dodajemy dwie pozycje do tej samej transakcji
        TransactionProduct tp1 = new TransactionProduct();
        tp1.setId(new TransactionProductId(300, 10));
        tp1.setQuantity(5);
        repository.addTransactionProduct(tp1);

        TransactionProduct tp2 = new TransactionProduct();
        tp2.setId(new TransactionProductId(300, 20));
        tp2.setQuantity(7);
        repository.addTransactionProduct(tp2);

        // weryfikujemy
        List<TransactionProduct> list = repository.getByTransaction(300);
        assertEquals(2, list.size());
    }

    /**
     * Testuje aktualizację (merge) i usuwanie (remove).
     */
    @Test
    void testAktualizujIUsun() {
        TransactionProductId id = new TransactionProductId(400, 50);
        TransactionProduct tp = new TransactionProduct();
        tp.setId(id);
        tp.setQuantity(1);
        repository.addTransactionProduct(tp);

        // zmieniamy ilość i aktualizujemy
        tp.setQuantity(99);
        repository.updateTransactionProduct(tp);

        TransactionProduct reloaded = repository.findById(400, 50);
        assertEquals(99, reloaded.getQuantity());

        // usuwamy
        repository.removeTransactionProduct(tp);
        assertNull(repository.findById(400, 50));
    }

    /**
     * Testuje, że pobierzWszystkie zwraca wszystkie zapisy.
     */
    @Test
    void testPobierzWszystkie() {
        // czyści baza automatycznie przy create-drop, więc dodajemy jedną
        TransactionProduct tp = new TransactionProduct();
        tp.setId(new TransactionProductId(500, 60));
        tp.setQuantity(3);
        repository.addTransactionProduct(tp);

        List<TransactionProduct> all = repository.getAllTransactionProducts();
        assertTrue(all.stream().anyMatch(e ->
                e.getId().equals(new TransactionProductId(500, 60))
        ));
    }
}
