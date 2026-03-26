package com.ledgerflow.ledgerflow.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledgerflow.ledgerflow.model.Income;
import com.ledgerflow.ledgerflow.model.User;
import com.ledgerflow.ledgerflow.repository.IncomeRepository;

@Service
public class IncomeService {
    
    @Autowired
    private IncomeRepository incomeRepository;
    
    @Transactional
    public Income addIncome(Income income, User user) {
        income.setUser(user);
        return incomeRepository.save(income);
    }
    
    public List<Income> getUserIncomes(User user) {
        return incomeRepository.findByUserOrderByIncomeDateDesc(user);
    }
    
    public BigDecimal getTotalIncome(User user) {
        BigDecimal total = incomeRepository.sumByUser(user);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getIncomeForDateRange(User user, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = incomeRepository.sumByUserAndDateRange(user, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public List<Object[]> getCategoryWiseIncomes(User user) {
        return incomeRepository.getCategoryWiseIncomes(user);
    }
    
    @Transactional
    public void deleteIncome(Long incomeId, User user) {
        Income income = incomeRepository.findById(incomeId)
            .orElseThrow(() -> new RuntimeException("Income not found"));
        
        if (!income.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this income");
        }
        
        incomeRepository.delete(income);
    }
    
    public Income getIncomeById(Long id) {
        return incomeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Income not found"));
    }
}