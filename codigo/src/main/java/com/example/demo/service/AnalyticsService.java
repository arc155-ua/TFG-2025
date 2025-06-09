package com.example.demo.service;

import com.example.demo.model.DailySummary;
import com.example.demo.model.User;
import com.example.demo.repository.DailySummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private DailySummaryRepository dailySummaryRepository;

    public Map<LocalDate, Double> getCaloriasPorDia(User user, LocalDate startDate, LocalDate endDate) {
        List<DailySummary> summaries = dailySummaryRepository.findByUserAndFechaBetweenOrderByFechaAsc(
            user, startDate, endDate);

        return summaries.stream()
            .collect(Collectors.toMap(
                DailySummary::getFecha,
                DailySummary::getCaloriasTotales,
                (v1, v2) -> v1,
                TreeMap::new
            ));
    }

    public List<String> getAlertas(User user, LocalDate startDate, LocalDate endDate) {
        List<DailySummary> summaries = dailySummaryRepository.findByUserAndFechaBetweenOrderByFechaDesc(
            user, startDate, endDate);

        List<String> alertas = new ArrayList<>();
        double caloriasObjetivo = user.getCaloriasMetaDiaria();
        double umbralSuperior = caloriasObjetivo * 1.2; // 20% por encima
        double umbralInferior = caloriasObjetivo * 0.8; // 20% por debajo

        // Analizar los últimos 7 días
        List<DailySummary> ultimos7Dias = summaries.stream()
            .limit(7)
            .collect(Collectors.toList());

        if (!ultimos7Dias.isEmpty()) {
            // Contar días por encima y por debajo del objetivo
            long diasPorEncima = ultimos7Dias.stream()
                .filter(s -> s.getCaloriasTotales() > umbralSuperior)
                .count();

            long diasPorDebajo = ultimos7Dias.stream()
                .filter(s -> s.getCaloriasTotales() < umbralInferior)
                .count();

            // Calcular promedio de calorías
            double promedioCalorias = ultimos7Dias.stream()
                .mapToDouble(DailySummary::getCaloriasTotales)
                .average()
                .orElse(0.0);

            // Generar alertas
            if (diasPorEncima >= 3) {
                alertas.add("Has consumido más calorías de lo recomendado en " + diasPorEncima + " de los últimos 7 días");
            }
            if (diasPorDebajo >= 3) {
                alertas.add("Has consumido menos calorías de lo recomendado en " + diasPorDebajo + " de los últimos 7 días");
            }
            if (promedioCalorias > umbralSuperior) {
                alertas.add("Tu consumo promedio de calorías está por encima de lo recomendado");
            }
            if (promedioCalorias < umbralInferior) {
                alertas.add("Tu consumo promedio de calorías está por debajo de lo recomendado");
            }
        }

        return alertas;
    }

    public Map<String, Double> getEstadisticas(Map<LocalDate, Double> caloriasPorDia, double caloriasObjetivo) {
        Map<String, Double> estadisticas = new HashMap<>();
        
        if (!caloriasPorDia.isEmpty()) {
            // Calcular estadísticas
            double promedio = caloriasPorDia.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

            double maximo = caloriasPorDia.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);

            double minimo = caloriasPorDia.values().stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);

            double desviacionObjetivo = Math.abs(promedio - caloriasObjetivo) / caloriasObjetivo * 100;

            estadisticas.put("promedio", promedio);
            estadisticas.put("maximo", maximo);
            estadisticas.put("minimo", minimo);
            estadisticas.put("desviacionObjetivo", desviacionObjetivo);
        }

        return estadisticas;
    }
} 