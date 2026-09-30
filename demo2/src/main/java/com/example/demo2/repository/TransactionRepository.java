package com.example.demo2.repository;

import com.example.demo2.entity.Transaction;
import com.example.demo2.entity.Transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Lấy giao dịch xuất kho trong ngày
    @Query("SELECT t FROM Transaction t WHERE t.type = :type AND DATE(t.transactionDate) = DATE(:date)")
    List<Transaction> findByTypeAndDate(@Param("type") TransactionType type, @Param("date") LocalDateTime date);

    // Thống kê xuất kho theo supply
    @Query("SELECT t.supply.id, t.supply.name, SUM(t.amount) as total FROM Transaction t " +
            "WHERE t.type = :type GROUP BY t.supply.id ORDER BY total DESC")
    List<Object[]> getTopExportSupplies(@Param("type") TransactionType type);
}
