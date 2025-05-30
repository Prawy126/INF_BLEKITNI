/*
 * Classname: UserRepository
 * Version information: 2.0
 * Date: 2025-05-30
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Employee;
import org.example.sys.PasswordResetToken;

import java.util.Date;
import java.util.List;

public class UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);
    private static int loggedInEmployeeId = -1;

    public UserRepository() {
        logger.info("Używam wspólnego EMF z EMFProvider");
    }

    public List<Employee> getAllEmployees() {
        logger.debug("getAllEmployees() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.deleted = false", Employee.class)
                    .getResultList();
            logger.info("getAllEmployees() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllEmployees() – błąd podczas pobierania pracowników", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllEmployees() – EM zamknięty");
        }
    }

    public List<Employee> getCashiers() {
        logger.debug("getCashiers() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.position = 'Kasjer' AND e.deleted = false", Employee.class)
                    .getResultList();
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

    public Employee findByLogin(String login) {
        logger.debug("findByLogin() – start, login={}", login);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Employee e = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login", Employee.class)
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

    public Employee findByLoginAndPassword(String login, String password) {
        logger.debug("findByLoginAndPassword() – start, login={}", login);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Employee e = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login AND e.password = :password", Employee.class)
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

    public Employee findById(int id) {
        logger.debug("findById() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public void addEmployee(Employee employee) {
        logger.debug("addEmployee() – start, {}", employee);
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public void updateEmployee(Employee employee) {
        logger.debug("updateEmployee() – start, {}", employee);
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public void removeEmployee(Employee employee) {
        logger.debug("removeEmployee() – start, {}", employee);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee m = em.find(Employee.class, employee.getId());
            if (m != null) {
                m.setDeleted(true);
                em.merge(m);
                logger.info("removeEmployee() – ustawiono usunięty dla id={}", m.getId());
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

    public Employee getCurrentEmployee() {
        logger.debug("getCurrentEmployee() – currentId={}", loggedInEmployeeId);
        if (loggedInEmployeeId < 0) {
            logger.info("getCurrentEmployee() – brak zalogowanego pracownika");
            return null;
        }
        EntityManager em = EMFProvider.get().createEntityManager();
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

    public static void setLoggedInEmployee(int employeeId) {
        logger.debug("setLoggedInEmployee() – {}", employeeId);
        loggedInEmployeeId = employeeId;
    }

    public static void resetCurrentEmployee() {
        logger.debug("resetCurrentEmployee() – reset ID");
        loggedInEmployeeId = -1;
    }

    public List<Employee> findByName(String nameFragment) {
        logger.debug("findByName() – fragment={}", nameFragment);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "  AND e.deleted = FALSE",
                            Employee.class)
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

    public List<Employee> findBySurname(String surnameFragment) {
        logger.debug("findBySurname() – fragment={}", surnameFragment);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.surname) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "  AND e.deleted = FALSE",
                            Employee.class)
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

    public List<Employee> findByAge(int min, int max) {
        logger.debug("findByAge() – min={}, max={}", min, max);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.age BETWEEN :min AND :max " +
                                    "  AND e.deleted = FALSE",
                            Employee.class)
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

    public List<Employee> findByAddress(int addressId) {
        logger.debug("findByAddress() – addressId={}", addressId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.address.id = :aid AND e.deleted = FALSE",
                            Employee.class)
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

    public List<Employee> findByEmail(String emailFragment) {
        logger.debug("findByEmail() – fragment={}", emailFragment);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.email) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "  AND e.deleted = FALSE",
                            Employee.class)
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

    public List<Employee> findBySalary(double min, double max) {
        logger.debug("findBySalary() – min={}, max={}", min, max);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.salary BETWEEN :min AND :max " +
                                    "  AND e.deleted = FALSE",
                            Employee.class)
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

    public List<Employee> findByPosition(String position) {
        logger.debug("findByPosition() – position={}", position);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.position = :st AND e.deleted = FALSE",
                            Employee.class)
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

    public List<Employee> getOnSickLeave() {
        logger.debug("getOnSickLeave() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.onSickLeave = TRUE AND e.deleted = FALSE",
                            Employee.class)
                    .getResultList();
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

    public List<Employee> getNotOnSickLeave() {
        logger.debug("getNotOnSickLeave() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.onSickLeave = FALSE AND e.deleted = FALSE",
                            Employee.class)
                    .getResultList();
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

    public boolean updatePasswordByEmail(String email, String newHashedPassword) {
        logger.debug("updatePasswordByEmail() – email={}", email);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            List<Employee> employees = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.email = :email AND e.deleted = FALSE",
                            Employee.class)
                    .setParameter("email", email)
                    .getResultList();
            if (employees.isEmpty()) {
                logger.warn("updatePasswordByEmail() – brak użytkownika z emailem: {}", email);
                return false;
            }
            Employee employee = employees.get(0);
            employee.setPassword(newHashedPassword);
            em.merge(employee);
            tx.commit();
            logger.info("updatePasswordByEmail() – zaktualizowano hasło dla: {}", email);
            return true;
        } catch (Exception e) {
            logger.error("updatePasswordByEmail() – błąd", e);
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
            logger.debug("updatePasswordByEmail() – EM zamknięty");
        }
    }

    public boolean savePasswordResetToken(PasswordResetToken token) {
        logger.debug("savePasswordResetToken() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(token);
            tx.commit();
            logger.info("savePasswordResetToken() – zapisano token dla użytkownika: {}", token.getUserId());
            return true;
        } catch (Exception e) {
            logger.error("savePasswordResetToken() – błąd", e);
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
            logger.debug("savePasswordResetToken() – EM zamknięty");
        }
    }

    public PasswordResetToken findValidToken(String email, String codeHash) {
        logger.debug("findValidToken() – email={}", email);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Employee employee = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.email = :email AND e.deleted = FALSE",
                            Employee.class)
                    .setParameter("email", email)
                    .getSingleResult();
            if (employee == null) return null;
            return em.createQuery(
                            "SELECT t FROM PasswordResetToken t " +
                                    " WHERE t.userId = :userId " +
                                    "   AND t.resetCodeHash = :codeHash " +
                                    "   AND t.expirationTime > CURRENT_TIMESTAMP " +
                                    "   AND t.used = FALSE",
                            PasswordResetToken.class)
                    .setParameter("userId", (long) employee.getId())
                    .setParameter("codeHash", codeHash)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            logger.error("findValidToken() – błąd", e);
            return null;
        } finally {
            em.close();
            logger.debug("findValidToken() – EM zamknięty");
        }
    }

    public boolean markTokenAsUsed(PasswordResetToken token) {
        logger.debug("markTokenAsUsed() – token ID={}", token.getId());
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            token.setUsed(true);
            em.merge(token);
            tx.commit();
            return true;
        } catch (Exception e) {
            logger.error("markTokenAsUsed() – błąd", e);
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
            logger.debug("markTokenAsUsed() – EM zamknięty");
        }
    }

    public List<PasswordResetToken> findValidTokensByUserId(long userId) {
        logger.debug("findValidTokensByUserId() – userId={}", userId);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM PasswordResetToken t " +
                                    " WHERE t.userId = :userId " +
                                    "   AND t.expirationTime > CURRENT_TIMESTAMP " +
                                    "   AND t.used = FALSE",
                            PasswordResetToken.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("findValidTokensByUserId() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findValidTokensByUserId() – EM zamknięty");
        }
    }

    public void close() {
    }
}