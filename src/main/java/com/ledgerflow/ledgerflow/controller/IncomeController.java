package com.ledgerflow.ledgerflow.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ledgerflow.ledgerflow.model.Income;
import com.ledgerflow.ledgerflow.model.User;
import com.ledgerflow.ledgerflow.service.IncomeService;
import com.ledgerflow.ledgerflow.service.UserService;

@Controller
@RequestMapping("/income")
public class IncomeController {
    
    @Autowired
    private IncomeService incomeService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/add")
    public String showAddIncomeForm(Model model) {
        model.addAttribute("income", new Income());
        return "add-income";
    }
    
    @PostMapping("/add")
    public String addIncome(@ModelAttribute Income income,
                            Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        income.setIncomeDate(LocalDate.now());
        incomeService.addIncome(income, user);
        return "redirect:/dashboard";
    }
    
    @GetMapping("/view")
    public String viewIncomes(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        List<Income> incomes = incomeService.getUserIncomes(user);
        model.addAttribute("incomes", incomes);
        return "view-incomes";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteIncome(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        incomeService.deleteIncome(id, user);
        return "redirect:/income/view";
    }
}