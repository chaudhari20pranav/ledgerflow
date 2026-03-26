package com.ledgerflow.ledgerflow.service;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledgerflow.ledgerflow.dto.ChangePasswordDto;
import com.ledgerflow.ledgerflow.dto.UserProfileDto;
import com.ledgerflow.ledgerflow.dto.UserRegistrationDto;
import com.ledgerflow.ledgerflow.model.User;
import com.ledgerflow.ledgerflow.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles("USER")
            .build();
    }
    
    @Transactional
    public User registerNewUser(UserRegistrationDto registrationDto) {
        // Check if username exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Check if passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        User user = new User(
            registrationDto.getUsername(),
            registrationDto.getEmail(),
            passwordEncoder.encode(registrationDto.getPassword()),
            registrationDto.getFullName()
        );
        
        if (registrationDto.getPhoneNumber() != null && !registrationDto.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(registrationDto.getPhoneNumber());
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUserProfile(String username, UserProfileDto profileDto) {
        User user = findByUsername(username);
        
        // Check if new username is taken by another user
        if (!user.getUsername().equals(profileDto.getUsername()) && 
            userRepository.existsByUsername(profileDto.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        
        // Check if new email is taken by another user
        if (!user.getEmail().equals(profileDto.getEmail()) && 
            userRepository.existsByEmail(profileDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        user.setUsername(profileDto.getUsername());
        user.setEmail(profileDto.getEmail());
        user.setFullName(profileDto.getFullName());
        user.setPhoneNumber(profileDto.getPhoneNumber());
        
        if (profileDto.getPreferredCurrency() != null) {
            user.setPreferredCurrency(profileDto.getPreferredCurrency());
        }
        
        if (profileDto.getMonthlyBudget() != null) {
            user.setMonthlyBudget(profileDto.getMonthlyBudget());
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void changePassword(String username, ChangePasswordDto passwordDto) {
        User user = findByUsername(username);
        
        // Verify current password
        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Check if new passwords match
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(user);
    }
    
    @Transactional
    public void updateCurrency(String username, String currencyCode) {
        User user = findByUsername(username);
        user.setPreferredCurrency(currencyCode);
        userRepository.save(user);
    }
    
    @Transactional
    public void updateMonthlyBudget(String username, Double monthlyBudget) {
        User user = findByUsername(username);
        user.setMonthlyBudget(monthlyBudget);
        userRepository.save(user);
    }
    
    @Transactional
    public void updateLastLogin(String username) {
        User user = findByUsername(username);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
    public UserProfileDto getUserProfile(String username) {
        User user = findByUsername(username);
        UserProfileDto profileDto = new UserProfileDto();
        profileDto.setUsername(user.getUsername());
        profileDto.setEmail(user.getEmail());
        profileDto.setFullName(user.getFullName());
        profileDto.setPhoneNumber(user.getPhoneNumber());
        profileDto.setPreferredCurrency(user.getPreferredCurrency());
        profileDto.setMonthlyBudget(user.getMonthlyBudget());
        return profileDto;
    }

    
}