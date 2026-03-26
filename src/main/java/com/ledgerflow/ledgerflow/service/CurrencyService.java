package com.ledgerflow.ledgerflow.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ledgerflow.ledgerflow.model.Currency;
import com.ledgerflow.ledgerflow.repository.CurrencyRepository;

@Service
public class CurrencyService {
    
    @Autowired
    private CurrencyRepository currencyRepository;
    
    // Predefined exchange rates (in a real app, you'd call an external API)
    private static final Map<String, BigDecimal> EXCHANGE_RATES = new HashMap<>();
    
    static {
        EXCHANGE_RATES.put("USD", BigDecimal.ONE);
        EXCHANGE_RATES.put("EUR", new BigDecimal("0.92"));
        EXCHANGE_RATES.put("GBP", new BigDecimal("0.79"));
        EXCHANGE_RATES.put("INR", new BigDecimal("83.12"));
        EXCHANGE_RATES.put("JPY", new BigDecimal("151.45"));
        EXCHANGE_RATES.put("CAD", new BigDecimal("1.35"));
        EXCHANGE_RATES.put("AUD", new BigDecimal("1.52"));
        EXCHANGE_RATES.put("CNY", new BigDecimal("7.21"));
        EXCHANGE_RATES.put("SGD", new BigDecimal("1.34"));
        EXCHANGE_RATES.put("CHF", new BigDecimal("0.89"));
    }
    
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }
    
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        BigDecimal fromRate = EXCHANGE_RATES.getOrDefault(fromCurrency, BigDecimal.ONE);
        BigDecimal toRate = EXCHANGE_RATES.getOrDefault(toCurrency, BigDecimal.ONE);
        
        // Convert to USD first, then to target currency
        BigDecimal inUSD = amount.divide(fromRate, 4, RoundingMode.HALF_UP);
        BigDecimal result = inUSD.multiply(toRate);
        
        return result.setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        
        BigDecimal fromRate = EXCHANGE_RATES.getOrDefault(fromCurrency, BigDecimal.ONE);
        BigDecimal toRate = EXCHANGE_RATES.getOrDefault(toCurrency, BigDecimal.ONE);
        
        return toRate.divide(fromRate, 4, RoundingMode.HALF_UP);
    }
    
    public String getCurrencySymbol(String currencyCode) {
        Map<String, String> symbols = new HashMap<>();
        symbols.put("USD", "$");
        symbols.put("EUR", "€");
        symbols.put("GBP", "£");
        symbols.put("INR", "₹");
        symbols.put("JPY", "¥");
        symbols.put("CAD", "C$");
        symbols.put("AUD", "A$");
        symbols.put("CNY", "¥");
        symbols.put("SGD", "S$");
        symbols.put("CHF", "CHF");
        
        return symbols.getOrDefault(currencyCode, currencyCode);
    }
    
    public void initializeCurrencies() {
        for (Map.Entry<String, BigDecimal> entry : EXCHANGE_RATES.entrySet()) {
            if (!currencyRepository.existsByCode(entry.getKey())) {
                Currency currency = new Currency();
                currency.setCode(entry.getKey());
                currency.setName(getCurrencyName(entry.getKey()));
                currency.setSymbol(getCurrencySymbol(entry.getKey()));
                currency.setExchangeRate(entry.getValue());
                currency.setBaseCurrency("USD");
                currencyRepository.save(currency);
            }
        }
    }
    
    private String getCurrencyName(String code) {
        Map<String, String> names = new HashMap<>();
        names.put("USD", "US Dollar");
        names.put("EUR", "Euro");
        names.put("GBP", "British Pound");
        names.put("INR", "Indian Rupee");
        names.put("JPY", "Japanese Yen");
        names.put("CAD", "Canadian Dollar");
        names.put("AUD", "Australian Dollar");
        names.put("CNY", "Chinese Yuan");
        names.put("SGD", "Singapore Dollar");
        names.put("CHF", "Swiss Franc");
        
        return names.getOrDefault(code, code);
    }
}