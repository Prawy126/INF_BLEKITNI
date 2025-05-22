/*
 * Classname: UserRepository
 * Version information: 1.4
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Employee;

import java.util.List;

/**
 * Repozytorium do zarządzania pracownikami (użytkownikami) w systemie.
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz zaawansowane wyszukiwanie pracowników.
 */
public class UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);
    private final EntityManagerFactory emf;
    private static int loggedInEmployeeId = -1;

    /**
     * Konstruktor inicjalizujący fabrykę EntityManagerFactory dla persistence unit "myPU".
     */
    public UserRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono UserRepository, EMF = {}", emf);
    }

    /**
     * Pobiera wszystkich pracowników nieoznaczonych jako usunięci (soft delete = false).
     *
     * @return lista aktywnych pracowników lub pusta lista w przypadku błędu
     */
    public List<Employee> getAllEmployess() {
        logger.debug("getAllEmployess() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.deleted = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("getAllEmployess() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllEmployess() – błąd podczas pobierania pracowników", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllEmployess() – EM zamknięty");
        }
    }

    /**
     * Pobiera wszystkich pracowników o stanowisku 'Kasjer', którzy nie są usunięci.
     *
     * @return lista pracowników na stanowisku Kasjer lub pusta lista
     */
    public List<Employee> getCashiers() {
        logger.debug("getCashiers() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.position = 'Kasjer' AND e.deleted = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("getCashiers() – znaleziono {} kasjerów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getCashiers() – błąd podczas pobierania kasjerów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getCashiers() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracownika po unikalnym loginie, pomijając usuniętych.
     *
     * @param login login pracownika
     * @return obiekt Employee lub null, jeśli nie znaleziono lub użytkownik jest usunięty
     */
    public Employee findByLogin(String login) {
        logger.debug("findByLogin() – start, login={}", login);
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login",
                            Employee.class
                    )
                    .setParameter("login", login)
                    .getSingleResult();
            if (e != null && !e.isDeleted()) {
                logger.info("findByLogin() – znaleziono: {}", e);
                return e;
            } else {
                logger.warn("findByLogin() – pracownik usunięty lub null");
                return null;
            }
        } catch (NoResultException ex) {
            logger.warn("findByLogin() – brak wyniku dla login={}", login);
            return null;
        } catch (Exception ex) {
            logger.error("findByLogin() – błąd podczas wyszukiwania login={}", login, ex);
            return null;
        } finally {
            em.close();
            logger.debug("findByLogin() – EM zamknięty");
        }
    }

    /**
     * Uwierzytelnia pracownika po loginie i haśle; ustawia zalogowanego.
     *
     * @param login login
     * @param password hasło
     * @return zalogowany obiekt Employee lub null w przypadku niepowodzenia
     */
    public Employee findByLoginAndPassword(String login, String password) {
        logger.debug("findByLoginAndPassword() – start, login={}", login);
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login AND e.password = :password",
                            Employee.class
                    )
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .getSingleResult();
            if (e != null && !e.isDeleted()) {
                setLoggedInEmployee(e.getId());
                logger.info("findByLoginAndPassword() – uwierzytelniono, currentId={}", e.getId());
                return e;
            } else {
                logger.warn("findByLoginAndPassword() – pracownik usunięty lub null");
                return null;
            }
        } catch (NoResultException ex) {
            logger.warn("findByLoginAndPassword() – brak wyniku dla login={}", login);
            return null;
        } catch (Exception ex) {
            logger.error("findByLoginAndPassword() – błąd podczas logowania login={}", login, ex);
            return null;
        } finally {
            em.close();
            logger.debug("findByLoginAndPassword() – EM zamknięty");
        }
    }

    /**
     * Pobiera pracownika po jego identyfikatorze, pomijając usuniętych.
     *
     * @param id identyfikator pracownika
     * @return obiekt Employee lub null
     */
    public Employee findById(int id) {
        logger.debug("findById() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.find(Employee.class, id);
            if (e != null && !e.isDeleted()) {
                logger.info("findById() – znaleziono: {}", e);
                return e;
            } else {
                logger.warn("findById() – brak lub usunięty id={}", id);
                return null;
            }
        } catch (Exception ex) {
            logger.error("findById() – błąd podczas wyszukiwania id={}", id, ex);
            return null;
        } finally {
            em.close();
            logger.debug("findById() – EM zamknięty");
        }
    }

    /**
     * Dodaje nowego pracownika do bazy.
     *
     * @param employee encja Employee do zapisania
     */
    public void addEmployee(Employee employee) {
        logger.debug("addEmployee() – start, {}", employee);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(employee);
            tx.commit();
            logger.info("addEmployee() – dodano: {}", employee);
        } catch (Exception ex) {
            logger.error("addEmployee() – błąd podczas dodawania", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addEmployee() – EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejącego pracownika w bazie.
     *
     * @param employee encja Employee do zaktualizowania
     */
    public void updateEmployee(Employee employee) {
        logger.debug("updateEmployee() – start, {}", employee);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(employee);
            tx.commit();
            logger.info("updateEmployee() – zaktualizowano: {}", employee);
        } catch (Exception ex) {
            logger.error("updateEmployee() – błąd podczas aktualizacji", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateEmployee() – EM zamknięty");
        }
    }

    /**
     * Oznacza pracownika jako usuniętego (soft delete).
     *
     * @param employee encja Employee do oznaczenia jako usunięta
     */
    public void removeEmployee(Employee employee) {
        logger.debug("removeEmployee() – start, {}", employee);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee m = em.find(Employee.class, employee.getId());
            if (m != null) {
                m.setDeleted(true);
                em.merge(m);
                logger.info("removeEmployee() – ustawiono usuniety dla id={}", m.getId());
            } else {
                logger.warn("removeEmployee() – brak pracownika id={}", employee.getId());
            }
            tx.commit();
        } catch (Exception ex) {
            logger.error("removeEmployee() – błąd podczas usuwania", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeEmployee() – EM zamknięty");
        }
    }

    /**
     * Zwraca obiekt aktualnie zalogowanego pracownika.
     *
     * @return obiekt Employee lub null jeśli nikt nie jest zalogowany
     */
    public Employee getCurrentEmployee() {
        logger.debug("getCurrentEmployee() – currentId={}", loggedInEmployeeId);
        if (loggedInEmployeeId < 0) {
            logger.info("getCurrentEmployee() – brak zalogowanego pracownika");
            return null;
        }
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.find(Employee.class, loggedInEmployeeId);
            if (e != null && !e.isDeleted()) {
                logger.info("getCurrentEmployee() – zwrócono: {}", e);
                return e;
            } else {
                logger.warn("getCurrentEmployee() – pracownik usunięty lub nie istnieje");
                return null;
            }
        } finally {
            em.close();
            logger.debug("getCurrentEmployee() – EM zamknięty");
        }
    }

    /**
     * Ustawia ID aktualnie zalogowanego pracownika.
     *
     * @param employeeId identyfikator pracownika
     */
    public static void setLoggedInEmployee(int employeeId) {
        logger.debug("setLoggedInEmployee() – {}", employeeId);
        loggedInEmployeeId = employeeId;
    }


    /**
     * Resetuje informację o aktualnie zalogowanym pracowniku.
     */
    public static void resetCurrentEmployee() {
        logger.debug("resetCurrentEmployee() – reset ID");
        loggedInEmployeeId = -1;
    }

    /**
     * Wyszukuje pracowników po fragmencie imienia, pomijając usuniętych.
     *
     * @param nameFragment fragment imienia do wyszukania
     * @return lista pasujących pracowników lub pusta lista
     */
    public List<Employee> findByName(String nameFragment) {
        logger.debug("findByName() – fragment={}", nameFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "AND e.deleted = FALSE",
                            Employee.class
                    )
                    .setParameter("frag", nameFragment)
                    .getResultList();
            logger.info("findByName() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByName() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByName() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników po fragmencie nazwiska, pomijając usuniętych.
     *
     * @param surnameFragment fragment nazwiska
     * @return lista pasujących pracowników lub pusta lista
     */
    public List<Employee> findBySurname(String surnameFragment) {
        logger.debug("findBySurname() – fragment={}", surnameFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.surname) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "AND e.deleted = FALSE",
                            Employee.class
                    )
                    .setParameter("frag", surnameFragment)
                    .getResultList();
            logger.info("findBySurname() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findBySurname() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findBySurname() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników w podanym przedziale wieku, pomijając usuniętych.
     *
     * @param min minimalny wiek
     * @param max maksymalny wiek
     * @return lista pracowników spełniających kryterium lub pusta lista
     */
    public List<Employee> findByAge(int min, int max) {
        logger.debug("findByAge() – min={}, max={}", min, max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.age BETWEEN :min AND :max " +
                                    "AND e.deleted = FALSE",
                            Employee.class
                    )
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("findByAge() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByAge() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByAge() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników według identyfikatora adresu, pomijając usuniętych.
     *
     * @param addressId identyfikator adresu
     * @return lista pracowników mieszkających pod danym adresem
     */
    public List<Employee> findByAddress(int addressId) {
        logger.debug("findByAddress() – addressId={}", addressId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.address.id = :aid AND e.deleted = FALSE",
                            Employee.class
                    )
                    .setParameter("aid", addressId)
                    .getResultList();
            logger.info("findByAddress() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByAddress() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByAddress() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników po fragmencie e-maila, pomijając usuniętych.
     *
     * @param emailFragment fragment adresu e-mail
     * @return lista pasujących pracowników lub pusta lista
     */
    public List<Employee> findByEmail(String emailFragment) {
        logger.debug("findByEmail() – fragment={}", emailFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.email) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "AND e.deleted = FALSE",
                            Employee.class
                    )
                    .setParameter("frag", emailFragment)
                    .getResultList();
            logger.info("findByEmail() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByEmail() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByEmail() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników w podanym przedziale zarobków, pomijając usuniętych.
     *
     * @param min minimalne zarobki
     * @param max maksymalne zarobki
     * @return lista pracowników spełniających kryterium lub pusta lista
     */
    public List<Employee> findBySalary(double min, double max) {
        logger.debug("findBySalary() – min={}, max={}", min, max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.salary BETWEEN :min AND :max " +
                                    "AND e.deleted = FALSE",
                            Employee.class
                    )
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("findBySalary() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findBySalary() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findBySalary() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników według stanowiska, pomijając usuniętych.
     *
     * @param position nazwa stanowiska
     * @return lista pracowników na danym stanowisku lub pusta lista
     */
    public List<Employee> findByPosition(String position) {
        logger.debug("findByPosition() – position={}", position);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.position = :st AND e.deleted = FALSE",
                            Employee.class
                    )
                    .setParameter("st", position)
                    .getResultList();
            logger.info("findByPosition() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByPosition() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByPosition() – EM zamknięty");
        }
    }

    /**
     * Pobiera pracowników będących na zwolnieniu lekarskim, pomijając usuniętych.
     *
     * @return lista pracowników na zwolnieniu
     */
    public List<Employee> getOnSickLeave() {
        logger.debug("getOnSickLeave() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e " +
                            "WHERE e.onSickLeave = TRUE AND e.deleted = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("getOnSickLeave() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getOnSickLeave() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getOnSickLeave() – EM zamknięty");
        }
    }

    /**
     * Pobiera pracowników niebędących na zwolnieniu lekarskim, pomijając usuniętych.
     *
     * @return lista pracowników nie na zwolnieniu
     */
    public List<Employee> getNotOnSickLeave() {
        logger.debug("getNotOnSickLeave() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e " +
                            "WHERE e.onSickLeave = FALSE AND e.deleted = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("getNotOnSickLeave() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getNotOnSickLeave() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getNotOnSickLeave() – EM zamknięty");
        }
    }

    /**
     * Zamyka fabrykę EntityManagerFactory, zwalniając wszystkie zasoby.
     * Po wywołaniu tej metody instancja repozytorium nie może być już używana.
     */
    public void close() {
        logger.debug("close() – start zamykania EMF");
        try {
            if (emf.isOpen()) {
                emf.close();
                logger.info("close() – EMF zamknięty");
            } else {
                logger.warn("close() – EMF był już zamknięty");
            }
        } catch (Exception ex) {
            logger.error("close() – błąd podczas zamykania EMF", ex);
        }
    }
}