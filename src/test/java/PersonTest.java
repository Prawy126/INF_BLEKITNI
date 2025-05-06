import org.example.sys.Person;
import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void testParameterizedConstructor() {
        try {
            Person person = new Person("Jan", "Kowalski", 30, "jan@example.com");

            assertEquals("Jan", person.getName());
            assertEquals("Kowalski", person.getSurname());
            assertEquals(30, person.getAge());
            assertEquals("jan@example.com", person.getEmail());
        } catch (Exception e) {
            fail("Wyjątek w konstruktorze: " + e.getMessage());
        }
    }

    @Test
    void testToString() throws Exception {
        Person person = new Person("Anna", "Nowak", 25, "anna@example.com");
        String expected = "Anna Nowak (25), anna@example.com";
        assertEquals(expected, person.toString());
    }

    @Test
    void testSettersAndGetters() throws Exception {
        Person person = new Person();

        person.setName("Piotr");
        person.setSurname("Wiśniewski");
        person.setAge(40);
        person.setEmail("piotr@example.com");

        assertEquals("Piotr", person.getName());
        assertEquals("Wiśniewski", person.getSurname());
        assertEquals(40, person.getAge());
        assertEquals("piotr@example.com", person.getEmail());
    }

    @Test
    void testSetNameInvalid() {
        Person person = new Person();
        assertThrows(NameException.class, () -> person.setName(""));
        assertThrows(NameException.class, () -> person.setName("A"));
        assertThrows(NameException.class, () -> person.setName(null));
    }

    @Test
    void testSetSurnameInvalid() {
        Person person = new Person();
        assertThrows(NameException.class, () -> person.setSurname(""));
        assertThrows(NameException.class, () -> person.setSurname("B"));
        assertThrows(NameException.class, () -> person.setSurname(null));
    }

    @Test
    void testSetAgeInvalid() {
        Person person = new Person();
        assertThrows(AgeException.class, () -> person.setAge(-1));
        assertThrows(AgeException.class, () -> person.setAge(130));
        assertThrows(AgeException.class, () -> person.setAge(17));
    }
}
