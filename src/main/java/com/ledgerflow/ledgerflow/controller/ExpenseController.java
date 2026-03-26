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
import org.springframework.web.bind.annotation.RequestParam;

import com.ledgerflow.ledgerflow.model.Expense;
import com.ledgerflow.ledgerflow.model.User;
import com.ledgerflow.ledgerflow.service.ExpenseService;
import com.ledgerflow.ledgerflow.service.UserService;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/add")
    public String showAddExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "add-expense";
    }
    
    @PostMapping("/add")
    public String addExpense(@ModelAttribute Expense expense,
                            @RequestParam String expenseType,
                            Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        expense.setExpenseType(expenseType);
        expense.setExpenseDate(LocalDate.now());
        expenseService.addExpense(expense, user);
        return "redirect:/dashboard";
    }
    
    @GetMapping("/view")
    public String viewExpenses(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        List<Expense> expenses = expenseService.getUserExpenses(user);
        model.addAttribute("expenses", expenses);
        return "view-expenses";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        expenseService.deleteExpense(id, user);
        return "redirect:/expenses/view";
    }
}