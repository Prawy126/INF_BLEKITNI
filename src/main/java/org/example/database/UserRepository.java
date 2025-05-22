/*
 * Classname: UserRepository
 * Version information: 1.3
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
    public List<Employee> pobierzWszystkichPracownikow() {
        logger.debug("pobierzWszystkichPracownikow() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.usuniety = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("pobierzWszystkichPracownikow() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkichPracownikow() – błąd podczas pobierania pracowników", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkichPracownikow() – EM zamknięty");
        }
    }

    /**
     * Pobiera wszystkich pracowników o stanowisku 'Kasjer', którzy nie są usunięci.
     *
     * @return lista pracowników na stanowisku Kasjer lub pusta lista
     */
    public List<Employee> pobierzKasjerow() {
        logger.debug("pobierzKasjerow() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.stanowisko = 'Kasjer' AND e.usuniety = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("pobierzKasjerow() – znaleziono {} kasjerów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzKasjerow() – błąd podczas pobierania kasjerów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzKasjerow() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracownika po unikalnym loginie, pomijając usuniętych.
     *
     * @param login login pracownika
     * @return obiekt Employee lub null, jeśli nie znaleziono lub użytkownik jest usunięty
     */
    public Employee znajdzPoLoginie(String login) {
        logger.debug("znajdzPoLoginie() – start, login={}", login);
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login",
                            Employee.class
                    )
                    .setParameter("login", login)
                    .getSingleResult();
            if (e != null && !e.isUsuniety()) {
                logger.info("znajdzPoLoginie() – znaleziono: {}", e);
                return e;
            } else {
                logger.warn("znajdzPoLoginie() – pracownik usunięty lub null");
                return null;
            }
        } catch (NoResultException ex) {
            logger.warn("znajdzPoLoginie() – brak wyniku dla login={}", login);
            return null;
        } catch (Exception ex) {
            logger.error("znajdzPoLoginie() – błąd podczas wyszukiwania login={}", login, ex);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzPoLoginie() – EM zamknięty");
        }
    }

    /**
     * Uwierzytelnia pracownika po loginie i haśle; ustawia zalogowanego.
     *
     * @param login login
     * @param haslo hasło
     * @return zalogowany obiekt Employee lub null w przypadku niepowodzenia
     */
    public Employee znajdzPoLoginieIHasle(String login, String haslo) {
        logger.debug("znajdzPoLoginieIHasle() – start, login={}", login);
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login AND e.password = :haslo",
                            Employee.class
                    )
                    .setParameter("login", login)
                    .setParameter("haslo", haslo)
                    .getSingleResult();
            if (e != null && !e.isUsuniety()) {
                setLoggedInEmployee(e.getId());
                logger.info("znajdzPoLoginieIHasle() – uwierzytelniono, currentId={}", e.getId());
                return e;
            } else {
                logger.warn("znajdzPoLoginieIHasle() – pracownik usunięty lub null");
                return null;
            }
        } catch (NoResultException ex) {
            logger.warn("znajdzPoLoginieIHasle() – brak wyniku dla login={}", login);
            return null;
        } catch (Exception ex) {
            logger.error("znajdzPoLoginieIHasle() – błąd podczas logowania login={}", login, ex);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzPoLoginieIHasle() – EM zamknięty");
        }
    }

    /**
     * Pobiera pracownika po jego identyfikatorze, pomijając usuniętych.
     *
     * @param id identyfikator pracownika
     * @return obiekt Employee lub null
     */
    public Employee znajdzPoId(int id) {
        logger.debug("znajdzPoId() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.find(Employee.class, id);
            if (e != null && !e.isUsuniety()) {
                logger.info("znajdzPoId() – znaleziono: {}", e);
                return e;
            } else {
                logger.warn("znajdzPoId() – brak lub usunięty id={}", id);
                return null;
            }
        } catch (Exception ex) {
            logger.error("znajdzPoId() – błąd podczas wyszukiwania id={}", id, ex);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzPoId() – EM zamknięty");
        }
    }

    /**
     * Dodaje nowego pracownika do bazy.
     *
     * @param pracownik encja Employee do zapisania
     */
    public void dodajPracownika(Employee pracownik) {
        logger.debug("dodajPracownika() – start, {}", pracownik);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(pracownik);
            tx.commit();
            logger.info("dodajPracownika() – dodano: {}", pracownik);
        } catch (Exception ex) {
            logger.error("dodajPracownika() – błąd podczas dodawania", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajPracownika() – EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejącego pracownika w bazie.
     *
     * @param pracownik encja Employee do zaktualizowania
     */
    public void aktualizujPracownika(Employee pracownik) {
        logger.debug("aktualizujPracownika() – start, {}", pracownik);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(pracownik);
            tx.commit();
            logger.info("aktualizujPracownika() – zaktualizowano: {}", pracownik);
        } catch (Exception ex) {
            logger.error("aktualizujPracownika() – błąd podczas aktualizacji", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujPracownika() – EM zamknięty");
        }
    }

    /**
     * Oznacza pracownika jako usuniętego (soft delete).
     *
     * @param pracownik encja Employee do oznaczenia jako usunięta
     */
    public void usunPracownika(Employee pracownik) {
        logger.debug("usunPracownika() – start, {}", pracownik);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee m = em.find(Employee.class, pracownik.getId());
            if (m != null) {
                m.setUsuniety(true);
                em.merge(m);
                logger.info("usunPracownika() – ustawiono usuniety dla id={}", m.getId());
            } else {
                logger.warn("usunPracownika() – brak pracownika id={}", pracownik.getId());
            }
            tx.commit();
        } catch (Exception ex) {
            logger.error("usunPracownika() – błąd podczas usuwania", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunPracownika() – EM zamknięty");
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
            if (e != null && !e.isUsuniety()) {
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
     * @param imieFragment fragment imienia do wyszukania
     * @return lista pasujących pracowników lub pusta lista
     */
    public List<Employee> znajdzPoImieniu(String imieFragment) {
        logger.debug("znajdzPoImieniu() – fragment={}", imieFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.imie) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("frag", imieFragment)
                    .getResultList();
            logger.info("znajdzPoImieniu() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoImieniu() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoImieniu() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników po fragmencie nazwiska, pomijając usuniętych.
     *
     * @param nazwiskoFragment fragment nazwiska
     * @return lista pasujących pracowników lub pusta lista
     */
    public List<Employee> znajdzPoNazwisku(String nazwiskoFragment) {
        logger.debug("znajdzPoNazwisku() – fragment={}", nazwiskoFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.nazwisko) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("frag", nazwiskoFragment)
                    .getResultList();
            logger.info("znajdzPoNazwisku() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoNazwisku() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoNazwisku() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników w podanym przedziale wieku, pomijając usuniętych.
     *
     * @param min minimalny wiek
     * @param max maksymalny wiek
     * @return lista pracowników spełniających kryterium lub pusta lista
     */
    public List<Employee> znajdzPoWieku(int min, int max) {
        logger.debug("znajdzPoWieku() – min={}, max={}", min, max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.wiek BETWEEN :min AND :max " +
                                    "AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("znajdzPoWieku() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoWieku() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoWieku() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników według identyfikatora adresu, pomijając usuniętych.
     *
     * @param addressId identyfikator adresu
     * @return lista pracowników mieszkających pod danym adresem
     */
    public List<Employee> znajdzPoAdresie(int addressId) {
        logger.debug("znajdzPoAdresie() – addressId={}", addressId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.adres.id = :aid AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("aid", addressId)
                    .getResultList();
            logger.info("znajdzPoAdresie() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoAdresie() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoAdresie() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników po fragmencie e-maila, pomijając usuniętych.
     *
     * @param emailFragment fragment adresu e-mail
     * @return lista pasujących pracowników lub pusta lista
     */
    public List<Employee> znajdzPoEmailu(String emailFragment) {
        logger.debug("znajdzPoEmailu() – fragment={}", emailFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.email) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("frag", emailFragment)
                    .getResultList();
            logger.info("znajdzPoEmailu() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoEmailu() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoEmailu() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników w podanym przedziale zarobków, pomijając usuniętych.
     *
     * @param min minimalne zarobki
     * @param max maksymalne zarobki
     * @return lista pracowników spełniających kryterium lub pusta lista
     */
    public List<Employee> znajdzPoZarobkach(double min, double max) {
        logger.debug("znajdzPoZarobkach() – min={}, max={}", min, max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.zarobki BETWEEN :min AND :max " +
                                    "AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("znajdzPoZarobkach() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoZarobkach() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoZarobkach() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje pracowników według stanowiska, pomijając usuniętych.
     *
     * @param stanowisko nazwa stanowiska
     * @return lista pracowników na danym stanowisku lub pusta lista
     */
    public List<Employee> znajdzPoStanowisku(String stanowisko) {
        logger.debug("znajdzPoStanowisku() – stanowisko={}", stanowisko);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.stanowisko = :st AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("st", stanowisko)
                    .getResultList();
            logger.info("znajdzPoStanowisku() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoStanowisku() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoStanowisku() – EM zamknięty");
        }
    }

    /**
     * Pobiera pracowników będących na zwolnieniu lekarskim, pomijając usuniętych.
     *
     * @return lista pracowników na zwolnieniu
     */
    public List<Employee> pobierzNaSickLeave() {
        logger.debug("pobierzNaSickLeave() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e " +
                            "WHERE e.onSickLeave = TRUE AND e.usuniety = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("pobierzNaSickLeave() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzNaSickLeave() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzNaSickLeave() – EM zamknięty");
        }
    }

    /**
     * Pobiera pracowników niebędących na zwolnieniu lekarskim, pomijając usuniętych.
     *
     * @return lista pracowników nie na zwolnieniu
     */
    public List<Employee> pobierzNieNaSickLeave() {
        logger.debug("pobierzNieNaSickLeave() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e " +
                            "WHERE e.onSickLeave = FALSE AND e.usuniety = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("pobierzNieNaSickLeave() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzNieNaSickLeave() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzNieNaSickLeave() – EM zamknięty");
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