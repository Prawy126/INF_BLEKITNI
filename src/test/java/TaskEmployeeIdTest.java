

import org.example.sys.TaskEmployeeId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla klasy TaskEmployeeId.
 */
class TaskEmployeeIdTest {

    /**
     * Testuje konstruktor wygodny oraz gettery.
     */
    @Test
    void testConstructorAndGetters() {
        TaskEmployeeId id = new TaskEmployeeId(5, 10);

        assertEquals(5,  id.getTaskId(),     "getTaskId() powinno zwrócić 5");
        assertEquals(10, id.getEmployeeId(), "getEmployeeId() powinno zwrócić 10");
    }

    /**
     * Testuje settery dla pola taskId i employeeId.
     */
    @Test
    void testSetters() {
        TaskEmployeeId id = new TaskEmployeeId();
        id.setTaskId(7);
        id.setEmployeeId(14);

        assertEquals(7,  id.getTaskId(),     "getTaskId() powinno zwrócić 7 po wywołaniu setTaskId");
        assertEquals(14, id.getEmployeeId(), "getEmployeeId() powinno zwrócić 14 po wywołaniu setEmployeeId");
    }

    /**
     * Testuje metody equals i hashCode.
     */
    @Test
    void testEqualsAndHashCode() {
        TaskEmployeeId id1 = new TaskEmployeeId(3, 4);
        TaskEmployeeId id2 = new TaskEmployeeId(3, 4);
        TaskEmployeeId id3 = new TaskEmployeeId(4, 5);

        assertEquals(id1, id2,                    "Obiekty o tych samych wartościach powinny być równe");
        assertEquals(id1.hashCode(), id2.hashCode(), "hashCode() tych samych obiektów powinien być taki sam");
        assertNotEquals(id1, id3,                 "Obiekty o różnych wartościach nie powinny być równe");
    }
}
