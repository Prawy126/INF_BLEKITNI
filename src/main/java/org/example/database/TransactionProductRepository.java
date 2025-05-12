package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Product;
import org.example.sys.Transaction;

import java.util.List;

public class TransactionProductRepository {

    private final EntityManagerFactory emf;

    public TransactionProductRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void przypiszProduktDoTransakcji(Transaction transakcja, Product produkt) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction managedTransaction = em.find(Transaction.class, transakcja.getId());
            Product managedProduct = em.find(Product.class, produkt.getId());
            if (managedTransaction != null && managedProduct != null) {
                managedTransaction.getProdukty().add(managedProduct);
                managedProduct.getTransakcje().add(managedTransaction);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void usunProduktZTransakcji(Transaction transakcja, Product produkt) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction managedTransaction = em.find(Transaction.class, transakcja.getId());
            Product managedProduct = em.find(Product.class, produkt.getId());
            if (managedTransaction != null && managedProduct != null) {
                managedTransaction.getProdukty().remove(managedProduct);
                managedProduct.getTransakcje().remove(managedTransaction);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void close() {
        if (emf.isOpen()) emf.close();
    }
}
