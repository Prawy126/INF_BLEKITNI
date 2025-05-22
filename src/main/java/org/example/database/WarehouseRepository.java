/*
 * Classname: WarehouseRepository
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
import org.example.sys.Warehouse;

import java.util.List;

/**
 * Repozytorium do zarządzania stanem magazynowym.
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz wyszukiwanie stanów magazynowych.
 */
public class WarehouseRepository {
    private static final Logger logger = LogManager.getLogger(WarehouseRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący fabrykę EntityManagerFactory dla persistence unit "myPU".
     */
    public WarehouseRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono WarehouseRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowy stan magazynowy (pozycję) do bazy.
     *
     * @param stan obiekt Warehouse reprezentujący stan magazynowy do zapisania
     */
    public void dodajStanMagazynowy(Warehouse stan) {
        logger.debug("dodajStanMagazynowy() – start, stan={}", stan);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(stan);
            tx.commit();
            logger.info("dodajStanMagazynowy() – dodano stan: {}", stan);
        } catch (Exception ex) {
            logger.error("dodajStanMagazynowy() – błąd podczas dodawania stanu", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajStanMagazynowy() – EM zamknięty");
        }
    }

    /**
     * Pobiera stan magazynowy (pozycję) na podstawie identyfikatora produktu.
     *
     * @param idProduktu identyfikator produktu
     * @return obiekt Warehouse lub null, jeśli nie znaleziono
     */
    public Warehouse znajdzStanPoIdProduktu(int idProduktu) {
        logger.debug("znajdzStanPoIdProduktu() – start, idProduktu={}", idProduktu);
        EntityManager em = emf.createEntityManager();
        try {
            Warehouse stan = em.find(Warehouse.class, idProduktu);
            logger.info("znajdzStanPoIdProduktu() – znaleziono: {}", stan);
            return stan;
        } catch (Exception ex) {
            logger.error("znajdzStanPoIdProduktu() – błąd podczas wyszukiwania idProduktu={}", idProduktu, ex);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzStanPoIdProduktu() – EM zamknięty");
        }
    }

    /**
     * Pobiera wszystkie stany magazynowe z bazy.
     *
     * @return lista obiektów Warehouse lub pusta lista w przypadku błędu
     */
    public List<Warehouse> pobierzWszystkieStany() {
        logger.debug("pobierzWszystkieStany() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery("SELECT w FROM Warehouse w", Warehouse.class)
                    .getResultList();
            logger.info("pobierzWszystkieStany() – pobrano {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("pobierzWszystkieStany() – błąd podczas pobierania wszystkich stanów", ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieStany() – EM zamknięty");
        }
    }

    /**
     * Usuwa stan magazynowy na podstawie identyfikatora produktu.
     *
     * @param idProduktu identyfikator produktu
     */
    public void usunStan(int idProduktu) {
        logger.debug("usunStan() – start, idProduktu={}", idProduktu);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Warehouse stan = em.find(Warehouse.class, idProduktu);
            if (stan != null) {
                em.remove(stan);
                logger.info("usunStan() – usunięto stan: {}", stan);
            } else {
                logger.warn("usunStan() – brak rekordu dla idProduktu={}", idProduktu);
            }
            tx.commit();
        } catch (Exception ex) {
            logger.error("usunStan() – błąd podczas usuwania idProduktu={}", idProduktu, ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunStan() – EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejący stan magazynowy.
     *
     * @param stan obiekt Warehouse z zaktualizowanymi danymi
     */
    public void aktualizujStan(Warehouse stan) {
        logger.debug("aktualizujStan() – start, stan={}", stan);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(stan);
            tx.commit();
            logger.info("aktualizujStan() – zaktualizowano stan: {}", stan);
        } catch (Exception ex) {
            logger.error("aktualizujStan() – błąd podczas aktualizacji stanu", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujStan() – EM zamknięty");
        }
    }

    /**
     * Ustawia nową ilość produktu w magazynie (soft update).
     *
     * @param productId identyfikator produktu
     * @param newQty    nowa ilość produktu
     */
    public void ustawIloscProduktu(int productId, int newQty) {
        logger.debug("ustawIloscProduktu() – start, productId={}, newQty={}", productId, newQty);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Warehouse w = em.find(Warehouse.class, productId);
            if (w != null) {
                w.setQuantity(newQty);
                em.merge(w);
                logger.info("ustawIloscProduktu() – ilość zaktualizowana: {} → {}", productId, newQty);
            } else {
                logger.warn("ustawIloscProduktu() – brak rekordu dla productId={}", productId);
            }
            tx.commit();
        } catch (Exception ex) {
            logger.error("ustawIloscProduktu() – błąd podczas ustawiania ilości", ex);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("ustawIloscProduktu() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje stany magazynowe o dokładnie podanej ilości.
     *
     * @param ilosc wartość ilości
     * @return lista rekordów Warehouse lub pusta lista
     */
    public List<Warehouse> znajdzPoIlosci(int ilosc) {
        logger.debug("znajdzPoIlosci() – ilosc={}", ilosc);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.ilosc = :ilosc", Warehouse.class)
                    .setParameter("ilosc", ilosc)
                    .getResultList();
            logger.info("znajdzPoIlosci() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("znajdzPoIlosci() – błąd dla ilosc={}", ilosc, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoIlosci() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje stany magazynowe o ilości mniejszej niż podana.
     *
     * @param max maksymalna ilość
     * @return lista rekordów Warehouse lub pusta lista
     */
    public List<Warehouse> znajdzPoIlosciMniejszejNiz(int max) {
        logger.debug("znajdzPoIlosciMniejszejNiz() – max={}", max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.ilosc < :max", Warehouse.class)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("znajdzPoIlosciMniejszejNiz() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("znajdzPoIlosciMniejszejNiz() – błąd dla max={}", max, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoIlosciMniejszejNiz() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje stany magazynowe o ilości większej niż podana.
     *
     * @param min minimalna ilość
     * @return lista rekordów Warehouse lub pusta lista
     */
    public List<Warehouse> znajdzPoIlosciWiekszejNiz(int min) {
        logger.debug("znajdzPoIlosciWiekszejNiz() – min={}", min);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.ilosc > :min", Warehouse.class)
                    .setParameter("min", min)
                    .getResultList();
            logger.info("znajdzPoIlosciWiekszejNiz() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("znajdzPoIlosciWiekszejNiz() – błąd dla min={}", min, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoIlosciWiekszejNiz() – EM zamknięty");
        }
    }

    /**
     * Wyszukuje stany magazynowe o ilości mieszczącej się w podanym przedziale.
     *
     * @param min minimalna ilość
     * @param max maksymalna ilość
     * @return lista rekordów Warehouse lub pusta lista
     */
    public List<Warehouse> znajdzPoIlosciWMiedzy(int min, int max) {
        logger.debug("znajdzPoIlosciWMiedzy() – min={}, max={}", min, max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.ilosc BETWEEN :min AND :max", Warehouse.class)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("znajdzPoIlosciWMiedzy() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("znajdzPoIlosciWMiedzy() – błąd dla min={}, max={}", min, max, ex);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoIlosciWMiedzy() – EM zamknięty");
        }
    }

    /**
     * Zamyka fabrykę EntityManagerFactory, zwalniając wszystkie zasoby.
     * Po wywołaniu tej metody instancja repozytorium nie może być używana.
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
