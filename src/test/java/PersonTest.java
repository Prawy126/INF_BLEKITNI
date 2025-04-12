import org.example.sys.Person;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void testParameterizedConstructor() {
        Person person = new Person("Jan", "Kowalski", 30, "ul. Testowa 1", "secret", "jan@example.com");

        // Porównanie wartości
        assertEquals("Jan", person.getName());
        assertEquals("Kowalski", person.getSurname());
        assertEquals(30, person.getAge());
        assertEquals("ul. Testowa 1", person.getAddress());
        assertEquals("secret", person.getPassword());
        assertEquals("jan@example.com", person.getEmail());
    }

    @Test
    void testMatchesPassword() {
        Person person = new Person();
        person.setPassword("correctPass");
        assertTrue(person.matchesPassword("correctPass"));
        assertFalse(person.matchesPassword("wrongPass"));
        assertFalse(person.matchesPassword(null));
        person.setPassword(null);
        assertFalse(person.matchesPassword("correctPass"));
        assertFalse(person.matchesPassword(null));
    }

    @Test
    void testMatchesEmail() {
        Person person = new Person();
        person.setEmail("test@example.com");

        assertTrue(person.matchesEmail("test@example.com"));
        assertFalse(person.matchesEmail("wrong@example.com"));
        assertFalse(person.matchesEmail(null));

        person.setEmail(null);
        assertFalse(person.matchesEmail("test@example.com"));
        assertFalse(person.matchesEmail(null));
    }

    @Test
    void testToString() {
        Person person = new Person("Anna", "Nowak", 25, "ul. Kwiatowa 5", "pass123", "anna@example.com");
        String result = person.toString();
        String expected = "Anna Nowak (25), ul. Kwiatowa 5, anna@example.com";
        assertEquals(expected, result);
    }

    @Test
    void testSettersAndGetters() {
        Person person = new Person();

        person.setName("Piotr");
        person.setSurname("Wiśniewski");
        person.setAge(40);
        person.setAddress("ul. Leśna 10");
        person.setPassword("newPass");
        person.setEmail("piotr@example.com");

        assertEquals("Piotr", person.getName());
        assertEquals("Wiśniewski", person.getSurname());
        assertEquals(40, person.getAge());
        assertEquals("ul. Leśna 10", person.getAddress());
        assertEquals("newPass", person.getPassword());
        assertEquals("piotr@example.com", person.getEmail());
    }

    @Test
    void testEdgeCasesForAge() {
        Person person = new Person();
        person.setAge(0);
        assertEquals(0, person.getAge());
        person.setAge(-1);
        assertEquals(-1, person.getAge());
    }
}