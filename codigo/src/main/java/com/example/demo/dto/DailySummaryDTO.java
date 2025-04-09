package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailySummaryDTO {
    private Long id;
    private Long userId;
    private LocalDate fecha;
    private Double caloriasTotales;
    private Double caloriasObjetivo;
} 