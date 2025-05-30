/*
 * Classname: ProductRepository
 * Version information: 1.5
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductRepository implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(ProductRepository.class);

    public ProductRepository() {
        logger.info("Utworzono ProductRepository, EMF={}", EMFProvider.get());
    }

    public void addProduct(Product product) {
        logger.debug("addProduct() – start, product={}", product);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(product);
            tx.commit();
            logger.info("addProduct() – product dodany: {}", product);
        } catch (Exception e) {
            logger.error("addProduct() – błąd podczas dodawania produktu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addProduct() – EM zamknięty");
        }
    }

    public Product findProductById(int id) {
        logger.debug("findProductById() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            Product p = em.find(Product.class, id);
            logger.info("findProductById() – znaleziono: {}", p);
            return p;
        } catch (Exception e) {
            logger.error("findProductById() – błąd podczas wyszukiwania produktu id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findProductById() – EM zamknięty");
        }
    }

    public List<Product> getAllProducts() {
        logger.debug("getAllProducts() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Product> list = em.createQuery("SELECT p FROM Product p", Product.class)
                    .getResultList();
            logger.info("getAllProducts() – pobrano {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllProducts() – błąd podczas pobierania produktów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllProducts() – EM zamknięty");
        }
    }

    public List<Product> getProductsByCategory(String category) {
        logger.debug("getProductsByCategory() – start, category={}", category);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.category = :k", Product.class)
                    .setParameter("k", category)
                    .getResultList();
            logger.info("getProductsByCategory() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getProductsByCategory() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getProductsByCategory() – EM zamknięty");
        }
    }

    public void removeProduct(int id) {
        logger.debug("removeProduct() – start, id={}", id);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product p = em.find(Product.class, id);
            if (p != null) {
                em.remove(p);
                logger.info("removeProduct() – usunięto produkt: {}", p);
            } else {
                logger.warn("removeProduct() – brak produktu o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeProduct() – błąd podczas usuwania produktu id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeProduct() – EM zamknięty");
        }
    }

    public void updateProduct(Product product) {
        logger.debug("updateProduct() – start, product={}", product);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(product);
            tx.commit();
            logger.info("updateProduct() – product zaktualizowany: {}", product);
        } catch (Exception e) {
            logger.error("updateProduct() – błąd podczas aktualizacji produktu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateProduct() – EM zamknięty");
        }
    }

    public void updateProductPrice(int id, BigDecimal price) {
        logger.debug("updateProductPrice() – start, id={}, price={}", id, price);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product p = em.find(Product.class, id);
            if (p != null && price.compareTo(BigDecimal.ZERO) >= 0) {
                p.setPrice(price);
                em.merge(p);
                logger.info("updateProductPrice() – cena zaktualizowana: {}", p);
            } else {
                logger.warn("updateProductPrice() – niepoprawne dane lub brak produktu id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("updateProductPrice() – błąd podczas aktualizacji ceny", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateProductPrice() – EM zamknięty");
        }
    }

    public int removeProductsFromCategory(String category) {
        logger.debug("removeProductsFromCategory() – start, category={}", category);
        EntityManager em = EMFProvider.get().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int count = em.createQuery("DELETE FROM Product p WHERE p.category = :k")
                    .setParameter("k", category)
                    .executeUpdate();
            tx.commit();
            logger.info("removeProductsFromCategory() – usunięto {} produktów", count);
            return count;
        } catch (Exception e) {
            logger.error("removeProductsFromCategory() – błąd podczas usuwania produktów", e);
            if (tx.isActive()) tx.rollback();
            return 0;
        } finally {
            em.close();
            logger.debug("removeProductsFromCategory() – EM zamknięty");
        }
    }

    public List<Product> getPriceRangeProducts(BigDecimal min, BigDecimal max) {
        logger.debug("getPriceRangeProducts() – min={}, max={}", min, max);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max", Product.class)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("getPriceRangeProducts() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getPriceRangeProducts() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getPriceRangeProducts() – EM zamknięty");
        }
    }

    public List<String> getCategories() {
        logger.debug("getCategories() – start");
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<String> list = em.createQuery(
                            "SELECT DISTINCT p.category FROM Product p", String.class)
                    .getResultList();
            logger.info("getCategories() – znaleziono {} kategorii", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getCategories() – błąd podczas pobierania kategorii", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getCategories() – EM zamknięty");
        }
    }

    public List<Product> findByName(String fragName) {
        logger.debug("findByName() – fragName={}", fragName);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :f, '%'))", Product.class)
                    .setParameter("f", fragName)
                    .getResultList();
            logger.info("findByName() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByName() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByName() – EM zamknięty");
        }
    }

    public List<Product> findByExactPrice(BigDecimal price) {
        logger.debug("findByExactPrice() – price={}", price);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price = :c", Product.class)
                    .setParameter("c", price)
                    .getResultList();
            logger.info("findByExactPrice() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByExactPrice() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByExactPrice() – EM zamknięty");
        }
    }

    public List<Product> findByMinPrice(BigDecimal minPrice) {
        logger.debug("findByMinPrice() – minPrice={}", minPrice);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price >= :min", Product.class)
                    .setParameter("min", minPrice)
                    .getResultList();
            logger.info("findByMinPrice() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByMinPrice() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByMinPrice() – EM zamknięty");
        }
    }

    public List<Product> findByMaxPrice(BigDecimal maxPrice) {
        logger.debug("findByMaxPrice() – maxPrice={}", maxPrice);
        EntityManager em = EMFProvider.get().createEntityManager();
        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price <= :max", Product.class)
                    .setParameter("max", maxPrice)
                    .getResultList();
            logger.info("findByMaxPrice() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByMaxPrice() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByMaxPrice() – EM zamknięty");
        }
    }

    @Override
    public void close() {
    }
}

