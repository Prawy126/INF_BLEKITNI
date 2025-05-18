/*
 * Classname: ProductRepository
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Product;

import java.util.List;

public class ProductRepository {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

    /** Dodaje nowy produkt */
    public void dodajProdukt(Product produkt) {
        executeInsideTx(em -> em.persist(produkt));
    }

    /** Zwraca produkt po ID lub null */
    public Product znajdzProduktPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    /** Wszystkie produkty */
    public List<Product> pobierzWszystkieProdukty() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        } finally {
            em.close();
        }
    }

    /** Produkty z danej kategorii */
    public List<Product> pobierzProduktyPoKategorii(String kategoria) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Product p WHERE p.category = :kategoria", Product.class)
                    .setParameter("kategoria", kategoria)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Zwraca listę unikalnych kategorii */
    public List<String> pobierzKategorie() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT p.category FROM Product p ORDER BY p.category",
                            String.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Aktualizuje dane istniejącego produktu (pojedyncze pola można nadpisać wcześniej pobranym obiektem) */
    public void aktualizujProdukt(Product produkt) {
        executeInsideTx(em -> em.merge(produkt));
    }

    /** Usuwa produkt po ID */
    public void usunProdukt(int id) {
        executeInsideTx(em -> {
            Product p = em.find(Product.class, id);
            if (p != null) em.remove(p);
        });
    }

    /** Aktualizuje cenę produktu */
    public void aktualizujCeneProduktu(int id, double nowaCena) {
        executeInsideTx(em -> {
            Product p = em.find(Product.class, id);
            if (p != null && nowaCena >= 0) {
                p.setPrice(nowaCena);
                em.merge(p);
            }
        });
    }

    /** Usuwa wszystkie produkty z danej kategorii, zwraca liczbę usuniętych */
    public int usunProduktyZKategorii(String kategoria) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int count = em.createQuery("DELETE FROM Product p WHERE p.category = :kategoria")
                    .setParameter("kategoria", kategoria)
                    .executeUpdate();
            tx.commit();
            return count;
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    /** Pobiera produkty w przedziale cenowym */
    public List<Product> pobierzProduktyWZakresieCenowym(double minCena, double maxCena) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Product p WHERE p.price BETWEEN :minCena AND :maxCena",
                            Product.class)
                    .setParameter("minCena", minCena)
                    .setParameter("maxCena", maxCena)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Zamknięcie EntityManagerFactory */
    public void close() {
        if (emf.isOpen()) emf.close();
    }

    private void executeInsideTx(java.util.function.Consumer<EntityManager> action) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}
