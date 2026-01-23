package com.ega.bank.egabankbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private LocalDate date;
    
    @Column(nullable = false)
    private Integer totalClients;
    
    @Column(nullable = false)
    private Integer newClientsToday;
    
    @Column(nullable = false)
    private Integer totalAccounts;
    
    @Column(nullable = false)
    private Integer newAccountsToday;
    
    @Column(nullable = false)
    private BigDecimal totalBalance;
    
    @Column(nullable = false)
    private BigDecimal dailyRevenue;
    
    @Column(nullable = false)
    private Integer totalTransactions;
    
    @Column(nullable = false)
    private Integer transactionsToday;
    
    @Column(nullable = false)
    private LocalDate createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}