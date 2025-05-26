/*
 * Classname: TaskEmployeeTest
 * Version information: 1.3
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.Employee;
import org.example.sys.EmpTask;
import org.example.sys.TaskEmployee;
import org.example.sys.TaskEmployeeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Testy jednostkowe dla klasy TaskEmployee.
 */
class TaskEmployeeTest {

    private TaskEmployee te;
    private EmpTask task;
    private Employee employee;

    @BeforeEach
    void setUp() {
        te = new TaskEmployee();
        task = new EmpTask();
        task.setId(100);
        employee = new Employee();
        employee.setId(200);
    }

    /**
     * Testuje domyślny konstruktor i metody setter/getter.
     */
    @Test
    void testDefaultConstructorAndSetters() {
        te.setTask(task);
        te.setEmployee(employee);

        assertNotNull(te.getTask(), "getTask() nie powinno zwracać null");
        assertEquals(100, te.getTask().getId(), "Id zadania powinno wynosić 100");
        assertNotNull(te.getEmployee(), "getEmployee() nie powinno zwracać null");
        assertEquals(200, te.getEmployee().getId(), "Id pracownika powinno wynosić 200");
    }

    /**
     * Testuje konstruktor wygodny oraz prawidłowość złożonego klucza.
     */
    @Test
    void testFullConstructorAndCompositeId() {
        TaskEmployee te2 = new TaskEmployee(task, employee);
        TaskEmployeeId expectedId = new TaskEmployeeId(100, 200);

        assertEquals(expectedId, te2.getId(), "Id powinno być złożone z (100,200)");
        assertSame(task, te2.getTask(), "getTask() powinno zwrócić obiekt Task przekazany do konstruktora");
        assertSame(employee, te2.getEmployee(), "getEmployee() powinno zwrócić obiekt Employee przekazany do konstruktora");
    }

    /**
     * Testuje setter/getter dla pola id.
     */
    @Test
    void testSetAndGetId() {
        TaskEmployeeId id = new TaskEmployeeId(1, 2);
        te.setId(id);

        assertEquals(id, te.getId(), "getId() powinno zwrócić ustawione id");
    }
}
