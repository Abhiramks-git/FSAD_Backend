package com.finvest.controller;

import com.finvest.entity.Fund;
import com.finvest.service.FundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funds")
public class FundController {

    @Autowired
    private FundService fundService;

    // ── Public: anyone can browse funds ───────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Fund>> getAllFunds(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String risk,
            @RequestParam(required = false) String q) {

        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(fundService.searchFunds(q));
        }
        if (category != null) {
            return ResponseEntity.ok(fundService.getFundsByCategory(category));
        }
        if (risk != null) {
            return ResponseEntity.ok(fundService.getFundsByRisk(risk));
        }
        return ResponseEntity.ok(fundService.getAllFunds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fund> getFund(@PathVariable String id) {
        return ResponseEntity.ok(fundService.getFundById(id));
    }

    // ── Admin: full CRUD ───────────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Fund> createFund(@RequestBody Fund fund) {
        return ResponseEntity.ok(fundService.createFund(fund));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Fund> updateFund(@PathVariable String id, @RequestBody Fund fund) {
        return ResponseEntity.ok(fundService.updateFund(id, fund));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteFund(@PathVariable String id) {
        fundService.deleteFund(id);
        Map<String, Object> resp = new HashMap<>();
        resp.put("ok", true);
        resp.put("message", "Fund deleted");
        return ResponseEntity.ok(resp);
    }

    // ── Analyst: update NAV and returns only ──────────────────────────────────
    @PatchMapping("/{id}/performance")
    @PreAuthorize("hasAnyRole('analyst','admin')")
    public ResponseEntity<Fund> updatePerformance(
            @PathVariable String id,
            @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(fundService.updatePerformance(
                id,
                body.get("nav"),
                body.get("return1y"),
                body.get("return3y"),
                body.get("return5y")));
    }
}
