package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Report;

import java.util.List;

public class ReportRepository {

    private final EntityManagerFactory emf;

    public ReportRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajRaport(Report raport) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(raport);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public Report znajdzRaportPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Report.class, id);
        } finally {
            em.close();
        }
    }

    public List<Report> pobierzWszystkieRaporty() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Report r", Report.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void usunRaport(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Report raport = em.find(Report.class, id);
            if (raport != null) {
                em.remove(raport);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void aktualizujRaport(Report raport) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(raport);
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
