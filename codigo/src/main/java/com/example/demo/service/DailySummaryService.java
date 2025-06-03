package com.example.demo.service;

import com.example.demo.model.DailySummary;
import com.example.demo.model.DailySummaryFood;
import com.example.demo.model.Food;
import com.example.demo.model.User;
import com.example.demo.repository.DailySummaryRepository;
import com.example.demo.repository.DailySummaryFoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DailySummaryService {

    @Autowired
    private DailySummaryRepository dailySummaryRepository;

    @Autowired
    private DailySummaryFoodRepository dailySummaryFoodRepository;

    @Autowired
    private CalorieCalculatorService calorieCalculatorService;

    public DailySummary getOrCreateDailySummary(User user, LocalDate date) {
        return dailySummaryRepository.findByUserAndFecha(user, date)
                .orElseGet(() -> {
                    DailySummary summary = new DailySummary();
                    summary.setUser(user);
                    summary.setFecha(date);
                    summary.setCaloriasObjetivo(user.getCaloriasMetaDiaria());
                    summary.setCaloriasTotales(0.0);
                    return dailySummaryRepository.save(summary);
                });
    }

    @Transactional
    public void updateCalories(DailySummary summary, Double calorias) {
        summary.setCaloriasTotales(summary.getCaloriasTotales() + calorias);
        dailySummaryRepository.save(summary);
    }

    @Transactional
    public void addFoodToSummary(DailySummary summary, Food food, Double cantidadG, String comidaTipo) {
        // Calcular calorías basadas en la cantidad
        Double calorias = (food.getCalorias100g() * cantidadG) / 100.0;

        // Crear nuevo registro de alimento
        DailySummaryFood summaryFood = new DailySummaryFood();
        summaryFood.setDailySummary(summary);
        summaryFood.setFood(food);
        summaryFood.setCantidadG(cantidadG);
        summaryFood.setCalorias(calorias);
        summaryFood.setComidaTipo(comidaTipo);

        // Añadir al resumen y actualizar calorías totales
        summary.addAlimento(summaryFood);
        updateCalories(summary, calorias);
        
        dailySummaryRepository.save(summary);
    }

    @Transactional
    public void removeFoodFromSummary(DailySummary summary, Long foodEntryId) {
        // Buscar el alimento en el resumen
        DailySummaryFood entry = summary.getAlimentos().stream()
                .filter(a -> a.getId().equals(foodEntryId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Alimento no encontrado en el resumen"));

        // Restar las calorías del alimento eliminado
        summary.setCaloriasTotales(summary.getCaloriasTotales() - entry.getCalorias());
        
        // Eliminar el alimento de la colección
        summary.removeAlimento(entry);
        
        // Guardar el resumen
        dailySummaryRepository.save(summary);
        
        // Eliminar el alimento de la base de datos
        dailySummaryFoodRepository.deleteById(foodEntryId);
        
        // Forzar la persistencia
        dailySummaryFoodRepository.flush();
    }

    public List<DailySummary> getDailySummariesForUser(User user) {
        return dailySummaryRepository.findByUserOrderByFechaDesc(user);
    }

    public List<DailySummary> getDailySummariesForUserInDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return dailySummaryRepository.findByUserAndFechaBetweenOrderByFechaDesc(user, startDate, endDate);
    }

    public Optional<DailySummary> getDailySummaryById(Long id) {
        return dailySummaryRepository.findById(id);
    }
} 