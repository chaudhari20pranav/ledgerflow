package com.ledgerflow.ledgerflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ledgerflow.ledgerflow.dto.ChangePasswordDto;
import com.ledgerflow.ledgerflow.dto.UserProfileDto;
import com.ledgerflow.ledgerflow.service.CurrencyService;
import com.ledgerflow.ledgerflow.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CurrencyService currencyService;
    
    @GetMapping
    public String viewProfile(Authentication authentication, Model model) {
        String username = authentication.getName();
        UserProfileDto profile = userService.getUserProfile(username);
        
        model.addAttribute("profile", profile);
        model.addAttribute("currencies", currencyService.getAllCurrencies());
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        
        return "profile";
    }
    
    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profile") UserProfileDto profileDto,
                                BindingResult result,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the errors in the form");
            return "redirect:/profile";
        }
        
        try {
            String username = authentication.getName();
            userService.updateUserProfile(username, profileDto);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/profile";
    }
    
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto passwordDto,
                                 BindingResult result,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the errors in the form");
            return "redirect:/profile";
        }
        
        try {
            String username = authentication.getName();
            userService.changePassword(username, passwordDto);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/profile";
    }
    
    @PostMapping("/update-currency")
    public String updateCurrency(@RequestParam String currency,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            userService.updateCurrency(username, currency);
            redirectAttributes.addFlashAttribute("success", "Currency updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/profile";
    }
    
    @PostMapping("/update-budget")
    public String updateBudget(@RequestParam Double monthlyBudget,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            userService.updateMonthlyBudget(username, monthlyBudget);
            redirectAttributes.addFlashAttribute("success", "Monthly budget updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/profile";
    }
}