/*
 * Classname: PasswordResetToken
 * Version information: 1.0
 * Date: 2025-06-04
 * Copyright notice: © BŁĘKITNI
 */

        package org.example.sys;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Klasa reprezentująca token do resetowania hasła użytkownika w
 * systemie.
 * Mapowana na tabelę "password_reset_tokens" w bazie danych.
 * Zawiera informacje o kodzie resetowania, użytkowniku, terminie
 * ważności
 * oraz statusie wykorzystania tokenu.
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    /**
     * Unikalny identyfikator tokenu generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Identyfikator pracownika, dla którego token został wygenerowany.
     * Referencja do pracownika w tabeli pracowników.
     */
    @Column(name = "pracownik_id",
            nullable = false,
            columnDefinition = "INT")
    private Integer userId;

    /**
     * Zahaszowana wartość kodu resetującego.
     * Przechowywana w formie hasza dla bezpieczeństwa.
     */
    @Column(name = "reset_code_hash", nullable = false)
    private String resetCodeHash;

    /**
     * Termin ważności tokenu resetującego.
     * Po upłynięciu tego terminu token nie może być użyty.
     */
    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    /**
     * Flaga określająca czy token został już wykorzystany.
     * Zapobiega wielokrotnemu użyciu tego samego tokenu.
     */
    @Column(name = "used", nullable = false)
    private boolean used;

    /**
     * Data i czas utworzenia tokenu.
     * Wartość jest ustawiana tylko raz podczas tworzenia tokenu.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* -------- gettery / settery -------- */

    /**
     * Pobiera identyfikator tokenu.
     *
     * @return identyfikator tokenu
     */
    public Integer getId() {
        return id;
    }

    /**
     * Ustawia identyfikator tokenu.
     *
     * @param id nowy identyfikator tokenu
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Pobiera identyfikator użytkownika, dla którego token
     * został wygenerowany.
     *
     * @return identyfikator użytkownika
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Ustawia identyfikator użytkownika powiązanego z tokenem.
     *
     * @param id identyfikator użytkownika
     */
    public void setUserId(Integer id) {
        this.userId = id;
    }

    /**
     * Pobiera zahaszowaną wartość kodu resetującego.
     *
     * @return hasz kodu resetującego
     */
    public String getResetCodeHash() {
        return resetCodeHash;
    }

    /**
     * Ustawia zahaszowaną wartość kodu resetującego.
     *
     * @param h zahaszowana wartość kodu
     */
    public void setResetCodeHash(String h) {
        this.resetCodeHash = h;
    }

    /**
     * Pobiera termin ważności tokenu.
     *
     * @return data i czas wygaśnięcia tokenu
     */
    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    /**
     * Ustawia termin ważności tokenu.
     *
     * @param t data i czas wygaśnięcia tokenu
     */
    public void setExpirationTime(LocalDateTime t) {
        this.expirationTime = t;
    }

    /**
     * Sprawdza czy token został już wykorzystany.
     *
     * @return true jeśli token został użyty, false w przeciwnym przypadku
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Ustawia status wykorzystania tokenu.
     *
     * @param used nowy status wykorzystania tokenu
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * Pobiera datę i czas utworzenia tokenu.
     *
     * @return data i czas utworzenia tokenu
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Ustawia datę i czas utworzenia tokenu.
     *
     * @param t data i czas utworzenia tokenu
     */
    public void setCreatedAt(LocalDateTime t) {
        this.createdAt = t;
    }
}