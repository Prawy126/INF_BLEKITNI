/*
 * Classname: AddressRepository
 * Version information: 1.4
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Address;

import java.util.List;

public class AddressRepository implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(AddressRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Tworzy repozytorium i inicjalizuje EntityManagerFactory.
     */
    public AddressRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono AddressRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowy adres do bazy.
     *
     * @param address obiekt Address do zapisania
     */
    public void addAddress(Address address) {
        logger.debug("addAddress() - start, address={}", address);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(address);
            tx.commit();
            logger.info("addAddress() - dodano adres: {}", address);
        } catch (Exception e) {
            logger.error("addAddress() - błąd podczas dodawania adresu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addAddress() - EM zamknięty");
        }
    }

    /**
     * Pobiera adres o podanym identyfikatorze.
     *
     * @param id identyfikator Address
     * @return znaleziony Address lub null, jeśli brak
     */
    public Address findAddressById(int id) {
        logger.debug("findAddressById() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Address a = em.find(Address.class, id);
            logger.info("findAddressById() - znaleziono: {}", a);
            return a;
        } catch (Exception e) {
            logger.error("findAddressById() - błąd podczas pobierania adresu o id=" + id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findAddressById() - EM zamknięty");
        }
    }

    /**
     * Pobiera listę wszystkich adresów w bazie.
     *
     * @return lista obiektów Address (może być pusta)
     */
    public List<Address> getAllAddresses() {
        logger.debug("getAllAddresses() - start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery("SELECT a FROM Address a", Address.class)
                    .getResultList();
            logger.info("getAllAddresses() - pobrano {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllAddresses() - błąd podczas pobierania adresów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllAddresses() - EM zamknięty");
        }
    }

    /**
     * Usuwa adres o podanym identyfikatorze.
     *
     * @param id identyfikator Address do usunięcia
     */
    public void removeAddress(int id) {
        logger.debug("removeAddress() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Address address = em.find(Address.class, id);
            if (address != null) {
                em.remove(address);
                logger.info("removeAddress() - usunięto adres: {}", address);
            } else {
                logger.warn("removeAddress() - brak adresu o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeAddress() - błąd podczas usuwania adresu o id=" + id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeAddress() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje adresy po fragmencie nazwy miejscowości (case-insensitive).
     *
     * @param town fragment nazwy miejscowości
     * @return lista pasujących Address (może być pusta)
     */
    public List<Address> findByTown(String town) {
        logger.debug("findByTown() - town={}", town);
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a WHERE LOWER(a.town) LIKE LOWER(CONCAT('%', :town, '%'))",
                            Address.class)
                    .setParameter("town", town)
                    .getResultList();
            logger.info("findByTown() - znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByTown() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByTown() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje adresy po numerze domu.
     *
     * @param houseNumber numer domu
     * @return lista pasujących Address
     */
    public List<Address> findByHouseNumber(String houseNumber) {
        logger.debug("findByHouseNumber() - houseNumber={}", houseNumber);
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a WHERE a.houseNumber = :houseNumber",
                            Address.class)
                    .setParameter("houseNumber", houseNumber)
                    .getResultList();
            logger.info("findByHouseNumber() - znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByHouseNumber() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByHouseNumber() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje adresy po numerze mieszkania.
     *
     * @param apartmentNumber numer mieszkania
     * @return lista pasujących Address
     */
    public List<Address> findByApartmentNumber(String apartmentNumber) {
        logger.debug("findByApartmentNumber() - apartmentNumber={}", apartmentNumber);
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a WHERE a.apartmentNumber = :apartmentNumber",
                            Address.class)
                    .setParameter("apartmentNumber", apartmentNumber)
                    .getResultList();
            logger.info("findByApartmentNumber() - znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByApartmentNumber() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByApartmentNumber() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje adresy po kodzie pocztowym.
     *
     * @param zipCode kod pocztowy
     * @return lista pasujących Address
     */
    public List<Address> findByZipCode(String zipCode) {
        logger.debug("findByZipCode() - zipCode={}", zipCode);
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a WHERE a.zipCode = :zipCode",
                            Address.class)
                    .setParameter("zipCode", zipCode)
                    .getResultList();
            logger.info("findByZipCode() - znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByZipCode() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByZipCode() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje adresy po mieście (case-insensitive).
     *
     * @param city nazwa miasta
     * @return lista pasujących Address
     */
    public List<Address> findByCity(String city) {
        logger.debug("findByCity() - city={}", city);
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a WHERE LOWER(a.city) = LOWER(:city)",
                            Address.class)
                    .setParameter("city", city)
                    .getResultList();
            logger.info("findByCity() - znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByCity() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByCity() - EM zamknięty");
        }
    }

    /**
     * Zamyka fabrykę EntityManagerFactory.
     */
    @Override
    public void close() {
        logger.debug("close() - zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() - EMF zamknięty");
        }
    }
}
