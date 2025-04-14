package com.example.demo.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class UserRegistrationDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 1, message = "La edad debe ser mayor que 0")
    private Integer edad;

    @NotNull(message = "El peso es obligatorio")
    @Min(value = 1, message = "El peso debe ser mayor que 0")
    private Double pesoKg;

    @NotNull(message = "La altura es obligatoria")
    @Min(value = 1, message = "La altura debe ser mayor que 0")
    private Double alturaCm;

    @NotBlank(message = "El género es obligatorio")
    private String genero;

    @NotBlank(message = "El nivel de actividad es obligatorio")
    private String nivelActividad;

    @NotBlank(message = "El objetivo es obligatorio")
    private String objetivo;
} 