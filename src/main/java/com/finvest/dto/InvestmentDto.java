package com.finvest.dto;

import java.time.LocalDate;
import java.util.List;

public class InvestmentDto {

    // ── Create request ────────────────────────────────────────────────────────
    public static class CreateRequest {
        private String  fundId;
        private Double  amount;
        private String  mode;
        private Integer sipDay;

        public String  getFundId()            { return fundId; }
        public void    setFundId(String v)    { this.fundId = v; }
        public Double  getAmount()            { return amount; }
        public void    setAmount(Double v)    { this.amount = v; }
        public String  getMode()              { return mode; }
        public void    setMode(String v)      { this.mode = v; }
        public Integer getSipDay()            { return sipDay; }
        public void    setSipDay(Integer v)   { this.sipDay = v; }
    }

    // ── Single holding response ───────────────────────────────────────────────
    public static class Response {
        private Long      id;
        private String    fundId;
        private String    fundName;
        private String    fundCategory;
        private String    fundRisk;
        private Double    amount;
        private Double    units;
        private Double    navAtPurchase;
        private Double    currentNav;
        private Double    currentValue;
        private Double    profitLoss;
        private Double    profitLossPct;
        private LocalDate investmentDate;
        private String    mode;
        private Boolean   sipActive;
        private Integer   sipDay;
        private LocalDate sipNextDate;

        public Long      getId()                      { return id; }
        public void      setId(Long v)                { this.id = v; }
        public String    getFundId()                  { return fundId; }
        public void      setFundId(String v)          { this.fundId = v; }
        public String    getFundName()                { return fundName; }
        public void      setFundName(String v)        { this.fundName = v; }
        public String    getFundCategory()            { return fundCategory; }
        public void      setFundCategory(String v)    { this.fundCategory = v; }
        public String    getFundRisk()                { return fundRisk; }
        public void      setFundRisk(String v)        { this.fundRisk = v; }
        public Double    getAmount()                  { return amount; }
        public void      setAmount(Double v)          { this.amount = v; }
        public Double    getUnits()                   { return units; }
        public void      setUnits(Double v)           { this.units = v; }
        public Double    getNavAtPurchase()           { return navAtPurchase; }
        public void      setNavAtPurchase(Double v)   { this.navAtPurchase = v; }
        public Double    getCurrentNav()              { return currentNav; }
        public void      setCurrentNav(Double v)      { this.currentNav = v; }
        public Double    getCurrentValue()            { return currentValue; }
        public void      setCurrentValue(Double v)    { this.currentValue = v; }
        public Double    getProfitLoss()              { return profitLoss; }
        public void      setProfitLoss(Double v)      { this.profitLoss = v; }
        public Double    getProfitLossPct()           { return profitLossPct; }
        public void      setProfitLossPct(Double v)   { this.profitLossPct = v; }
        public LocalDate getInvestmentDate()          { return investmentDate; }
        public void      setInvestmentDate(LocalDate v){ this.investmentDate = v; }
        public String    getMode()                    { return mode; }
        public void      setMode(String v)            { this.mode = v; }
        public Boolean   getSipActive()               { return sipActive; }
        public void      setSipActive(Boolean v)      { this.sipActive = v; }
        public Integer   getSipDay()                  { return sipDay; }
        public void      setSipDay(Integer v)         { this.sipDay = v; }
        public LocalDate getSipNextDate()             { return sipNextDate; }
        public void      setSipNextDate(LocalDate v)  { this.sipNextDate = v; }
    }

    // ── Advisor summary per investor ──────────────────────────────────────────
    public static class InvestorSummary {
        private Long           userId;
        private String         userName;
        private String         userEmail;
        private Double         totalInvested;
        private Double         currentValue;
        private Double         profitLoss;
        private Double         profitLossPct;
        private int            activeSips;
        private int            holdingsCount;
        private List<Response> holdings;

        public Long           getUserId()                      { return userId; }
        public void           setUserId(Long v)                { this.userId = v; }
        public String         getUserName()                    { return userName; }
        public void           setUserName(String v)            { this.userName = v; }
        public String         getUserEmail()                   { return userEmail; }
        public void           setUserEmail(String v)           { this.userEmail = v; }
        public Double         getTotalInvested()               { return totalInvested; }
        public void           setTotalInvested(Double v)       { this.totalInvested = v; }
        public Double         getCurrentValue()                { return currentValue; }
        public void           setCurrentValue(Double v)        { this.currentValue = v; }
        public Double         getProfitLoss()                  { return profitLoss; }
        public void           setProfitLoss(Double v)          { this.profitLoss = v; }
        public Double         getProfitLossPct()               { return profitLossPct; }
        public void           setProfitLossPct(Double v)       { this.profitLossPct = v; }
        public int            getActiveSips()                  { return activeSips; }
        public void           setActiveSips(int v)             { this.activeSips = v; }
        public int            getHoldingsCount()               { return holdingsCount; }
        public void           setHoldingsCount(int v)          { this.holdingsCount = v; }
        public List<Response> getHoldings()                    { return holdings; }
        public void           setHoldings(List<Response> v)    { this.holdings = v; }
    }
}
