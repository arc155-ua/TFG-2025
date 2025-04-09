package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String nombre;
    private String email;
    private Integer edad;
    private Double pesoKg;
    private Double alturaCm;
    private String genero;
    private String nivelActividad;
    private String objetivo;
    private Integer caloriasMetaDiaria;
    private LocalDateTime fechaCreacion;
} 