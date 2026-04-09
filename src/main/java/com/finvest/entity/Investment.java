package com.finvest.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "investments")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fund_id", nullable = false)
    private Fund fund;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Double units;

    @Column(name = "nav_at_purchase", nullable = false)
    private Double navAtPurchase;

    @Column(name = "investment_date")
    private LocalDate investmentDate;

    @Column(length = 20)
    private String mode = "lumpsum";

    @Column(name = "sip_active")
    private Boolean sipActive = false;

    @Column(name = "sip_day")
    private Integer sipDay;

    @Column(name = "sip_start_date")
    private LocalDate sipStartDate;

    @Column(name = "sip_next_date")
    private LocalDate sipNextDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // ── Constructor ───────────────────────────────────────────────────────────
    public Investment() {}

    // ── Getters ───────────────────────────────────────────────────────────────
    public Long getId()                   { return id; }
    public User getUser()                 { return user; }
    public Fund getFund()                 { return fund; }
    public Double getAmount()             { return amount; }
    public Double getUnits()              { return units; }
    public Double getNavAtPurchase()      { return navAtPurchase; }
    public LocalDate getInvestmentDate()  { return investmentDate; }
    public String getMode()               { return mode; }
    public Boolean getSipActive()         { return sipActive; }
    public Integer getSipDay()            { return sipDay; }
    public LocalDate getSipStartDate()    { return sipStartDate; }
    public LocalDate getSipNextDate()     { return sipNextDate; }
    public LocalDateTime getCreatedAt()   { return createdAt; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setId(Long id)                        { this.id = id; }
    public void setUser(User user)                    { this.user = user; }
    public void setFund(Fund fund)                    { this.fund = fund; }
    public void setAmount(Double amount)              { this.amount = amount; }
    public void setUnits(Double units)                { this.units = units; }
    public void setNavAtPurchase(Double v)            { this.navAtPurchase = v; }
    public void setInvestmentDate(LocalDate v)        { this.investmentDate = v; }
    public void setMode(String mode)                  { this.mode = mode; }
    public void setSipActive(Boolean sipActive)       { this.sipActive = sipActive; }
    public void setSipDay(Integer sipDay)             { this.sipDay = sipDay; }
    public void setSipStartDate(LocalDate v)          { this.sipStartDate = v; }
    public void setSipNextDate(LocalDate v)           { this.sipNextDate = v; }
}
