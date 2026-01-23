package com.ega.bank.egabankbackend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyStatsDTO {
    private List<DailyStatsDTO> weeklyData;
    private StatsComparison comparison;
}