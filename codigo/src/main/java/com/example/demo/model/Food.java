package com.example.demo.model;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "foods")
@Data
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String marca;

    @Column(length = 1000)
    private String categoria;
    
    @Column(name = "calorias_100g")
    private Double calorias100g;
    
    @Column(nullable = false)
    private Double grasas;

    @Column(nullable = false)
    private Double proteinas;

    @Column(nullable = false)
    private Double carbohidratos;
    
    @Column(name = "fuente_datos")
    private String fuenteDatos;
    
    @Column(name = "codigo_barra")
    private String codigoBarra;
} 