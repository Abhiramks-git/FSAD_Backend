package com.finvest.controller;

import com.finvest.dto.InvestmentDto.CreateRequest;
import com.finvest.dto.InvestmentDto.InvestorSummary;
import com.finvest.dto.InvestmentDto.Response;
import com.finvest.entity.User;
import com.finvest.repository.UserRepository;
import com.finvest.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    @Autowired private InvestmentService investService;
    @Autowired private UserRepository    userRepo;

    private Long uid(UserDetails ud) {
        return userRepo.findByEmail(ud.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ── Invest ────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Response> invest(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody CreateRequest req) {
        return ResponseEntity.ok(investService.invest(uid(ud), req));
    }

    // ── Portfolio ─────────────────────────────────────────────────────────────
    @GetMapping("/portfolio")
    public ResponseEntity<List<Response>> getPortfolio(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(investService.getPortfolio(uid(ud)));
    }

    // ── Summary: value, P&L, active SIPs ─────────────────────────────────────
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(investService.getPortfolioSummary(uid(ud)));
    }

    // ── Cancel SIP only (stops future debits, keeps holding) ─────────────────
    @PatchMapping("/{id}/cancel-sip")
    public ResponseEntity<Response> cancelSip(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(investService.cancelSip(id, uid(ud)));
    }

    // ── Cancel / Redeem full investment (deletes the holding from DB) ─────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancelInvestment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(investService.cancelInvestment(id, uid(ud)));
    }

    // ── Advisor: all investors + P&L ─────────────────────────────────────────
    @GetMapping("/advisor/clients")
    @PreAuthorize("hasAnyRole('advisor','admin')")
    public ResponseEntity<List<InvestorSummary>> getAllClients() {
        return ResponseEntity.ok(investService.getAllInvestorSummaries());
    }
}
