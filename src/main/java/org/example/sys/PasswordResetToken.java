package org.example.sys;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pracownik_id",
            nullable = false,
            columnDefinition = "INT")
    private Integer userId;

    @Column(name = "reset_code_hash", nullable = false)
    private String resetCodeHash;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* -------- gettery / settery -------- */
    public Integer getId()            { return id; }
    public void setId(Integer id)     { this.id = id; }

    public Integer getUserId()        { return userId; }
    public void setUserId(Integer id) { this.userId = id; }

    public String getResetCodeHash()  { return resetCodeHash; }
    public void setResetCodeHash(String h) { this.resetCodeHash = h; }

    public LocalDateTime getExpirationTime()       { return expirationTime; }
    public void setExpirationTime(LocalDateTime t) { this.expirationTime = t; }

    public boolean isUsed()           { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime t)      { this.createdAt = t; }
}
