/*
 * Classname: WorkloadRepository
 * Version information: 1.0
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pdf.WorkloadReportGenerator.EmployeeWorkload;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Repozytorium do pobierania danych o obciążeniu pracowników.
 * Umożliwia wykonanie zapytań natywnych do obliczenia sumy godzin
 * przepracowanych przez pracowników w zadanym przedziale dat.
 * Wykorzystywane głównie do generowania raportów obciążenia.
 */
public class WorkloadRepository implements AutoCloseable {

    /**
     * Logger do rejestrowania zdarzeń związanych z klasą WorkloadRepository.
     */
    private static final Logger logger = LogManager.getLogger(
            WorkloadRepository.class);

    /**
     * Domyślny konstruktor – korzysta ze wspólnego EMF z EMFProvider.
     * Operacja jest logowana na poziomie INFO.
     */
    public WorkloadRepository() {
        logger.info("Utworzono WorkloadRepository, korzysta z EMFProvider");
    }

    /**
     * Pobiera listę obciążeń pracowników w zadanym przedziale dat.
     * Wykorzystuje zapytanie natywne SQL do obliczenia łącznej liczby
     * przepracowanych godzin, pogrupowanych według pracownika.
     *
     * @param startDate data początkowa (włącznie)
     * @param endDate   data końcowa (włącznie)
     * @return lista obiektów EmployeeWorkload zawierających imię i nazwisko
     *         pracownika, stanowisko oraz łączną liczbę godzin;
     *         zwraca pustą listę w przypadku błędu
     */
    public List<EmployeeWorkload> getWorkloadData(
            LocalDate startDate,
            LocalDate endDate
    ) {
        logger.debug("getWorkloadData() " +
                        "– start, startDate={}, endDate={}",
                startDate, endDate);
        var em = EMFProvider.get().createEntityManager();
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
                                          COALESCE(zp.czas_trwania_zmiany,
                                           z.czas_trwania_zmiany)
                                      )
                                  ) / 3600
                              , 2)                           AS totalHours
                            FROM   Pracownicy          p
                            JOIN   Zadania_Pracownicy  zp ON zp.Id_pracownika 
                                                                 = p.Id
                            JOIN   Zadania             z  ON z.Id            
                                                                 = zp.Id_zadania
                            WHERE  z.Data BETWEEN :start AND :end
                            GROUP  BY p.Id, p.Imie, p.Nazwisko, p.Stanowisko
                            ORDER  BY p.Nazwisko, p.Imie
                            """,
                            "EmployeeWorkloadMapping")
                    .setParameter("start", Date.valueOf(startDate))
                    .setParameter("end",   Date.valueOf(endDate))
                    .getResultList();

            logger.info("getWorkloadData() – zwrócono {} rekordów",
                    result.size());
            return result;
        } catch (Exception ex) {
            logger.error("getWorkloadData() " +
                    "– błąd podczas pobierania obciążenia "
                    + "pracowników", ex);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("getWorkloadData() – EM zamknięty");
        }
    }

    /**
     * Zamyka wspólną fabrykę EMF (na zakończenie działania aplikacji).
     * Implementacja jest pusta, ponieważ korzystamy z EMFProvider.
     */
    @Override
    public void close() {
    }
}