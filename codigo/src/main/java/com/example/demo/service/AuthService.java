package com.example.demo.service;

import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User register(UserRegistrationDto registrationDto) {
        // Verificar si el email ya existe
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setNombre(registrationDto.getNombre());
        user.setEmail(registrationDto.getEmail());
        user.setContraseñaHash(passwordEncoder.encode(registrationDto.getContraseña()));
        user.setEdad(registrationDto.getEdad());
        user.setPesoKg(registrationDto.getPesoKg());
        user.setAlturaCm(registrationDto.getAlturaCm());
        user.setGenero(registrationDto.getGenero());
        user.setNivelActividad(registrationDto.getNivelActividad());
        user.setObjetivo(registrationDto.getObjetivo());
        user.setFechaCreacion(LocalDateTime.now());

        // Calcular calorías meta diaria basada en los datos del usuario
        user.setCaloriasMetaDiaria(calcularCaloriasMetaDiaria(user));

        return userRepository.save(user);
    }

    public User login(UserLoginDto loginDto) {
        return userRepository.findByEmail(loginDto.getEmail())
                .filter(user -> passwordEncoder.matches(loginDto.getContraseña(), user.getContraseñaHash()))
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));
    }

    private Integer calcularCaloriasMetaDiaria(User user) {
        // Fórmula básica de Harris-Benedict
        double bmr;
        if (user.getGenero().equalsIgnoreCase("hombre")) {
            bmr = 88.362 + (13.397 * user.getPesoKg()) + (4.799 * user.getAlturaCm()) - (5.677 * user.getEdad());
        } else {
            bmr = 447.593 + (9.247 * user.getPesoKg()) + (3.098 * user.getAlturaCm()) - (4.330 * user.getEdad());
        }

        // Factor de actividad
        double factorActividad;
        switch (user.getNivelActividad()) {
            case "sedentario":
                factorActividad = 1.2;
                break;
            case "ligero":
                factorActividad = 1.375;
                break;
            case "moderado":
                factorActividad = 1.55;
                break;
            case "activo":
                factorActividad = 1.725;
                break;
            case "muy activo":
                factorActividad = 1.9;
                break;
            default:
                factorActividad = 1.2;
        }

        // Ajuste según objetivo
        double factorObjetivo;
        switch (user.getObjetivo()) {
            case "perder peso":
                factorObjetivo = 0.85;
                break;
            case "ganar peso":
                factorObjetivo = 1.15;
                break;
            default:
                factorObjetivo = 1.0;
        }

        return (int) Math.round(bmr * factorActividad * factorObjetivo);
    }
} 