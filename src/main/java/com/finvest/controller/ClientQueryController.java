package com.finvest.controller;

import com.finvest.entity.ClientQuery;
import com.finvest.entity.User;
import com.finvest.repository.ClientQueryRepository;
import com.finvest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queries")
@Transactional
public class ClientQueryController {

    @Autowired private ClientQueryRepository queryRepo;
    @Autowired private UserRepository        userRepo;

    private User currentUser(UserDetails ud) {
        return userRepo.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ── Investor: submit a question ────────────────────────────────────────────
    // No role restriction here — any authenticated user (investor) can post
    @PostMapping
    public ResponseEntity<?> askQuestion(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody Map<String, String> body) {

        String question = body.get("question");
        if (question == null || question.isBlank()) {
            Map<String, Object> err = new HashMap<>();
            err.put("ok", false);
            err.put("message", "Question cannot be empty");
            return ResponseEntity.badRequest().body(err);
        }

        User investor = currentUser(ud);
        ClientQuery q = new ClientQuery();
        q.setInvestor(investor);
        q.setQuestion(question.trim());
        q.setStatus("pending");
        queryRepo.save(q);
        return ResponseEntity.ok(toMap(q));
    }

    // ── Investor: see their own questions + answers ────────────────────────────
    @GetMapping("/my")
    public ResponseEntity<List<Map<String, Object>>> myQueries(
            @AuthenticationPrincipal UserDetails ud) {
        User investor = currentUser(ud);
        List<ClientQuery> list = queryRepo.findByInvestorIdOrderByCreatedAtDesc(investor.getId());
        return ResponseEntity.ok(list.stream().map(this::toMap).toList());
    }

    // ── Advisor / Admin: see ALL questions from all investors ──────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('advisor','admin')")
    public ResponseEntity<List<Map<String, Object>>> allQueries() {
        List<ClientQuery> list = queryRepo.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(list.stream().map(this::toMap).toList());
    }

    // ── Advisor / Admin: reply to a question ──────────────────────────────────
    @PatchMapping("/{id}/reply")
    @PreAuthorize("hasAnyRole('advisor','admin')")
    public ResponseEntity<?> reply(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        ClientQuery q = queryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Query not found: " + id));

        String answer = body.get("answer");
        if (answer == null || answer.isBlank()) {
            Map<String, Object> err = new HashMap<>();
            err.put("ok", false);
            err.put("message", "Answer cannot be empty");
            return ResponseEntity.badRequest().body(err);
        }
        q.setAnswer(answer.trim());
        q.setStatus("answered");
        q.setAnsweredAt(LocalDateTime.now());
        queryRepo.save(q);
        return ResponseEntity.ok(toMap(q));
    }

    // ── Map entity → response ──────────────────────────────────────────────────
    private Map<String, Object> toMap(ClientQuery q) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",            q.getId());
        m.put("investorId",    q.getInvestor() != null ? q.getInvestor().getId()    : null);
        m.put("investorName",  q.getInvestor() != null ? q.getInvestor().getName()  : "");
        m.put("investorEmail", q.getInvestor() != null ? q.getInvestor().getEmail() : "");
        m.put("question",      q.getQuestion());
        m.put("answer",        q.getAnswer());
        m.put("status",        q.getStatus());
        m.put("createdAt",     q.getCreatedAt()  != null ? q.getCreatedAt().toString()  : null);
        m.put("answeredAt",    q.getAnsweredAt() != null ? q.getAnsweredAt().toString() : null);
        return m;
    }
}
