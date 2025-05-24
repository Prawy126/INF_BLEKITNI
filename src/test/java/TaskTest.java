
import org.example.sys.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testy jednostkowe dla klasy Task.
 */
public class TaskTest {

    private Task task;
    private Date today;
    private LocalTime shiftTime;

    /**
     * Przygotowanie wspólnych obiektów przed każdym testem.
     */
    @BeforeEach
    void setUp() {
        today     = new Date();
        shiftTime = LocalTime.of(8, 30);
        task = new Task(
                "Report",
                today,
                "OPEN",
                "Monthly sales report",
                shiftTime
        );
    }

    /**
     * Testuje poprawność konstruktora oraz getterów.
     */
    @Test
    @DisplayName("konstruktor i gettery")
    void shouldInitializeViaConstructorAndGetters() {
        assertEquals(
                "Report",
                task.getName(),
                "nazwa zadania powinna się zgadzać"
        );
        assertSame(
                today,
                task.getDate(),
                "data powinna być tą samą instancją"
        );
        assertEquals(
                "OPEN",
                task.getStatus(),
                "status zadania powinien się zgadzać"
        );
        assertEquals(
                "Monthly sales report",
                task.getDescription(),
                "opis zadania powinien się zgadzać"
        );
        assertEquals(
                shiftTime,
                task.getDurationOfTheShift(),
                "czas zmiany powinien się zgadzać"
        );
    }

    /**
     * Testuje poprawność setterów.
     */
    @Test
    @DisplayName("settery działają poprawnie")
    void shouldAllowSettersToModifyFields() {
        Date tomorrow    = new Date(today.getTime() + 86_400_000L);
        LocalTime newShift = LocalTime.of(12, 0);

        task.setName("Follow-up");
        task.setDate(tomorrow);
        task.setStatus("DONE");
        task.setDescription("Follow-up meeting");
        task.setDurationOfTheShift(newShift);

        assertEquals(
                "Follow-up",
                task.getName(),
                "nazwa zadania powinna zostać zmieniona"
        );
        assertSame(
                tomorrow,
                task.getDate(),
                "data zadania powinna zostać zmieniona"
        );
        assertEquals(
                "DONE",
                task.getStatus(),
                "status zadania powinien zostać zmieniony"
        );
        assertEquals(
                "Follow-up meeting",
                task.getDescription(),
                "opis zadania powinien zostać zmieniony"
        );
        assertEquals(
                newShift,
                task.getDurationOfTheShift(),
                "czas zmiany powinien zostać zmieniony"
        );
    }

    /**
     * Testuje, że toString() zawiera wartości pól.
     */
    @Test
    @DisplayName("toString zawiera pola")
    void toStringShouldContainFieldValues() {
        String text = task.toString();
        assertTrue(
                text.contains("Report"),
                "toString() powinien zawierać nazwę zadania"
        );
        assertTrue(
                text.contains(today.toString()),
                "toString() powinien zawierać datę"
        );
        assertTrue(
                text.contains(shiftTime.toString()),
                "toString() powinien zawierać czas zmiany"
        );
    }

    /**
     * Testuje, że toString() dobrze radzi sobie z polami null.
     */
    @Test
    @DisplayName("toString obsługuje null")
    void toStringShouldHandleNullValues() {
        Task empty = new Task();
        String text = empty.toString();
        assertTrue(
                text.contains("brak daty"),
                "toString() powinien wspominać o braku daty"
        );
        assertTrue(
                text.contains("brak"),
                "toString() powinien wspominać o braku czasu zmiany"
        );
    }
}
