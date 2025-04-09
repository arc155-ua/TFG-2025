package com.example.demo.model;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contraseñaHash;

    private Integer edad;
    
    @Column(name = "peso_kg")
    private Double pesoKg;
    
    @Column(name = "altura_cm")
    private Double alturaCm;
    
    private String genero;
    
    @Column(name = "nivel_actividad")
    private String nivelActividad;
    
    private String objetivo;
    
    @Column(name = "calorias_meta_diaria")
    private Integer caloriasMetaDiaria;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
} 