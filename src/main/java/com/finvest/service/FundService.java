package com.finvest.service;

import com.finvest.entity.Fund;
import com.finvest.repository.FundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FundService {

    @Autowired
    private FundRepository fundRepo;

    public List<Fund> getAllFunds() {
        return fundRepo.findAll();
    }

    public Fund getFundById(String id) {
        return fundRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Fund not found: " + id));
    }

    public List<Fund> searchFunds(String q) {
        return fundRepo.findByNameContainingIgnoreCaseOrAmcContainingIgnoreCase(q, q);
    }

    public List<Fund> getFundsByCategory(String category) {
        return fundRepo.findByCategory(category);
    }

    public List<Fund> getFundsByRisk(String risk) {
        return fundRepo.findByRisk(risk);
    }

    public Fund createFund(Fund fund) {
        return fundRepo.save(fund);
    }

    public Fund updateFund(String id, Fund updated) {
        Fund f = getFundById(id);
        f.setName(updated.getName());
        f.setAmc(updated.getAmc());
        f.setCategory(updated.getCategory());
        f.setSubCategory(updated.getSubCategory());
        f.setNav(updated.getNav());
        f.setAum(updated.getAum());
        f.setExpenseRatioRegular(updated.getExpenseRatioRegular());
        f.setExpenseRatioDirect(updated.getExpenseRatioDirect());
        f.setMinSip(updated.getMinSip());
        f.setRisk(updated.getRisk());
        f.setReturn1y(updated.getReturn1y());
        f.setReturn3y(updated.getReturn3y());
        f.setReturn5y(updated.getReturn5y());
        f.setReturnSinceInception(updated.getReturnSinceInception());
        f.setExitLoad(updated.getExitLoad());
        f.setAbout(updated.getAbout());
        f.setIdealFor(updated.getIdealFor());
        f.setInvestmentObjective(updated.getInvestmentObjective());
        f.setBenchmarkTier1(updated.getBenchmarkTier1());
        f.setBenchmarkTier2(updated.getBenchmarkTier2());
        f.setFundManagerName(updated.getFundManagerName());
        f.setFundManagerDesignation(updated.getFundManagerDesignation());
        f.setFundManagerAbout(updated.getFundManagerAbout());
        return fundRepo.save(f);
    }

    public Fund updatePerformance(String id, Double nav,
                                   Double r1y, Double r3y, Double r5y) {
        Fund f = getFundById(id);
        if (nav != null) f.setNav(nav);
        if (r1y != null) f.setReturn1y(r1y);
        if (r3y != null) f.setReturn3y(r3y);
        if (r5y != null) f.setReturn5y(r5y);
        return fundRepo.save(f);
    }

    public void deleteFund(String id) {
        fundRepo.deleteById(id);
    }
}
