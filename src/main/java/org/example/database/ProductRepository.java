package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Product;

import java.util.List;

public class ProductRepository {

    private final EntityManagerFactory emf;

    public ProductRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajProdukt(Product produkt) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(produkt);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public Product znajdzProduktPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public List<Product> pobierzWszystkieProdukty() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void usunProdukt(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product produkt = em.find(Product.class, id);
            if (produkt != null) {
                em.remove(produkt);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void aktualizujProdukt(Product produkt) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(produkt);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
