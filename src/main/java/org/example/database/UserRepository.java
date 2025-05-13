package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.example.sys.Employee;
import java.util.List;

/**
 * Repozytorium do operacji na pracownikach.
 */
public class UserRepository {

    private final EntityManagerFactory emf;

    // Statyczna zmienna przechowująca ID zalogowanego użytkownika
    private static int loggedInEmployeeId = -1;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory.
     */
    public UserRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    /**
     * Pobiera wszystkich pracowników.
     *
     * @return lista wszystkich pracowników
     */
    public List<Employee> pobierzWszystkichPracownikow() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Employee e", Employee.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Zwraca listę wszystkich kasjerów.
     *
     * @return lista kasjerów
     */
    public List<Employee> pobierzKasjerow() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Employee e WHERE e.stanowisko = 'Kasjer'",
                    Employee.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po loginie.
     *
     * @param login login pracownika
     * @return znaleziony pracownik
     */
    public Employee znajdzPoLoginie(String login) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login",
                            Employee.class
                    ).setParameter("login", login)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Dodaje nowego pracownika.
     *
     * @param pracownik pracownik do dodania
     */
    public void dodajPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(pracownik);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Aktualizuje dane istniejącego pracownika.
     *
     * @param pracownik pracownik do aktualizacji
     */
    public void aktualizujPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(pracownik);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Usuwa pracownika na podstawie identyfikatora.
     *
     * @param pracownik pracownik do usunięcia
     */
    public void usunPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee managed = em.find(Employee.class, pracownik.getId());
            if (managed != null) {
                em.remove(managed);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po loginie i haśle.
     * Jeśli pracownik zostanie znaleziony, jego ID jest zapisywane jako ID zalogowanego użytkownika.
     *
     * @param login login pracownika
     * @param haslo hasło pracownika
     * @return znaleziony pracownik lub null, jeśli brak
     */
    public Employee znajdzPoLoginieIHasle(String login, String haslo) {
        EntityManager em = emf.createEntityManager();
        try {
            Employee employee = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login "
                                    + "AND e.password = :haslo",
                            Employee.class
                    ).setParameter("login", login)
                    .setParameter("haslo", haslo)
                    .getSingleResult();

            if (employee != null) {
                setLoggedInEmployee(employee.getId());
            }

            return employee;
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po identyfikatorze.
     *
     * @param id identyfikator pracownika
     * @return znaleziony pracownik lub null, jeśli brak
     */
    public Employee znajdzPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Employee.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Ustawia ID zalogowanego pracownika.
     *
     * @param employeeId ID pracownika
     */
    public static void setLoggedInEmployee(int employeeId) {
        loggedInEmployeeId = employeeId;
    }

    /**
     * Zwraca ID aktualnie zalogowanego pracownika.
     *
     * @return ID zalogowanego pracownika lub -1 jeśli nikt nie jest zalogowany
     */
    public static int getLoggedInEmployeeId() {
        return loggedInEmployeeId;
    }

    public Employee getCurrentEmployee() {
        if (loggedInEmployeeId == -1) {
            return null;
        }

        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Employee.class, loggedInEmployeeId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Resetuje ID zalogowanego pracownika.
     * Używane podczas wylogowywania.
     */
    public static void resetCurrentEmployee() {
        loggedInEmployeeId = -1;
    }

    /**
     * Zamknięcie EntityManagerFactory.
     */
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}