package com.example.demo.model;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "daily_summary")
@Data
public class DailySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "calorias_totales")
    private Double caloriasTotales;

    @Column(name = "calorias_objetivo")
    private Double caloriasObjetivo;
} 