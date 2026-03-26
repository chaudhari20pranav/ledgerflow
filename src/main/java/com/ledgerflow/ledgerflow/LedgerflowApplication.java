package com.ledgerflow.ledgerflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ledgerflow.ledgerflow.service.CurrencyService;

@SpringBootApplication
public class LedgerflowApplication implements CommandLineRunner {
    
    @Autowired
    private CurrencyService currencyService;
    
    public static void main(String[] args) {
        SpringApplication.run(LedgerflowApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize currencies on startup
        currencyService.initializeCurrencies();
        System.out.println("Currencies initialized successfully!");
    }
}