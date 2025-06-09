/*
 * Classname: TaskEmployeeId
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Klasa reprezentująca złożony klucz główny (EmbeddedId)
 * dla encji TaskEmployee (połączenie taskId + employeeId).
 * Implementuje Serializable, co jest wymagane dla kluczy złożonych
 * w JPA.
 * Zawiera metody equals() i hashCode() niezbędne do prawidłowego
 * porównywania identyfikatorów w kontekście JPA.
 */
@Embeddable
public class TaskEmployeeId implements Serializable {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą TaskEmployeeId.
     */
    private static final Logger logger
            = LogManager.getLogger(TaskEmployeeId.class);

    /**
     * Identyfikator zadania.
     * Stanowi część złożonego klucza głównego.
     */
    @Column(name = "Id_zadania")
    private int taskId;

    /**
     * Identyfikator pracownika.
     * Stanowi część złożonego klucza głównego.
     */
    @Column(name = "Id_pracownika")
    private int employeeId;

    /**
     * Konstruktor bezparametrowy wymagany przez JPA.
     * Operacja jest logowana na poziomie DEBUG.
     */
    public TaskEmployeeId() {
        logger.debug("Utworzono nową instancję" +
                " TaskEmployeeId (domyślny konstruktor).");
    }

    /**
     * Konstruktor wygodny.
     * Tworzy złożony identyfikator z podanych składowych.
     * Operacja jest logowana na poziomie INFO.
     *
     * @param taskId identyfikator zadania
     * @param employeeId identyfikator pracownika
     */
    public TaskEmployeeId(int taskId, int employeeId) {
        this.taskId = taskId;
        this.employeeId = employeeId;
        logger.info("Utworzono złożony ID: Id_zadania={}," +
                " Id_pracownika={}", taskId, employeeId);
    }

    /**
     * @return identyfikator zadania
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * @param taskId nowy identyfikator zadania
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
        logger.debug("Zaktualizowano taskId na: {}", taskId);
    }

    /**
     * @return identyfikator pracownika
     */
    public int getEmployeeId() {
        return employeeId;
    }

    /**
     * @param employeeId nowy identyfikator pracownika
     */
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
        logger.debug("Zaktualizowano employeeId na: {}", employeeId);
    }

    /**
     * Porównuje ten obiekt z innym do równości.
     * Metoda niezbędna do prawidłowego działania JPA z kluczami złożonymi.
     * Operacja jest logowana na poziomie WARN w przypadku nierówności.
     *
     * @param o obiekt do porównania
     * @return true jeśli obiekty są równe, false w przeciwnym przypadku
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskEmployeeId)) return false;
        TaskEmployeeId that = (TaskEmployeeId) o;
        boolean result = taskId == that.taskId && employeeId == that.employeeId;
        if (!result) {
            logger.warn("TaskEmployeeId NIE jest równy:" +
                    " {} vs {}", this, that);
        }
        return result;
    }

    /**
     * Oblicza kod mieszający dla tego obiektu.
     * Metoda niezbędna do prawidłowego działania JPA z kluczami złożonymi.
     * Operacja jest logowana na poziomie DEBUG.
     *
     * @return wyliczony kod mieszający
     */
    @Override
    public int hashCode() {
        int hash = Objects.hash(taskId, employeeId);
        logger.debug("Obliczono hashCode: {}", hash);
        return hash;
    }

    /**
     * Zwraca reprezentację tekstową złożonego identyfikatora.
     *
     * @return tekstowa reprezentacja identyfikatora
     */
    @Override
    public String toString() {
        return "TaskEmployeeId{" +
                "taskId=" + taskId +
                ", employeeId=" + employeeId +
                '}';
    }
}