package com.ledgerflow.ledgerflow.repository;

import com.ledgerflow.ledgerflow.model.Income;
import com.ledgerflow.ledgerflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserOrderByIncomeDateDesc(User user);
    List<Income> findByUserAndIncomeDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user = :user")
    BigDecimal sumByUser(@Param("user") User user);
    
    @Query("SELECT i.category, SUM(i.amount) FROM Income i WHERE i.user = :user GROUP BY i.category")
    List<Object[]> getCategoryWiseIncomes(@Param("user") User user);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user = :user AND i.incomeDate BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}