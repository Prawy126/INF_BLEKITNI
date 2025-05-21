/*
 * Classname: WorkloadRepository
 * Version information: 1.2
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pdf.WorkloadReportGenerator.EmployeeWorkload;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Collections;

/**
 * Repozytorium do pobierania danych o obciążeniu pracowników.
 */
public class WorkloadRepository implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(WorkloadRepository.class);
    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("myPU");

    /**
     * Pobiera listę obciążeń pracowników w zadanym przedziale dat.
     *
     * @param startDate data początkowa (włącznie)
     * @param endDate   data końcowa (włącznie)
     * @return lista EmployeeWorkload lub pusta lista przy błędzie
     */
    public List<EmployeeWorkload> getWorkloadData(LocalDate startDate,
                                                  LocalDate endDate) {
        logger.debug("getWorkloadData() – start, startDate={}, endDate={}", startDate, endDate);
        EntityManager em = emf.createEntityManager();
        try {
            @SuppressWarnings("unchecked")
            List<EmployeeWorkload> result = em.createNativeQuery(
                            // language=SQL
                            """
                            SELECT
                              CONCAT(p.Imie, ' ', p.Nazwisko) AS employeeName,
                              p.Stanowisko                  AS department,
                              ROUND(
                                  SUM(
                                      TIME_TO_SEC(
                                          COALESCE(zp.czas_trwania_zmiany, z.czas_trwania_zmiany)
                                      )
                                  ) / 3600
                              , 2)                           AS totalHours
                            FROM   Pracownicy          p
                            JOIN   Zadania_Pracownicy  zp ON zp.Id_pracownika = p.Id
                            JOIN   Zadania             z  ON z.Id            = zp.Id_zadania
                            WHERE  z.Data BETWEEN :start AND :end
                            GROUP  BY p.Id, p.Imie, p.Nazwisko, p.Stanowisko
                            ORDER  BY p.Nazwisko, p.Imie
                            """,
                            "EmployeeWorkloadMapping")
                    .setParameter("start", Date.valueOf(startDate))
                    .setParameter("end",   Date.valueOf(endDate))
                    .getResultList();
            logger.info("getWorkloadData() – zwrócono {} rekordów", result.size());
            return result;
        } catch (Exception ex) {
            logger.error("getWorkloadData() – błąd podczas pobierania obciążenia pracowników", ex);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("getWorkloadData() – EntityManager zamknięty");
        }
    }

    /**
     * Zamyka fabrykę EntityManagerFactory.
     */
    @Override
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
