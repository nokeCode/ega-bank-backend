package com.ega.bank.egabankbackend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsComparison {
    private Double clientsGrowth;
    private Double accountsGrowth;
    private Double balanceGrowth;
}