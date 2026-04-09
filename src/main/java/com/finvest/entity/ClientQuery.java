package com.finvest.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "client_queries")
public class ClientQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "investor_id", nullable = false)
    private User investor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(length = 20)
    private String status = "pending"; // pending | answered

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // Constructor
    public ClientQuery() {}

    // Getters
    public Long getId()                    { return id; }
    public User getInvestor()              { return investor; }
    public String getQuestion()            { return question; }
    public String getAnswer()              { return answer; }
    public String getStatus()              { return status; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public LocalDateTime getAnsweredAt()   { return answeredAt; }

    // Setters
    public void setId(Long id)                          { this.id = id; }
    public void setInvestor(User investor)              { this.investor = investor; }
    public void setQuestion(String question)            { this.question = question; }
    public void setAnswer(String answer)                { this.answer = answer; }
    public void setStatus(String status)                { this.status = status; }
    public void setAnsweredAt(LocalDateTime v)          { this.answeredAt = v; }
}
