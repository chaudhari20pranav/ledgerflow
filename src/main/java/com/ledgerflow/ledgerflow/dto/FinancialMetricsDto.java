package com.ledgerflow.ledgerflow.dto;

import java.math.BigDecimal;
import java.util.Map;

public class FinancialMetricsDto {
    private BigDecimal totalFixedExpenses;
    private BigDecimal totalVariableExpenses;
    private BigDecimal totalExpenses;
    private BigDecimal totalIncome;
    private BigDecimal grossProfit;
    private BigDecimal netProfit;
    private Double operatingLeverage;
    private Double financialLeverage;
    private BigDecimal savings;
    private Double savingsRate;
    private BigDecimal monthlyBudget;
    private BigDecimal budgetVariance;
    private Map<String, BigDecimal> categoryWiseExpenses;
    private Map<String, BigDecimal> categoryWiseIncome;
    private String currency;
    private BigDecimal conversionRate;
    
    // Constructors
    public FinancialMetricsDto() {
        this.totalFixedExpenses = BigDecimal.ZERO;
        this.totalVariableExpenses = BigDecimal.ZERO;
        this.totalExpenses = BigDecimal.ZERO;
        this.totalIncome = BigDecimal.ZERO;
        this.grossProfit = BigDecimal.ZERO;
        this.netProfit = BigDecimal.ZERO;
        this.operatingLeverage = 0.0;
        this.financialLeverage = 0.0;
        this.savings = BigDecimal.ZERO;
        this.savingsRate = 0.0;
        this.monthlyBudget = BigDecimal.ZERO;
        this.budgetVariance = BigDecimal.ZERO;
        this.conversionRate = BigDecimal.ONE;
        this.currency = "USD";
    }
    
    // Getters and Setters
    public BigDecimal getTotalFixedExpenses() { return totalFixedExpenses; }
    public void setTotalFixedExpenses(BigDecimal totalFixedExpenses) { this.totalFixedExpenses = totalFixedExpenses; }
    
    public BigDecimal getTotalVariableExpenses() { return totalVariableExpenses; }
    public void setTotalVariableExpenses(BigDecimal totalVariableExpenses) { this.totalVariableExpenses = totalVariableExpenses; }
    
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }
    
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    
    public BigDecimal getGrossProfit() { return grossProfit; }
    public void setGrossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; }
    
    public BigDecimal getNetProfit() { return netProfit; }
    public void setNetProfit(BigDecimal netProfit) { this.netProfit = netProfit; }
    
    public Double getOperatingLeverage() { return operatingLeverage; }
    public void setOperatingLeverage(Double operatingLeverage) { this.operatingLeverage = operatingLeverage; }
    
    public Double getFinancialLeverage() { return financialLeverage; }
    public void setFinancialLeverage(Double financialLeverage) { this.financialLeverage = financialLeverage; }
    
    public BigDecimal getSavings() { return savings; }
    public void setSavings(BigDecimal savings) { this.savings = savings; }
    
    public Double getSavingsRate() { return savingsRate; }
    public void setSavingsRate(Double savingsRate) { this.savingsRate = savingsRate; }
    
    public BigDecimal getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(BigDecimal monthlyBudget) { this.monthlyBudget = monthlyBudget; }
    
    public BigDecimal getBudgetVariance() { return budgetVariance; }
    public void setBudgetVariance(BigDecimal budgetVariance) { this.budgetVariance = budgetVariance; }
    
    public Map<String, BigDecimal> getCategoryWiseExpenses() { return categoryWiseExpenses; }
    public void setCategoryWiseExpenses(Map<String, BigDecimal> categoryWiseExpenses) { this.categoryWiseExpenses = categoryWiseExpenses; }
    
    public Map<String, BigDecimal> getCategoryWiseIncome() { return categoryWiseIncome; }
    public void setCategoryWiseIncome(Map<String, BigDecimal> categoryWiseIncome) { this.categoryWiseIncome = categoryWiseIncome; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public BigDecimal getConversionRate() { return conversionRate; }
    public void setConversionRate(BigDecimal conversionRate) { this.conversionRate = conversionRate; }
}