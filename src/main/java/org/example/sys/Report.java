package org.example.sys;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Raporty")
@Access(AccessType.FIELD)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Typ_raportu", nullable = false)
    private String typRaportu;

    @Column(name = "Data_poczatku", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataPoczatku;

    @Column(name = "Data_zakonczenia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataZakonczenia;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee pracownik;

    @Column(name = "Plik", nullable = false)
    private String plik;

    public Report() {}

    public Report(String typRaportu, Date dataPoczatku, Date dataZakonczenia, Employee pracownik, String plik) {
        this.typRaportu = typRaportu;
        this.dataPoczatku = dataPoczatku;
        this.dataZakonczenia = dataZakonczenia;
        this.pracownik = pracownik;
        this.plik = plik;
    }

    // === Gettery i settery ===

    public int getId() {
        return id;
    }

    public String getTypRaportu() {
        return typRaportu;
    }

    public void setTypRaportu(String typRaportu) {
        this.typRaportu = typRaportu;
    }

    public Date getDataPoczatku() {
        return dataPoczatku;
    }

    public void setDataPoczatku(Date dataPoczatku) {
        this.dataPoczatku = dataPoczatku;
    }

    public Date getDataZakonczenia() {
        return dataZakonczenia;
    }

    public void setDataZakonczenia(Date dataZakonczenia) {
        this.dataZakonczenia = dataZakonczenia;
    }

    public Employee getPracownik() {
        return pracownik;
    }

    public void setPracownik(Employee pracownik) {
        this.pracownik = pracownik;
    }

    public String getPlik() {
        return plik;
    }

    public void setPlik(String plik) {
        this.plik = plik;
    }
}
