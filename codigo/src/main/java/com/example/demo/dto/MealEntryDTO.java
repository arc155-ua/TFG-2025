package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MealEntryDTO {
    private Long id;
    private Long userId;
    private Long foodId;
    private LocalDate fecha;
    private String comidaTipo;
    private Double cantidadG;
    private Double calorias;
    private String notas;
} 