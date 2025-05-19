/*
 * Classname: TransactionRepository
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import com.mysql.cj.Session;
import jakarta.persistence.*;
import org.example.sys.PeriodType;
import org.example.sys.Transaction;
import pdf.SalesReportGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static pdf.SalesReportGenerator.PeriodType.DAILY;

public class TransactionRepository {

    private final EntityManagerFactory emf;

    public TransactionRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajTransakcje(Transaction transakcja) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transakcja);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public Transaction znajdzTransakcjePoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Transaction.class, id);
        } finally {
            em.close();
        }
    }

    public List<Transaction> pobierzWszystkieTransakcje() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void usunTransakcje(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction transakcja = em.find(Transaction.class, id);
            if (transakcja != null) {
                em.remove(transakcja);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void aktualizujTransakcje(Transaction transakcja) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(transakcja);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    /**
     * Pobiera transakcje z określonego okresu (dziennie, miesięcznie lub rocznie)
     * i od razu fetche kolekcję t.produkty, żeby uniknąć LazyInitializationException.
     */
    public List<Transaction> getTransactionsByPeriod(LocalDate selectedDate, PeriodType periodType) {
        // 1) Obliczamy zakres od–do (LocalDateTime)
        LocalDateTime od, do_;
        switch (periodType) {
            case DAILY:
                od  = selectedDate.atStartOfDay();
                do_ = selectedDate.atTime(LocalTime.MAX);
                break;
            case MONTHLY:
                od  = selectedDate.withDayOfMonth(1).atStartOfDay();
                do_ = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())
                        .atTime(LocalTime.MAX);
                break;
            case YEARLY:
                od  = selectedDate.withDayOfYear(1).atStartOfDay();
                do_ = selectedDate.withDayOfYear(selectedDate.lengthOfYear())
                        .atTime(LocalTime.MAX);
                break;
            default:
                throw new IllegalArgumentException("Nieznany typ okresu: " + periodType);
        }

        // 2) Konwersja na java.util.Date (bo pole Transaction.data jest Date)
        Date start = Date.from(od.atZone(ZoneId.systemDefault()).toInstant());
        Date end   = Date.from(do_.atZone(ZoneId.systemDefault()).toInstant());

        EntityManager em = emf.createEntityManager();
        try {
            // 3) Budujemy JPQL z fetch-join
            String jpql = """
            SELECT DISTINCT t
            FROM Transaction t
              LEFT JOIN FETCH t.produkty p
            WHERE t.data BETWEEN :start AND :end
            ORDER BY t.data
            """;

            EntityTransaction tx = em.getTransaction();
            tx.begin();

            TypedQuery<Transaction> query = em.createQuery(jpql, Transaction.class);
            query.setParameter("start", start, TemporalType.TIMESTAMP);
            query.setParameter("end",   end,   TemporalType.TIMESTAMP);

            List<Transaction> wynik = query.getResultList();

            tx.commit();
            return wynik;
        } catch (RuntimeException ex) {
            // w razie błędu wycofujemy i przepuszczamy dalej
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }


    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
