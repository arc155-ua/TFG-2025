package com.example.demo.model;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private Integer caloriasObjetivo;

    @OneToMany(mappedBy = "dailySummary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DailySummaryFood> alimentos = new ArrayList<>();

    public void addAlimento(DailySummaryFood alimento) {
        alimentos.add(alimento);
        alimento.setDailySummary(this);
    }

    public void removeAlimento(DailySummaryFood alimento) {
        alimentos.remove(alimento);
        alimento.setDailySummary(null);
    }
} 