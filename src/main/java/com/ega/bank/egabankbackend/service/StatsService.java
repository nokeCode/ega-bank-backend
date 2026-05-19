package com.ega.bank.egabankbackend.service;

import com.ega.bank.egabankbackend.dto.DailyStatsDTO;
import com.ega.bank.egabankbackend.dto.WeeklyStatsDTO;
import com.ega.bank.egabankbackend.dto.StatsComparison;
import com.ega.bank.egabankbackend.entity.DailyStats;
import com.ega.bank.egabankbackend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class StatsService {
    
    private DailyStatsRepository dailyStatsRepository;
    private ClientRepository clientRepository;
    private CompteRepository compteRepository;
    private TransactionRepository transactionRepository;
    
    /**
     * Enregistrer les statistiques quotidiennes automatiquement
     * Cette méthode s'exécute automatiquement tous les jours à minuit
     */
    @Scheduled(cron = "0 0 0 * * *") // Tous les jours à minuit
    public void recordDailyStatsScheduled() {
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
            recordDailyStatsScheduled();
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

    /**
     * Enregistrer les statistiques quotidiennes manuellement via DTO
     */
    @Transactional
    public DailyStatsDTO recordDailyStats(DailyStatsDTO dailyStatsDTO) {
        // Vérifier si des stats existent déjà pour cette date
        java.util.Optional<DailyStats> existingStats = dailyStatsRepository.findByDate(dailyStatsDTO.getDate());
        
        DailyStats dailyStats;
        if (existingStats.isPresent()) {
            // Mettre à jour les stats existantes
            dailyStats = existingStats.get();
            dailyStats.setTotalClients(dailyStatsDTO.getTotalClients());
            dailyStats.setNewClientsToday(dailyStatsDTO.getNewClientsToday());
            dailyStats.setTotalAccounts(dailyStatsDTO.getTotalAccounts());
            dailyStats.setNewAccountsToday(dailyStatsDTO.getNewAccountsToday());
            dailyStats.setTotalBalance(dailyStatsDTO.getTotalBalance());
            dailyStats.setDailyRevenue(dailyStatsDTO.getDailyRevenue());
            dailyStats.setTotalTransactions(dailyStatsDTO.getTotalTransactions());
            dailyStats.setTransactionsToday(dailyStatsDTO.getTransactionsToday());
        } else {
            // Créer une nouvelle entité DailyStats à partir du DTO
            dailyStats = DailyStats.builder()
                    .date(dailyStatsDTO.getDate())
                    .totalClients(dailyStatsDTO.getTotalClients())
                    .newClientsToday(dailyStatsDTO.getNewClientsToday())
                    .totalAccounts(dailyStatsDTO.getTotalAccounts())
                    .newAccountsToday(dailyStatsDTO.getNewAccountsToday())
                    .totalBalance(dailyStatsDTO.getTotalBalance())
                    .dailyRevenue(dailyStatsDTO.getDailyRevenue())
                    .totalTransactions(dailyStatsDTO.getTotalTransactions())
                    .transactionsToday(dailyStatsDTO.getTransactionsToday())
                    .build();
        }
        
        // Sauvegarder l'entité
        DailyStats savedStats = dailyStatsRepository.save(dailyStats);
        
        // Retourner le DTO mappé
        return mapToDTO(savedStats);
    }

    /**
     * Récupérer les statistiques de toutes les semaines
     */
    @Transactional(readOnly = true)
    public List<WeeklyStatsDTO> getAllWeeklyStats() {
        List<DailyStats> allStats = dailyStatsRepository.findAll();
        
        if (allStats.isEmpty()) {
            return List.of();
        }
        
        // Grouper par semaine et créer les WeeklyStatsDTO
        List<WeeklyStatsDTO> weeklyStats = new java.util.ArrayList<>();
        // Pour maintenant, retourner les 4 dernières semaines
        LocalDate today = LocalDate.now();
        for (int week = 0; week < 4; week++) {
            LocalDate endOfWeek = today.minusWeeks(week);
            LocalDate startOfWeek = endOfWeek.minusDays(6);
            
            List<DailyStats> weekStats = allStats.stream()
                    .filter(s -> !s.getDate().isBefore(startOfWeek) && !s.getDate().isAfter(endOfWeek))
                    .collect(java.util.stream.Collectors.toList());
            
            if (!weekStats.isEmpty()) {
                List<DailyStatsDTO> weeklyData = weekStats.stream()
                        .map(this::mapToDTO)
                        .collect(java.util.stream.Collectors.toList());
                
                StatsComparison comparison = calculateGrowth(weekStats);
                
                weeklyStats.add(WeeklyStatsDTO.builder()
                        .weeklyData(weeklyData)
                        .comparison(comparison)
                        .build());
            }
        }
        
        return weeklyStats;
    }

    /**
     * Récupérer les données d'une semaine spécifique
     */
    @Transactional(readOnly = true)
    public List<DailyStatsDTO> getWeekData(int weekNumber, int year) {
        List<DailyStats> stats = dailyStatsRepository.findAll();
        
        return stats.stream()
                .filter(s -> s.getDate().getYear() == year)
                .filter(s -> s.getDate().get(WeekFields.ISO.weekOfWeekBasedYear()) == weekNumber)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Charger les données fictives pour une semaine (7 jours)
     */
    @Transactional
    public WeeklyStatsDTO loadMockWeekData() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);
        
        // Supprimer les données existantes pour cette semaine
        for (LocalDate date = sevenDaysAgo; !date.isAfter(today); date = date.plusDays(1)) {
            dailyStatsRepository.findByDate(date).ifPresent(dailyStatsRepository::delete);
        }
        
        // Créer les données fictives
        List<DailyStatsDTO> mockData = new java.util.ArrayList<>();
        for (LocalDate date = sevenDaysAgo; !date.isAfter(today); date = date.plusDays(1)) {
            DailyStatsDTO dto = DailyStatsDTO.builder()
                    .date(date)
                    .totalClients(10 + (int)(Math.random() * 20))
                    .newClientsToday((int)(Math.random() * 5))
                    .totalAccounts(15 + (int)(Math.random() * 25))
                    .newAccountsToday((int)(Math.random() * 3))
                    .totalBalance(new BigDecimal(50000 + (int)(Math.random() * 50000)))
                    .dailyRevenue(new BigDecimal(5000 + (int)(Math.random() * 10000)))
                    .totalTransactions(100 + (int)(Math.random() * 100))
                    .transactionsToday((int)(Math.random() * 30))
                    .build();
            
            // Sauvegarder dans la base de données
            DailyStats dailyStats = DailyStats.builder()
                    .date(date)
                    .totalClients(dto.getTotalClients())
                    .newClientsToday(dto.getNewClientsToday())
                    .totalAccounts(dto.getTotalAccounts())
                    .newAccountsToday(dto.getNewAccountsToday())
                    .totalBalance(dto.getTotalBalance())
                    .dailyRevenue(dto.getDailyRevenue())
                    .totalTransactions(dto.getTotalTransactions())
                    .transactionsToday(dto.getTransactionsToday())
                    .build();
            
            dailyStatsRepository.save(dailyStats);
            mockData.add(dto);
        }
        
        StatsComparison comparison = StatsComparison.builder()
                .clientsGrowth(calculatePercentageGrowth(mockData.get(0).getTotalClients(), 
                        mockData.get(mockData.size() - 1).getTotalClients()))
                .accountsGrowth(calculatePercentageGrowth(mockData.get(0).getTotalAccounts(), 
                        mockData.get(mockData.size() - 1).getTotalAccounts()))
                .balanceGrowth(calculatePercentageGrowth(mockData.get(0).getTotalBalance().doubleValue(), 
                        mockData.get(mockData.size() - 1).getTotalBalance().doubleValue()))
                .build();
        
        return WeeklyStatsDTO.builder()
                .weeklyData(mockData)
                .comparison(comparison)
                .build();
    }
}