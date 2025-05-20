// src/main/java/org/example/database/ReportRepository.java
package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Raport;
import pdf.SalesReportGenerator;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class RaportRepository {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

    public void dodajRaport(Raport report) {
        executeInsideTx(em -> em.persist(report));
    }

    public Raport znajdzRaportPoId(int id) {
        return runReturning(em -> em.find(Raport.class, id));
    }

    public List<Raport> pobierzWszystkieRaporty() {
        return runReturning(em ->
                em.createQuery("SELECT r FROM Raport r", Raport.class).getResultList());
    }

    public void aktualizujRaport(Raport report) {
        executeInsideTx(em -> em.merge(report));
    }

    public void usunRaport(int id) {
        executeInsideTx(em -> {
            Raport r = em.find(Raport.class, id);
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

    public List<Raport> pobierzRaportyPracownikaDzien(int idPracownika, LocalDate date) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Raport r WHERE r.pracownik.id = :idPracownika " +
                                    "AND r.dataPoczatku <= :date AND r.dataZakonczenia >= :date", Raport.class)
                    .setParameter("idPracownika", idPracownika)
                    .setParameter("date", date)
                    .getResultList();
        } finally {
            em.close();
        }
    }


}
