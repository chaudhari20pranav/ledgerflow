package com.ledgerflow.ledgerflow.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ledgerflow.ledgerflow.dto.FinancialMetricsDto;
import com.ledgerflow.ledgerflow.model.User;

@Service
public class FinancialService {
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private IncomeService incomeService;
    
    @Autowired
    private CurrencyService currencyService;
    
    public FinancialMetricsDto calculateMetrics(User user, LocalDate startDate, LocalDate endDate) {
        FinancialMetricsDto metrics = new FinancialMetricsDto();
        
        BigDecimal fixedExpenses = expenseService.getTotalExpensesByType(user, "FIXED");
        BigDecimal variableExpenses = expenseService.getTotalExpensesByType(user, "VARIABLE");
        BigDecimal totalIncome = incomeService.getIncomeForDateRange(user, startDate, endDate);
        
        metrics.setTotalFixedExpenses(fixedExpenses != null ? fixedExpenses : BigDecimal.ZERO);
        metrics.setTotalVariableExpenses(variableExpenses != null ? variableExpenses : BigDecimal.ZERO);
        metrics.setTotalExpenses(metrics.getTotalFixedExpenses().add(metrics.getTotalVariableExpenses()));
        metrics.setTotalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO);
        
        BigDecimal netProfit = metrics.getTotalIncome().subtract(metrics.getTotalExpenses());
        metrics.setNetProfit(netProfit);
        metrics.setSavings(netProfit);
        
        if (metrics.getTotalIncome().compareTo(BigDecimal.ZERO) > 0) {
            metrics.setSavingsRate(netProfit.doubleValue() / metrics.getTotalIncome().doubleValue() * 100);
        }
        
        List<Object[]> categoryExpensesList = expenseService.getCategoryWiseExpenses(user);
        Map<String, BigDecimal> categoryExpenseMap = new HashMap<>();
        if (categoryExpensesList != null) {
            for (Object[] entry : categoryExpensesList) {
                if (entry[0] != null && entry[1] != null) {
                    categoryExpenseMap.put((String) entry[0], (BigDecimal) entry[1]);
                }
            }
        }
        metrics.setCategoryWiseExpenses(categoryExpenseMap);
        
        List<Object[]> categoryIncomesList = incomeService.getCategoryWiseIncomes(user);
        Map<String, BigDecimal> categoryIncomeMap = new HashMap<>();
        if (categoryIncomesList != null) {
            for (Object[] entry : categoryIncomesList) {
                if (entry[0] != null && entry[1] != null) {
                    categoryIncomeMap.put((String) entry[0], (BigDecimal) entry[1]);
                }
            }
        }
        metrics.setCategoryWiseIncome(categoryIncomeMap);
        
        if (user.getMonthlyBudget() != null && user.getMonthlyBudget() > 0) {
            BigDecimal monthlyBudget = BigDecimal.valueOf(user.getMonthlyBudget());
            metrics.setMonthlyBudget(monthlyBudget);
            metrics.setBudgetVariance(monthlyBudget.subtract(metrics.getTotalExpenses()));
        }
        
        String preferredCurrency = user.getPreferredCurrency();
        if (preferredCurrency != null && !preferredCurrency.equals("USD")) {
            metrics.setCurrency(preferredCurrency);
            BigDecimal conversionRate = currencyService.getExchangeRate("USD", preferredCurrency);
            metrics.setConversionRate(conversionRate);
            
            metrics.setTotalFixedExpenses(convertAmount(metrics.getTotalFixedExpenses(), conversionRate));
            metrics.setTotalVariableExpenses(convertAmount(metrics.getTotalVariableExpenses(), conversionRate));
            metrics.setTotalExpenses(convertAmount(metrics.getTotalExpenses(), conversionRate));
            metrics.setTotalIncome(convertAmount(metrics.getTotalIncome(), conversionRate));
            metrics.setNetProfit(convertAmount(metrics.getNetProfit(), conversionRate));
            metrics.setSavings(convertAmount(metrics.getSavings(), conversionRate));
            if (metrics.getMonthlyBudget() != null) {
                metrics.setMonthlyBudget(convertAmount(metrics.getMonthlyBudget(), conversionRate));
                metrics.setBudgetVariance(convertAmount(metrics.getBudgetVariance(), conversionRate));
            }
            
            Map<String, BigDecimal> convertedExpenses = new HashMap<>();
            for (Map.Entry<String, BigDecimal> entry : categoryExpenseMap.entrySet()) {
                convertedExpenses.put(entry.getKey(), convertAmount(entry.getValue(), conversionRate));
            }
            metrics.setCategoryWiseExpenses(convertedExpenses);
            
            Map<String, BigDecimal> convertedIncomes = new HashMap<>();
            for (Map.Entry<String, BigDecimal> entry : categoryIncomeMap.entrySet()) {
                convertedIncomes.put(entry.getKey(), convertAmount(entry.getValue(), conversionRate));
            }
            metrics.setCategoryWiseIncome(convertedIncomes);
        } else {
            metrics.setCurrency("USD");
        }
        
        return metrics;
    }
    
    private BigDecimal convertAmount(BigDecimal amount, BigDecimal conversionRate) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(conversionRate).setScale(2, RoundingMode.HALF_UP);
    }
}