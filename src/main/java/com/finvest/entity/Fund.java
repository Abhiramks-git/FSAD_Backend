package com.finvest.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "funds")
public class Fund {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String amc;

    @Column(length = 50)
    private String category;

    @Column(name = "sub_category", length = 80)
    private String subCategory;

    @Column(nullable = false)
    private Double nav;

    private Double aum;

    @Column(name = "expense_ratio_regular")
    private Double expenseRatioRegular;

    @Column(name = "expense_ratio_direct")
    private Double expenseRatioDirect;

    @Column(name = "min_sip")
    private Integer minSip;

    @Column(length = 30)
    private String risk;

    @Column(name = "return_1y")
    private Double return1y;

    @Column(name = "return_3y")
    private Double return3y;

    @Column(name = "return_5y")
    private Double return5y;

    @Column(name = "return_since_inception")
    private Double returnSinceInception;

    @Column(name = "inception_date", length = 30)
    private String inceptionDate;

    @Column(name = "exit_load", length = 200)
    private String exitLoad;

    @Column(name = "benchmark_tier1", length = 200)
    private String benchmarkTier1;

    @Column(name = "benchmark_tier2", length = 200)
    private String benchmarkTier2;

    @Column(columnDefinition = "TEXT")
    private String about;

    @Column(name = "ideal_for", columnDefinition = "TEXT")
    private String idealFor;

    @Column(name = "investment_objective", columnDefinition = "TEXT")
    private String investmentObjective;

    @Column(name = "fund_manager_name", length = 100)
    private String fundManagerName;

    @Column(name = "fund_manager_designation", length = 100)
    private String fundManagerDesignation;

    @Column(name = "fund_manager_about", columnDefinition = "TEXT")
    private String fundManagerAbout;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // ── Constructor ───────────────────────────────────────────────────────────
    public Fund() {}

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getId()                   { return id; }
    public String getName()                 { return name; }
    public String getAmc()                  { return amc; }
    public String getCategory()             { return category; }
    public String getSubCategory()          { return subCategory; }
    public Double getNav()                  { return nav; }
    public Double getAum()                  { return aum; }
    public Double getExpenseRatioRegular()  { return expenseRatioRegular; }
    public Double getExpenseRatioDirect()   { return expenseRatioDirect; }
    public Integer getMinSip()              { return minSip; }
    public String getRisk()                 { return risk; }
    public Double getReturn1y()             { return return1y; }
    public Double getReturn3y()             { return return3y; }
    public Double getReturn5y()             { return return5y; }
    public Double getReturnSinceInception() { return returnSinceInception; }
    public String getInceptionDate()        { return inceptionDate; }
    public String getExitLoad()             { return exitLoad; }
    public String getBenchmarkTier1()       { return benchmarkTier1; }
    public String getBenchmarkTier2()       { return benchmarkTier2; }
    public String getAbout()                { return about; }
    public String getIdealFor()             { return idealFor; }
    public String getInvestmentObjective()  { return investmentObjective; }
    public String getFundManagerName()      { return fundManagerName; }
    public String getFundManagerDesignation(){ return fundManagerDesignation; }
    public String getFundManagerAbout()     { return fundManagerAbout; }
    public LocalDateTime getUpdatedAt()     { return updatedAt; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setId(String id)                              { this.id = id; }
    public void setName(String name)                          { this.name = name; }
    public void setAmc(String amc)                            { this.amc = amc; }
    public void setCategory(String category)                  { this.category = category; }
    public void setSubCategory(String subCategory)            { this.subCategory = subCategory; }
    public void setNav(Double nav)                            { this.nav = nav; }
    public void setAum(Double aum)                            { this.aum = aum; }
    public void setExpenseRatioRegular(Double v)              { this.expenseRatioRegular = v; }
    public void setExpenseRatioDirect(Double v)               { this.expenseRatioDirect = v; }
    public void setMinSip(Integer minSip)                     { this.minSip = minSip; }
    public void setRisk(String risk)                          { this.risk = risk; }
    public void setReturn1y(Double v)                         { this.return1y = v; }
    public void setReturn3y(Double v)                         { this.return3y = v; }
    public void setReturn5y(Double v)                         { this.return5y = v; }
    public void setReturnSinceInception(Double v)             { this.returnSinceInception = v; }
    public void setInceptionDate(String v)                    { this.inceptionDate = v; }
    public void setExitLoad(String v)                         { this.exitLoad = v; }
    public void setBenchmarkTier1(String v)                   { this.benchmarkTier1 = v; }
    public void setBenchmarkTier2(String v)                   { this.benchmarkTier2 = v; }
    public void setAbout(String about)                        { this.about = about; }
    public void setIdealFor(String idealFor)                  { this.idealFor = idealFor; }
    public void setInvestmentObjective(String v)              { this.investmentObjective = v; }
    public void setFundManagerName(String v)                  { this.fundManagerName = v; }
    public void setFundManagerDesignation(String v)           { this.fundManagerDesignation = v; }
    public void setFundManagerAbout(String v)                 { this.fundManagerAbout = v; }
}
