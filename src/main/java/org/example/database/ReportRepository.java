// src/main/java/org/example/database/ReportRepository.java
package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Report;

import java.io.File;
import java.util.List;

public class ReportRepository {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

    public void dodajRaport(Report report) {
        executeInsideTx(em -> em.persist(report));
    }

    public Report znajdzRaportPoId(int id) {
        return runReturning(em -> em.find(Report.class, id));
    }

    public List<Report> pobierzWszystkieRaporty() {
        return runReturning(em ->
                em.createQuery("SELECT r FROM Report r", Report.class).getResultList());
    }

    public void aktualizujRaport(Report report) {
        executeInsideTx(em -> em.merge(report));
    }

    public void usunRaport(int id) {
        executeInsideTx(em -> {
            Report r = em.find(Report.class, id);
            if (r != null) {
                File f = new File(r.getSciezkaPliku());
                if (f.exists()) f.delete();
                em.remove(r);
            }
        });
    }

    public void close() { if (emf.isOpen()) emf.close(); }

    /* ===== pomocnicze ===== */
    private void executeInsideTx(java.util.function.Consumer<EntityManager> action) {
        runReturning(em -> { action.accept(em); return null; });
    }
    private <T> T runReturning(java.util.function.Function<EntityManager,T> func) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T res = func.apply(em);
            tx.commit();
            return res;
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}
