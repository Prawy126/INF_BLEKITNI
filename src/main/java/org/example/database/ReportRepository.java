package org.example.database;

import org.example.sys.Report;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReportRepository {

    // Przechowujemy raporty w pamięci jako lista – możesz tu podłączyć prawdziwą bazę
    private final List<Report> storage = new ArrayList<>();

    /**
     * Generuje nowy raport (wywołanie Twojej biblioteki) i zapisuje go w repo.
     */
    public void generateReport(String type, LocalDate from, LocalDate to, String filterCriteria) {
        // TODO: tu podmień na wywołanie swojej biblioteki
        String name = type + " [" + from + "→" + to + "]";
        Report r = new Report(name, LocalDate.now(), filterCriteria);
        storage.add(r);
    }

    /** Zwraca wszystkie zapisane raporty */
    public List<Report> fetchAll() {
        return new ArrayList<>(storage);
    }

    /** Usuwa dany raport */
    public void delete(Report r) {
        // usuwamy pierwszy pasujący element
        Iterator<Report> it = storage.iterator();
        while (it.hasNext()) {
            if (it.next() == r) {
                it.remove();
                break;
            }
        }
    }
}