package com.finvest.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "reset_token", length = 10)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Role {
        investor, advisor, analyst, admin
    }

    // ── Constructors ──────────────────────────────────────────────────────────
    public User() {}

    public User(String name, String email, String password, Role role) {
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
        this.isActive = true;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Long getId()                       { return id; }
    public String getName()                   { return name; }
    public String getEmail()                  { return email; }
    public String getPassword()               { return password; }
    public Role getRole()                     { return role; }
    public String getResetToken()             { return resetToken; }
    public LocalDateTime getResetTokenExpiry(){ return resetTokenExpiry; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public Boolean getIsActive()              { return isActive; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setId(Long id)                            { this.id = id; }
    public void setName(String name)                      { this.name = name; }
    public void setEmail(String email)                    { this.email = email; }
    public void setPassword(String password)              { this.password = password; }
    public void setRole(Role role)                        { this.role = role; }
    public void setResetToken(String resetToken)          { this.resetToken = resetToken; }
    public void setResetTokenExpiry(LocalDateTime expiry) { this.resetTokenExpiry = expiry; }
    public void setIsActive(Boolean isActive)             { this.isActive = isActive; }
}
