/*
 * Classname: ProductRepository
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
import org.example.sys.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductRepository {

    private static final Logger logger = LogManager.getLogger(ProductRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący fabrykę EntityManagerFactory.
     */
    public ProductRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono ProductRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowy produkt do bazy.
     *
     * @param produkt obiekt produktu do zapisania
     */
    public void dodajProdukt(Product produkt) {
        logger.debug("dodajProdukt() – start, produkt={}", produkt);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(produkt);
            tx.commit();
            logger.info("dodajProdukt() – produkt dodany: {}", produkt);
        } catch (Exception e) {
            logger.error("dodajProdukt() – błąd podczas dodawania produktu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajProdukt() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje produkt o podanym identyfikatorze.
     *
     * @param id identyfikator produktu
     * @return znaleziony produkt lub null, jeśli nie istnieje
     */
    public Product znajdzProduktPoId(int id) {
        logger.debug("znajdzProduktPoId() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Product p = em.find(Product.class, id);
            logger.info("znajdzProduktPoId() – znaleziono: {}", p);
            return p;
        } catch (Exception e) {
            logger.error("znajdzProduktPoId() – błąd podczas wyszukiwania produktu o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzProduktPoId() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie produkty z bazy.
     *
     * @return lista wszystkich produktów, pusta lista w razie błędu
     */
    public List<Product> pobierzWszystkieProdukty() {
        logger.debug("pobierzWszystkieProdukty() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Product> list = em.createQuery("SELECT p FROM Product p", Product.class)
                    .getResultList();
            logger.info("pobierzWszystkieProdukty() – pobrano {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkieProdukty() – błąd podczas pobierania produktów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieProdukty() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera produkty z określonej kategorii.
     *
     * @param kategoria nazwa kategorii
     * @return lista produktów w danej kategorii, pusta lista w razie błędu
     */
    public List<Product> pobierzProduktyPoKategorii(String kategoria) {
        logger.debug("pobierzProduktyPoKategorii() – start, kategoria={}", kategoria);
        EntityManager em = emf.createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.category = :k", Product.class)
                    .setParameter("k", kategoria)
                    .getResultList();
            logger.info("pobierzProduktyPoKategorii() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzProduktyPoKategorii() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzProduktyPoKategorii() – EntityManager zamknięty");
        }
    }
    /**
     * Usuwa produkt o wskazanym identyfikatorze.
     *
     * @param id identyfikator produktu do usunięcia
     */
    public void usunProdukt(int id) {
        logger.debug("usunProdukt() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product p = em.find(Product.class, id);
            if (p != null) {
                em.remove(p);
                logger.info("usunProdukt() – usunięto produkt: {}", p);
            } else {
                logger.warn("usunProdukt() – brak produktu o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunProdukt() – błąd podczas usuwania produktu o id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunProdukt() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje istniejący produkt.
     *
     * @param produkt obiekt produktu z nowymi danymi
     */
    public void aktualizujProdukt(Product produkt) {
        logger.debug("aktualizujProdukt() – start, produkt={}", produkt);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(produkt);
            tx.commit();
            logger.info("aktualizujProdukt() – produkt zaktualizowany: {}", produkt);
        } catch (Exception e) {
            logger.error("aktualizujProdukt() – błąd podczas aktualizacji produktu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujProdukt() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje cenę produktu.
     *
     * @param id       identyfikator produktu
     * @param nowaCena nowa cena ≥ 0
     */
    public void aktualizujCeneProduktu(int id, BigDecimal nowaCena) {
        logger.debug("aktualizujCeneProduktu() – start, id={}, nowaCena={}", id, nowaCena);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product p = em.find(Product.class, id);
            if (p != null && nowaCena.compareTo(BigDecimal.ZERO) >= 0) {
                p.setPrice(nowaCena);
                em.merge(p);
                logger.info("aktualizujCeneProduktu() – cena zaktualizowana: {}", p);
            } else {
                logger.warn("aktualizujCeneProduktu() – niepoprawne dane lub brak produktu id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("aktualizujCeneProduktu() – błąd podczas aktualizacji ceny", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujCeneProduktu() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa wszystkie produkty z danej kategorii.
     *
     * @param kategoria nazwa kategorii
     * @return liczba usuniętych rekordów
     */
    public int usunProduktyZKategorii(String kategoria) {
        logger.debug("usunProduktyZKategorii() – start, kategoria={}", kategoria);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int removed = em.createQuery(
                            "DELETE FROM Product p WHERE p.category = :k")
                    .setParameter("k", kategoria)
                    .executeUpdate();
            tx.commit();
            logger.info("usunProduktyZKategorii() – usunięto {} produktów", removed);
            return removed;
        } catch (Exception e) {
            logger.error("usunProduktyZKategorii() – błąd podczas usuwania produktów", e);
            if (tx.isActive()) tx.rollback();
            return 0;
        } finally {
            em.close();
            logger.debug("usunProduktyZKategorii() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera produkty w zadanym przedziale cenowym.
     *
     * @param minCena cena minimalna
     * @param maxCena cena maksymalna
     * @return lista produktów, pusta lista w razie błędu
     */
    public List<Product> pobierzProduktyWZakresieCenowym(BigDecimal minCena, BigDecimal maxCena) {
        logger.debug("pobierzProduktyWZakresieCenowym() – start, minCena={}, maxCena={}", minCena, maxCena);
        EntityManager em = emf.createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max",
                            Product.class)
                    .setParameter("min", minCena)
                    .setParameter("max", maxCena)
                    .getResultList();
            logger.info("pobierzProduktyWZakresieCenowym() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzProduktyWZakresieCenowym() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzProduktyWZakresieCenowym() – EntityManager zamknięty");
        }
    }

    /**
     * Zwraca listę wszystkich unikalnych kategorii produktów.
     *
     * @return lista kategorii lub pusta lista w razie błędu
     */
    public List<String> pobierzKategorie() {
        logger.debug("pobierzKategorie() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<String> list = em.createQuery(
                            "SELECT DISTINCT p.category FROM Product p", String.class)
                    .getResultList();
            logger.info("pobierzKategorie() – znaleziono {} kategorii", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzKategorie() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzKategorie() – EntityManager zamknięty");
        }
    }

    // === DODATKOWE METODY WYSZUKIWANIA ===

    /**
     * Znajduje produkty zawierające dany fragment w nazwie.
     *
     * @param fragName fragment nazwy
     * @return lista produktów, pusta lista w razie błędu
     */
    public List<Product> znajdzPoNazwie(String fragName) {
        logger.debug("znajdzPoNazwie() – start, fragName={}", fragName);
        EntityManager em = emf.createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :f, '%'))",
                            Product.class)
                    .setParameter("f", fragName)
                    .getResultList();
            logger.info("znajdzPoNazwie() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoNazwie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoNazwie() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje produkty o dokładnej cenie.
     *
     * @param cena dokładna wartość ceny
     * @return lista produktów, pusta lista w razie błędu
     */
    public List<Product> znajdzPoCenieDokladnej(BigDecimal cena) {
        logger.debug("znajdzPoCenieDokladnej() – cena={}", cena);
        EntityManager em = emf.createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price = :c", Product.class)
                    .setParameter("c", cena)
                    .getResultList();
            logger.info("znajdzPoCenieDokladnej() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoCenieDokladnej() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoCenieDokladnej() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje produkty o cenie nie mniejszej niż podana.
     *
     * @param minCena minimalna cena
     * @return lista produktów, pusta lista w razie błędu
     */
    public List<Product> znajdzPoCenieMin(BigDecimal minCena) {
        logger.debug("znajdzPoCenieMin() – minCena={}", minCena);
        EntityManager em = emf.createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price >= :min", Product.class)
                    .setParameter("min", minCena)
                    .getResultList();
            logger.info("znajdzPoCenieMin() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoCenieMin() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoCenieMin() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje produkty o cenie nie większej niż podana.
     *
     * @param maxCena maksymalna cena
     * @return lista produktów, pusta lista w razie błędu
     */
    public List<Product> znajdzPoCenieMax(BigDecimal maxCena) {
        logger.debug("znajdzPoCenieMax() – maxCena={}", maxCena);
        EntityManager em = emf.createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price <= :max", Product.class)
                    .setParameter("max", maxCena)
                    .getResultList();
            logger.info("znajdzPoCenieMax() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoCenieMax() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoCenieMax() – EntityManager zamknięty");
        }
    }

    /**
     * Zamyka fabrykę EntityManagerFactory.
     */
    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}
