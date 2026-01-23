package com.ega.bank.egabankbackend.service;

import com.ega.bank.egabankbackend.dto.DailyStatsDTO;
import com.ega.bank.egabankbackend.dto.WeeklyStatsDTO;
import com.ega.bank.egabankbackend.dto.StatsComparison;
import com.ega.bank.egabankbackend.entity.DailyStats;
import com.ega.bank.egabankbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.ega.bank.egabankbackend.repository.CompteRepository;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StatsService {
    
    private final DailyStatsRepository dailyStatsRepository;
    private final ClientRepository clientRepository;
    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * Enregistrer les statistiques quotidiennes
     * Cette méthode s'exécute automatiquement tous les jours à minuit
     */
    @Scheduled(cron = "0 0 0 * * *") // Tous les jours à minuit
    public void recordDailyStats() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        // Vérifier si les stats d'aujourd'hui existent déjà
        if (dailyStatsRepository.findByDate(today).isPresent()) {
            return; // Stats déjà enregistrées
        }
        
        // Récupérer les stats d'hier pour calculer les nouveaux
        DailyStats yesterdayStats = dailyStatsRepository.findByDate(yesterday).orElse(null);
        
        // Calculer les statistiques actuelles
        long totalClients = clientRepository.count();
        long totalAccounts = compteRepository.count();
        BigDecimal totalBalance = compteRepository.findAll()
                .stream()
                .map(compte -> compte.getSolde() != null ? compte.getSolde() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalTransactions = transactionRepository.count();
        
        // Calculer les nouveaux clients/comptes/transactions d'aujourd'hui
        int newClientsToday = yesterdayStats != null ? 
            (int)(totalClients - yesterdayStats.getTotalClients()) : (int)totalClients;
        int newAccountsToday = yesterdayStats != null ? 
            (int)(totalAccounts - yesterdayStats.getTotalAccounts()) : (int)totalAccounts;
        int transactionsToday = yesterdayStats != null ? 
            (int)(totalTransactions - yesterdayStats.getTotalTransactions()) : (int)totalTransactions;
        
        // Calculer le revenu quotidien (somme des dépôts du jour)
        BigDecimal dailyRevenue = transactionRepository.findAll()
                .stream()
                .filter(t -> t.getDateTransaction().toLocalDate().equals(today))
                .filter(t -> t.getTypeTransaction().toString().equals("DEPOT"))
                .map(t -> t.getMontant() != null ? t.getMontant() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Créer et sauvegarder les statistiques
        DailyStats stats = DailyStats.builder()
                .date(today)
                .totalClients((int)totalClients)
                .newClientsToday(Math.max(newClientsToday, 0))
                .totalAccounts((int)totalAccounts)
                .newAccountsToday(Math.max(newAccountsToday, 0))
                .totalBalance(totalBalance)
                .dailyRevenue(dailyRevenue)
                .totalTransactions((int)totalTransactions)
                .transactionsToday(Math.max(transactionsToday, 0))
                .build();
        
        dailyStatsRepository.save(stats);
    }
    
    /**
     * Récupérer les statistiques des 7 derniers jours
     */
    @Transactional(readOnly = true)
    public WeeklyStatsDTO getWeeklyStats() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);
        
        // Récupérer les stats des 7 derniers jours
        List<DailyStats> stats = dailyStatsRepository
                .findByDateBetweenOrderByDateAsc(sevenDaysAgo, today);
        
        // S'il n'y a pas de stats, enregistrer celles d'aujourd'hui
        if (stats.isEmpty()) {
            recordDailyStats();
            stats = dailyStatsRepository.findByDateBetweenOrderByDateAsc(sevenDaysAgo, today);
        }
        
        // Remplir les jours manquants avec des données vides
        List<DailyStatsDTO> weeklyData = fillMissingDays(stats, sevenDaysAgo, today);
        
        // Calculer la croissance
        StatsComparison comparison = calculateGrowth(stats);
        
        return WeeklyStatsDTO.builder()
                .weeklyData(weeklyData)
                .comparison(comparison)
                .build();
    }
    
    /**
     * Remplir les jours manquants avec des valeurs par défaut
     */
    private List<DailyStatsDTO> fillMissingDays(List<DailyStats> stats, LocalDate start, LocalDate end) {
        List<DailyStatsDTO> result = new java.util.ArrayList<>();
        
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            DailyStats stat = stats.stream()
                    .filter(s -> s.getDate().equals(currentDate))
                    .findFirst()
                    .orElse(null);
            
            if (stat != null) {
                result.add(mapToDTO(stat));
            } else {
                // Jour sans données
                result.add(DailyStatsDTO.builder()
                        .date(currentDate)
                        .totalClients(0)
                        .newClientsToday(0)
                        .totalAccounts(0)
                        .newAccountsToday(0)
                        .totalBalance(BigDecimal.ZERO)
                        .dailyRevenue(BigDecimal.ZERO)
                        .totalTransactions(0)
                        .transactionsToday(0)
                        .build());
            }
        }
        
        return result;
    }
    
    /**
     * Calculer la croissance par rapport à la semaine précédente
     */
    private StatsComparison calculateGrowth(List<DailyStats> stats) {
        if (stats.size() < 2) {
            return StatsComparison.builder()
                    .clientsGrowth(0.0)
                    .accountsGrowth(0.0)
                    .balanceGrowth(0.0)
                    .build();
        }
        
        DailyStats oldest = stats.get(0);
        DailyStats newest = stats.get(stats.size() - 1);
        
        double clientsGrowth = calculatePercentageGrowth(
                oldest.getTotalClients(), newest.getTotalClients());
        double accountsGrowth = calculatePercentageGrowth(
                oldest.getTotalAccounts(), newest.getTotalAccounts());
        double balanceGrowth = calculatePercentageGrowth(
                oldest.getTotalBalance().doubleValue(), newest.getTotalBalance().doubleValue());
        
        return StatsComparison.builder()
                .clientsGrowth(clientsGrowth)
                .accountsGrowth(accountsGrowth)
                .balanceGrowth(balanceGrowth)
                .build();
    }
    
    private double calculatePercentageGrowth(double oldValue, double newValue) {
        if (oldValue == 0) return 100.0;
        return ((newValue - oldValue) / oldValue) * 100;
    }
    
    private DailyStatsDTO mapToDTO(DailyStats stats) {
        return DailyStatsDTO.builder()
                .date(stats.getDate())
                .totalClients(stats.getTotalClients())
                .newClientsToday(stats.getNewClientsToday())
                .totalAccounts(stats.getTotalAccounts())
                .newAccountsToday(stats.getNewAccountsToday())
                .totalBalance(stats.getTotalBalance())
                .dailyRevenue(stats.getDailyRevenue())
                .totalTransactions(stats.getTotalTransactions())
                .transactionsToday(stats.getTransactionsToday())
                .build();
    }
}