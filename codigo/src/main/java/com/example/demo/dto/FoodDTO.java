package com.example.demo.dto;

import lombok.Data;

@Data
public class FoodDTO {
    private Long id;
    private String nombre;
    private String marca;
    private String categoria;
    private Double calorias100g;
    private Double grasas;
    private Double proteinas;
    private Double carbohidratos;
    private String fuenteDatos;
    private String codigoBarra;
} 