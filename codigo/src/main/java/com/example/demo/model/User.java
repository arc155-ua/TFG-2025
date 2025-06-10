package com.example.demo.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
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

    @Column(name = "is_admin")
    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
} 