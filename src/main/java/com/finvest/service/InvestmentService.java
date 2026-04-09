package com.finvest.service;

import com.finvest.dto.InvestmentDto.CreateRequest;
import com.finvest.dto.InvestmentDto.InvestorSummary;
import com.finvest.dto.InvestmentDto.Response;
import com.finvest.entity.Fund;
import com.finvest.entity.Investment;
import com.finvest.entity.User;
import com.finvest.entity.User.Role;
import com.finvest.repository.FundRepository;
import com.finvest.repository.InvestmentRepository;
import com.finvest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional          // ← keeps the Hibernate session open for ALL methods
public class InvestmentService {

    @Autowired private InvestmentRepository investRepo;
    @Autowired private UserRepository       userRepo;
    @Autowired private FundRepository       fundRepo;

    // ── Invest (lumpsum or SIP) ────────────────────────────────────────────────
    public Response invest(Long userId, CreateRequest req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Fund fund = fundRepo.findById(req.getFundId())
                .orElseThrow(() -> new RuntimeException("Fund not found: " + req.getFundId()));

        if (req.getAmount() < fund.getMinSip()) {
            throw new RuntimeException("Minimum investment is Rs." + fund.getMinSip());
        }

        boolean isSip  = "sip".equalsIgnoreCase(req.getMode());
        int sipDay     = (req.getSipDay() != null && req.getSipDay() >= 1 && req.getSipDay() <= 28)
                         ? req.getSipDay() : LocalDate.now().getDayOfMonth();
        double units   = req.getAmount() / fund.getNav();

        Investment inv = new Investment();
        inv.setUser(user);
        inv.setFund(fund);
        inv.setAmount(req.getAmount());
        inv.setUnits(units);
        inv.setNavAtPurchase(fund.getNav());
        inv.setInvestmentDate(LocalDate.now());
        inv.setMode(isSip ? "sip" : "lumpsum");
        inv.setSipActive(isSip);
        inv.setSipDay(isSip ? sipDay : null);
        inv.setSipStartDate(isSip ? LocalDate.now() : null);
        inv.setSipNextDate(isSip ? nextSipDate(sipDay) : null);

        investRepo.save(inv);
        return buildResponse(inv, fund);
    }

    // ── Full portfolio list ────────────────────────────────────────────────────
    public List<Response> getPortfolio(Long userId) {
        return investRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(inv -> buildResponse(inv, inv.getFund()))
                .collect(Collectors.toList());
    }

    // ── Portfolio summary: value, P&L, active SIP count ───────────────────────
    public Map<String, Object> getPortfolioSummary(Long userId) {
        List<Investment> list = investRepo.findByUserIdOrderByCreatedAtDesc(userId);

        double totalInvested = 0, currentValue = 0;
        long   activeSips    = 0;

        for (Investment inv : list) {
            Fund fund    = inv.getFund();            // safe: session is open (@Transactional)
            double nav   = fund != null ? fund.getNav() : inv.getNavAtPurchase();
            double cv    = inv.getUnits() * nav;

            totalInvested += inv.getAmount();
            currentValue  += cv;
            if ("sip".equals(inv.getMode()) && Boolean.TRUE.equals(inv.getSipActive())) {
                activeSips++;
            }
        }

        double pl    = currentValue - totalInvested;
        double plPct = totalInvested > 0 ? (pl / totalInvested) * 100 : 0;

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalInvested", Math.round(totalInvested * 100.0) / 100.0);
        m.put("currentValue",  Math.round(currentValue  * 100.0) / 100.0);
        m.put("profitLoss",    Math.round(pl   * 100.0) / 100.0);
        m.put("profitLossPct", Math.round(plPct * 100.0) / 100.0);
        m.put("activeSips",    activeSips);
        m.put("holdingsCount", list.size());
        return m;
    }

    // ── Cancel SIP only (stops future debits, keeps the holding) ──────────────
    public Response cancelSip(Long investmentId, Long userId) {
        Investment inv = investRepo.findById(investmentId)
                .orElseThrow(() -> new RuntimeException("Investment not found"));
        if (!inv.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }
        inv.setSipActive(false);
        inv.setSipNextDate(null);
        investRepo.save(inv);
        return buildResponse(inv, inv.getFund());
    }

    // ── Redeem / cancel full investment (deletes row) ─────────────────────────
    public Map<String, Object> cancelInvestment(Long investmentId, Long userId) {
        Investment inv = investRepo.findById(investmentId)
                .orElseThrow(() -> new RuntimeException("Investment not found"));
        if (!inv.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to cancel this investment");
        }

        Fund   fund         = inv.getFund();
        double nav          = fund != null ? fund.getNav() : inv.getNavAtPurchase();
        double currentValue = inv.getUnits() * nav;
        double pl           = currentValue - inv.getAmount();
        double plPct        = inv.getAmount() > 0 ? (pl / inv.getAmount()) * 100 : 0;

        investRepo.deleteById(investmentId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id",             investmentId);
        result.put("fundName",       fund != null ? fund.getName() : "");
        result.put("amountInvested", inv.getAmount());
        result.put("currentValue",   Math.round(currentValue * 100.0) / 100.0);
        result.put("profitLoss",     Math.round(pl   * 100.0) / 100.0);
        result.put("profitLossPct",  Math.round(plPct * 100.0) / 100.0);
        result.put("message",        "Investment redeemed successfully");
        return result;
    }

    // ── Advisor: all investors with their full holdings and P&L ───────────────
    public List<InvestorSummary> getAllInvestorSummaries() {
        List<Investment> allInv = investRepo.findByUserRole(Role.investor);
        Map<Long, List<Investment>> byUser = allInv.stream()
                .collect(Collectors.groupingBy(i -> i.getUser().getId()));

        List<User> allInvestors = userRepo.findAll().stream()
                .filter(u -> u.getRole() == Role.investor)
                .collect(Collectors.toList());

        List<InvestorSummary> result = new ArrayList<>();
        for (User investor : allInvestors) {
            List<Investment> holdings = byUser.getOrDefault(investor.getId(), new ArrayList<>());

            double invested = 0, current = 0;
            long   sips     = 0;
            for (Investment inv : holdings) {
                Fund   fund = inv.getFund();
                double nav  = fund != null ? fund.getNav() : inv.getNavAtPurchase();
                invested += inv.getAmount();
                current  += inv.getUnits() * nav;
                if ("sip".equals(inv.getMode()) && Boolean.TRUE.equals(inv.getSipActive())) sips++;
            }
            double pl    = current - invested;
            double plPct = invested > 0 ? (pl / invested) * 100 : 0;

            InvestorSummary s = new InvestorSummary();
            s.setUserId(investor.getId());
            s.setUserName(investor.getName());
            s.setUserEmail(investor.getEmail());
            s.setTotalInvested(Math.round(invested * 100.0) / 100.0);
            s.setCurrentValue(Math.round(current  * 100.0) / 100.0);
            s.setProfitLoss(Math.round(pl   * 100.0) / 100.0);
            s.setProfitLossPct(Math.round(plPct * 100.0) / 100.0);
            s.setActiveSips((int) sips);
            s.setHoldingsCount(holdings.size());
            s.setHoldings(holdings.stream()
                    .map(inv -> buildResponse(inv, inv.getFund()))
                    .collect(Collectors.toList()));
            result.add(s);
        }
        return result;
    }

    // ── Private helpers ───────────────────────────────────────────────────────
    private LocalDate nextSipDate(int sipDay) {
        LocalDate now  = LocalDate.now();
        LocalDate next = now.withDayOfMonth(sipDay);
        if (!next.isAfter(now)) next = next.plusMonths(1);
        return next;
    }

    /**
     * Build a Response DTO from an Investment and its Fund.
     * fund is passed explicitly to avoid any lazy-load issues.
     */
    private Response buildResponse(Investment inv, Fund fund) {
        double nav = (fund != null && fund.getNav() != null)
                     ? fund.getNav() : inv.getNavAtPurchase();
        double cv  = inv.getUnits() * nav;
        double pl  = cv - inv.getAmount();

        Response r = new Response();
        r.setId(inv.getId());
        r.setFundId(fund != null ? fund.getId()      : "");
        r.setFundName(fund != null ? fund.getName()  : "Unknown Fund");
        r.setFundCategory(fund != null ? fund.getCategory() : "");
        r.setFundRisk(fund != null ? fund.getRisk()  : "");
        r.setAmount(inv.getAmount());
        r.setUnits(Math.round(inv.getUnits() * 10000.0) / 10000.0);
        r.setNavAtPurchase(inv.getNavAtPurchase());
        r.setCurrentNav(nav);
        r.setCurrentValue(Math.round(cv * 100.0) / 100.0);
        r.setProfitLoss(Math.round(pl * 100.0) / 100.0);
        r.setProfitLossPct(inv.getAmount() > 0
                ? Math.round((pl / inv.getAmount() * 100) * 100.0) / 100.0 : 0.0);
        r.setInvestmentDate(inv.getInvestmentDate());
        r.setMode(inv.getMode());
        r.setSipActive(Boolean.TRUE.equals(inv.getSipActive()));
        r.setSipDay(inv.getSipDay());
        r.setSipNextDate(inv.getSipNextDate());
        return r;
    }
}
