package com.ledgerflow.ledgerflow.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledgerflow.ledgerflow.model.Expense;
import com.ledgerflow.ledgerflow.model.User;
import com.ledgerflow.ledgerflow.repository.ExpenseRepository;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;

    // Add to ExpenseService.java
    @Autowired
    private CurrencyService currencyService;


    public BigDecimal getTotalExpensesByTypeInCurrency(User user, String expenseType, String targetCurrency) {
        BigDecimal total = getTotalExpensesByType(user, expenseType);
        String userCurrency = user.getPreferredCurrency();
        if (!userCurrency.equals(targetCurrency)) {
            total = currencyService.convert(total, userCurrency, targetCurrency);
        }
        return total;
    }
    
    @Transactional
    public Expense addExpense(Expense expense, User user) {
        expense.setUser(user);
        return expenseRepository.save(expense);
    }
    
    public List<Expense> getUserExpenses(User user) {
        return expenseRepository.findByUserOrderByExpenseDateDesc(user);
    }
    
    public List<Expense> getUserExpensesByType(User user, String expenseType) {
        return expenseRepository.findByUserAndExpenseTypeOrderByExpenseDateDesc(user, expenseType);
    }
    
    public BigDecimal getTotalExpensesByType(User user, String expenseType) {
        BigDecimal total = expenseRepository.sumByUserAndExpenseType(user, expenseType);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public List<Object[]> getCategoryWiseExpenses(User user) {
        return expenseRepository.getCategoryWiseExpenses(user);
    }
    
    @Transactional
    public void deleteExpense(Long expenseId, User user) {
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        
        expenseRepository.delete(expense);
    }
    
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
    }
}