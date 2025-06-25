package com.example.demo.model;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "daily_summary_foods")
@Data
public class DailySummaryFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "daily_summary_id", nullable = false)
    private DailySummary dailySummary;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(name = "cantidad_g", nullable = false)
    private Double cantidadG;

    @Column(nullable = false)
    private Double calorias;

    @Column(name = "comida_tipo")
    private String comidaTipo;
} 