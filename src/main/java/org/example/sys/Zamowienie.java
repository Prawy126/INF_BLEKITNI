package org.example.sys;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Zamowienia")
public class Zamowienie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_produktu")
    private Product produkt;

    @Column(name = "Ilosc")
    private int ilosc;

    @Column(name = "Data")
    private Date data;

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Product getProdukt() { return produkt; }
    public void setProdukt(Product produkt) { this.produkt = produkt; }

    public int getIlosc() { return ilosc; }
    public void setIlosc(int ilosc) { this.ilosc = ilosc; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }
}
