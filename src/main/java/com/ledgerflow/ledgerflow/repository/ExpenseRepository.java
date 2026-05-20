package com.ledgerflow.ledgerflow.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ledgerflow.ledgerflow.model.Expense;
import com.ledgerflow.ledgerflow.model.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserOrderByExpenseDateDesc(User user);
    List<Expense> findByUserAndExpenseTypeOrderByExpenseDateDesc(User user, String expenseType);
    List<Expense> findByUserAndExpenseDateBetween(User user, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.expenseType = :expenseType")
    BigDecimal sumByUserAndExpenseType(@Param("user") User user, @Param("expenseType") String expenseType);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.expenseType = :expenseType AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserAndExpenseTypeAndDateRange(@Param("user") User user,
                                                   @Param("expenseType") String expenseType,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = :user GROUP BY e.category")
    List<Object[]> getCategoryWiseExpenses(@Param("user") User user);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.expenseDate BETWEEN :startDate AND :endDate GROUP BY e.category")
    List<Object[]> getCategoryWiseExpensesForDateRange(@Param("user") User user,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);
}