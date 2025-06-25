package com.example.demo.model;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "meal_entries")
@Data
public class MealEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "comida_tipo")
    private String comidaTipo;

    @Column(name = "cantidad_g")
    private Double cantidadG;

    private Double calorias;
    private String notas;
} 