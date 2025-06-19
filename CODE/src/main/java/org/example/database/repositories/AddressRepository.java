/*
 * Classname: AddressRepository
 * Version information: 1.0
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.EMFProvider;
import org.example.sys.Address;

import java.util.List;

/**
 * Repozytorium do zarządzania adresami w systemie.
 * Zapewnia operacje CRUD oraz metody wyszukiwania adresów
 * według różnych kryteriów. Wykorzystuje EntityManager
 * do komunikacji z bazą danych.
 */
public class AddressRepository implements AutoCloseable {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą AddressRepository.
     */
    private static final Logger logger = LogManager.getLogger(
            AddressRepository.class);

    /**
     * Domyślny konstruktor – korzysta ze wspólnego EMF z EMFProvider.
     * Operacja jest logowana na poziomie INFO.
     */
    public AddressRepository() {
        logger.info("Utworzono AddressRepository, EMF={}",
                EMFProvider.get());
    }

    /**
     * Dodaje nowy adres do bazy.
     * Operacja jest wykonywana w transakcji.
     * W przypadku błędu, transakcja jest wycofywana.
     *
     * @param address obiekt Address do zapisania
     */
    public void addAddress(Address address) {
        logger.debug("addAddress() - start, address={}", address);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(address);
            tx.commit();
            logger.info("addAddress() - dodano adres: {}", address);
        } catch (Exception e) {
            logger.error("addAddress() " +
                    "- błąd podczas dodawania adresu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addAddress() - EM zamknięty");
        }
    }

    /**
     * Pobiera adres o podanym identyfikatorze.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest wartość null.
     *
     * @param id identyfikator Address
     * @return znaleziony Address lub null, jeśli brak lub wystąpił błąd
     */
    public Address findAddressById(int id) {
        logger.debug("findAddressById() - start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Address a = em.find(Address.class, id);
            logger.info("findAddressById() - znaleziono: {}", a);
            return a;
        } catch (Exception e) {
            logger.error("findAddressById() " +
                    "- błąd podczas pobierania adresu "
                    + "o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findAddressById() - EM zamknięty");
        }
    }

    /**
     * Pobiera listę wszystkich adresów w bazie.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @return lista obiektów Address (może być pusta)
     */
    public List<Address> getAllAddresses() {
        logger.debug("getAllAddresses() - start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Address> list = em
                    .createQuery("SELECT a FROM Address a",
                            Address.class)
                    .getResultList();
            logger.info("getAllAddresses() " +
                    "- pobrano {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllAddresses() " +
                    "- błąd podczas pobierania adresów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllAddresses() - EM zamknięty");
        }
    }

    /**
     * Usuwa adres o podanym identyfikatorze.
     * Operacja jest wykonywana w transakcji.
     * Jeśli adres nie istnieje, operacja jest logowana jako ostrzeżenie.
     *
     * @param id identyfikator Address do usunięcia
     */
    public void removeAddress(int id) {
        logger.debug("removeAddress() - start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Address address = em.find(Address.class, id);
            if (address != null) {
                em.remove(address);
                logger.info("removeAddress() " +
                        "- usunięto adres: {}", address);
            } else {
                logger.warn("removeAddress() " +
                        "- brak adresu o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeAddress() " +
                    "- błąd podczas usuwania adresu "
                    + "o id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeAddress() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje adresy po fragmencie nazwy miejscowości.
     * Wyszukiwanie jest wykonywane bez rozróżniania wielkości liter.
     *
     * @param town fragment nazwy miejscowości
     * @return lista pasujących Address (może być pusta)
     */
    public List<Address> findByTown(String town) {
        logger.debug("findByTown() - town={}", town);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a "
                                    + "WHERE LOWER(a.town) " +
                                    "LIKE LOWER(CONCAT('%', :town, '%'))",
                            Address.class)
                    .setParameter("town", town)
                    .getResultList();
            logger.info("findByTown() " +
                    "- znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByTown() " +
                    "- błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByTown() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje adresy po numerze domu.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @param houseNumber numer domu
     * @return lista pasujących Address
     */
    public List<Address> findByHouseNumber(String houseNumber) {
        logger.debug("findByHouseNumber() " +
                "- houseNumber={}", houseNumber);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a " +
                                    "WHERE a.houseNumber = :houseNumber",
                            Address.class)
                    .setParameter("houseNumber", houseNumber)
                    .getResultList();
            logger.info(
                    "findByHouseNumber() " +
                            "- znaleziono {} adresów",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByHouseNumber() " +
                    "- błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByHouseNumber() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje adresy po numerze mieszkania.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @param apartmentNumber numer mieszkania
     * @return lista pasujących Address
     */
    public List<Address> findByApartmentNumber(String apartmentNumber) {
        logger.debug(
                "findByApartmentNumber() - apartmentNumber={}",
                apartmentNumber);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a "
                                    + "WHERE a.apartmentNumber " +
                                    "= :apartmentNumber",
                            Address.class)
                    .setParameter("apartmentNumber", apartmentNumber)
                    .getResultList();
            logger.info(
                    "findByApartmentNumber() - znaleziono {} adresów",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByApartmentNumber() " +
                    "- błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByApartmentNumber() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje adresy po kodzie pocztowym.
     * W przypadku błędu, wyjątek jest logowany i zwracana jest pusta lista.
     *
     * @param zipCode kod pocztowy
     * @return lista pasujących Address
     */
    public List<Address> findByZipCode(String zipCode) {
        logger.debug("findByZipCode() - zipCode={}", zipCode);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a " +
                                    "WHERE a.zipCode = :zipCode",
                            Address.class)
                    .setParameter("zipCode", zipCode)
                    .getResultList();
            logger.info("findByZipCode() - znaleziono {} adresów",
                    list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByZipCode() " +
                    "- błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByZipCode() - EM zamknięty");
        }
    }

    /**
     * Wyszukuje adresy po mieście.
     * Wyszukiwanie jest wykonywane bez rozróżniania wielkości liter.
     *
     * @param city nazwa miasta
     * @return lista pasujących Address
     */
    public List<Address> findByCity(String city) {
        logger.debug("findByCity() - city={}", city);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a " +
                                    "WHERE LOWER(a.city) = LOWER(:city)",
                            Address.class)
                    .setParameter("city", city)
                    .getResultList();
            logger.info("findByCity() " +
                    "- znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByCity() " +
                    "- błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByCity() - EM zamknięty");
        }
    }

    /**
     * Zamyka wspólną fabrykę EMF (na zakończenie działania aplikacji).
     * Implementacja jest pusta, ponieważ korzystamy z EMFProvider.
     */
    @Override
    public void close() {
    }
}