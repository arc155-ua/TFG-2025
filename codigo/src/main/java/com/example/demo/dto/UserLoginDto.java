package com.example.demo.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class UserLoginDto {
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contraseña;
} 