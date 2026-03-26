package com.ledgerflow.ledgerflow.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ledgerflow.ledgerflow.dto.FinancialMetricsDto;
import com.ledgerflow.ledgerflow.model.User;
import com.ledgerflow.ledgerflow.service.CurrencyService;
import com.ledgerflow.ledgerflow.service.ExpenseService;
import com.ledgerflow.ledgerflow.service.FinancialService;
import com.ledgerflow.ledgerflow.service.IncomeService;
import com.ledgerflow.ledgerflow.service.UserService;

@Controller
public class DashboardController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private IncomeService incomeService;
    
    @Autowired
    private FinancialService financialService;
    
    @Autowired
    private CurrencyService currencyService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, 
                           @RequestParam(required = false) Integer month,
                           @RequestParam(required = false) Integer year,
                           Model model) {
        User user = userService.findByUsername(authentication.getName());
        
        LocalDate startDate;
        LocalDate endDate;
        int selectedMonth;
        int selectedYear;
        
        if (month != null && year != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();
            selectedMonth = month;
            selectedYear = year;
        } else {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
            selectedMonth = currentMonth.getMonthValue();
            selectedYear = currentMonth.getYear();
        }
        
        FinancialMetricsDto metrics = financialService.calculateMetrics(user, startDate, endDate);
        
        model.addAttribute("username", user.getFullName());
        model.addAttribute("metrics", metrics);
        model.addAttribute("categoryExpenses", metrics.getCategoryWiseExpenses() != null ? metrics.getCategoryWiseExpenses() : new HashMap<>());
        model.addAttribute("categoryIncomes", metrics.getCategoryWiseIncome() != null ? metrics.getCategoryWiseIncome() : new HashMap<>());
        model.addAttribute("recentExpenses", expenseService.getUserExpenses(user).stream().limit(5).collect(Collectors.toList()));
        model.addAttribute("recentIncomes", incomeService.getUserIncomes(user).stream().limit(5).collect(Collectors.toList()));
        model.addAttribute("currencySymbol", currencyService.getCurrencySymbol(metrics.getCurrency()));
        model.addAttribute("startDate", startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        model.addAttribute("endDate", endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("selectedYear", selectedYear);
        
        return "dashboard";
    }
}