package com.ega.bank.egabankbackend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStatsDTO {
    private LocalDate date;
    private Integer totalClients;
    private Integer newClientsToday;
    private Integer totalAccounts;
    private Integer newAccountsToday;
    private BigDecimal totalBalance;
    private BigDecimal dailyRevenue;
    private Integer totalTransactions;
    private Integer transactionsToday;
}


