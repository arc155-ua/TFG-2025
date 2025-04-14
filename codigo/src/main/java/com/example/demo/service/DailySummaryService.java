package com.example.demo.service;

import com.example.demo.model.DailySummary;
import com.example.demo.model.User;
import com.example.demo.repository.DailySummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DailySummaryService {

    @Autowired
    private DailySummaryRepository dailySummaryRepository;

    @Transactional
    public DailySummary getOrCreateDailySummary(User user, LocalDate date) {
        Optional<DailySummary> existingSummary = dailySummaryRepository.findByUserAndFecha(user, date);
        
        if (existingSummary.isPresent()) {
            return existingSummary.get();
        }

        DailySummary summary = new DailySummary();
        summary.setUser(user);
        summary.setFecha(date);
        summary.setCaloriasTotales(0.0);
        summary.setCaloriasMeta(user.getCaloriasMetaDiaria());

        return dailySummaryRepository.save(summary);
    }

    @Transactional
    public void updateCalories(DailySummary summary, double calories) {
        summary.setCaloriasTotales(calories);
        dailySummaryRepository.save(summary);
    }
} 