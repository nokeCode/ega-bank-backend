package com.ega.bank.egabankbackend.controller;

import com.ega.bank.egabankbackend.dto.DailyStatsDTO;
import com.ega.bank.egabankbackend.dto.WeeklyStatsDTO;
import com.ega.bank.egabankbackend.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "http://localhost:4200")
public class StatsController {

    @Autowired
    private StatsService statsService;

    /**
     * Récupérer les statistiques par défaut (semaine actuelle)
     */
    @GetMapping
    public ResponseEntity<?> getStats() {
        try {
            WeeklyStatsDTO weeklyStats = statsService.getWeeklyStats();
            return ResponseEntity.ok(weeklyStats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erreur lors de la récupération des statistiques: " + e.getMessage()
            ));
        }
    }

    /**
     * Récupérer les statistiques de la semaine actuelle
     */
    @GetMapping("/weekly")
    public ResponseEntity<?> getWeeklyStats() {
        try {
            WeeklyStatsDTO weeklyStats = statsService.getWeeklyStats();
            return ResponseEntity.ok(weeklyStats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erreur lors de la récupération des statistiques: " + e.getMessage()
            ));
        }
    }

    /**
     * Récupérer les statistiques de toutes les semaines
     */
    @GetMapping("/all-weeks")
    public ResponseEntity<?> getAllWeeklyStats() {
        try {
            List<WeeklyStatsDTO> allStats = statsService.getAllWeeklyStats();
            return ResponseEntity.ok(allStats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erreur lors de la récupération de toutes les statistiques: " + e.getMessage()
            ));
        }
    }

    /**
     * Enregistrer les statistiques du jour
     */
    @PostMapping("/record")
    public ResponseEntity<?> recordDailyStats(@RequestBody DailyStatsDTO dailyStatsDTO) {
        try {
            DailyStatsDTO recorded = statsService.recordDailyStats(dailyStatsDTO);
            return ResponseEntity.ok(recorded);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erreur lors de l'enregistrement des statistiques: " + e.getMessage()
            ));
        }
    }

    /**
     * Vérifier et réinitialiser si c'est une nouvelle semaine
     */
    @GetMapping("/check-week")
    public ResponseEntity<?> checkAndResetWeek() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate sevenDaysAgo = today.minusDays(6);

            // Récupérer les stats de cette semaine via le service
            WeeklyStatsDTO thisWeekData = statsService.getWeeklyStats();

            // Nouvelle semaine si pas de données
            boolean hasData = thisWeekData != null && !thisWeekData.getWeeklyData().isEmpty();

            return ResponseEntity.ok().body(Map.of(
                "hasData", hasData,
                "currentDate", today
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erreur vérification semaine: " + e.getMessage()
            ));
        }
    }

    /**
     * Charger les données fictives pour une semaine (7 jours)
     */
    @PostMapping("/load-mock-data")
    public ResponseEntity<?> loadMockWeekData() {
        try {
            WeeklyStatsDTO mockData = statsService.loadMockWeekData();
            return ResponseEntity.ok(mockData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erreur lors du chargement des données fictives: " + e.getMessage()
            ));
        }
    }
}