package com.example.demo.service;

import com.example.demo.model.DailySummary;
import com.example.demo.model.User;
import com.example.demo.repository.DailySummaryRepository;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
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

    public byte[] generarGraficoCalorias(Map<LocalDate, Double> caloriasPorDia, double caloriasObjetivo) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            
            // Añadir datos de calorías consumidas
            caloriasPorDia.forEach((fecha, calorias) -> 
                dataset.addValue(calorias, "Calorías Consumidas", fecha.toString())
            );
            
            // Añadir línea de objetivo
            caloriasPorDia.keySet().forEach(fecha -> 
                dataset.addValue(caloriasObjetivo, "Objetivo", fecha.toString())
            );

            // Añadir líneas de umbrales
            caloriasPorDia.keySet().forEach(fecha -> {
                dataset.addValue(caloriasObjetivo * 1.2, "Límite Superior", fecha.toString());
                dataset.addValue(caloriasObjetivo * 0.8, "Límite Inferior", fecha.toString());
            });

            JFreeChart chart = ChartFactory.createLineChart(
                "Evolución del Consumo Calórico",
                "Fecha",
                "Calorías",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 400);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el gráfico", e);
        }
    }

    public byte[] generarGraficoTendencia(Map<LocalDate, Double> caloriasPorDia, double caloriasObjetivo) {
        try {
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            
            // Serie de calorías consumidas
            TimeSeries seriesConsumidas = new TimeSeries("Calorías Consumidas");
            // Serie de promedio móvil (últimos 7 días)
            TimeSeries seriesPromedio = new TimeSeries("Promedio Móvil (7 días)");
            
            // Calcular promedio móvil
            List<Double> valores = new ArrayList<>(caloriasPorDia.values());
            for (int i = 0; i < valores.size(); i++) {
                double suma = 0;
                int count = 0;
                for (int j = Math.max(0, i - 6); j <= i; j++) {
                    suma += valores.get(j);
                    count++;
                }
                double promedio = suma / count;
                
                LocalDate fecha = new ArrayList<>(caloriasPorDia.keySet()).get(i);
                seriesPromedio.add(new Day(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant())), promedio);
            }
            
            // Añadir datos de calorías consumidas
            caloriasPorDia.forEach((fecha, calorias) -> 
                seriesConsumidas.add(new Day(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant())), calorias)
            );
            
            dataset.addSeries(seriesConsumidas);
            dataset.addSeries(seriesPromedio);

            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Tendencia de Consumo Calórico",
                "Fecha",
                "Calorías",
                dataset,
                true,
                true,
                false
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 400);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el gráfico", e);
        }
    }
} 