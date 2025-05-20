/*
 * Classname: Task
 * Version information: 1.0
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

/**
 * Reprezentuje zadanie w systemie.
 */
@Entity
@Table(name = "Zadania")
@Access(AccessType.FIELD)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nazwa;

    @Temporal(TemporalType.DATE)
    private Date data;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String opis;

    /**
     * Konstruktor bezparametrowy wymagany przez JPA.
     */
    public Task() {
        // Pusty konstruktor
    }

    /**
     * Konstruktor pełny.
     *
     * @param nazwa  nazwa zadania
     * @param data   termin wykonania
     * @param status status zadania
     * @param opis   opis zadania
     */
    public Task(String nazwa, Date data, String status, String opis) {
        this.nazwa = nazwa;
        this.data = data;
        this.status = status;
        this.opis = opis;
    }

    // ==================== Gettery i Settery ====================

    public int getId() {
        return id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    /**
     * Zwraca reprezentację tekstową zadania.
     *
     * @return opis zadania
     */
    @Override
    public String toString() {
        return String.format(
                "Zadanie: %s, Termin: %s",
                nazwa,
                data != null ? data.toString() : "brak daty"
        );
    }
}
