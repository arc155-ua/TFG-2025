package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CalorieCalculatorService calorieCalculatorService;

    @Transactional
    public User save(User user) {
        if (user.getContraseñaHash() != null) {
            user.setContraseñaHash(passwordEncoder.encode(user.getContraseñaHash()));
        }
        if (user.getFechaCreacion() == null) {
            user.setFechaCreacion(LocalDateTime.now());
        }
        
        // Calcular calorías meta diaria usando la fórmula de Mifflin-St Jeor (más precisa)
        user.setCaloriasMetaDiaria(calorieCalculatorService.calculateCalories(user, CalorieCalculatorService.Formula.MIFFLIN_ST_JEOR));
        
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User updateUserProfile(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Actualizar campos básicos
        existingUser.setNombre(user.getNombre());
        existingUser.setEdad(user.getEdad());
        existingUser.setPesoKg(user.getPesoKg());
        existingUser.setAlturaCm(user.getAlturaCm());
        existingUser.setGenero(user.getGenero());
        existingUser.setNivelActividad(user.getNivelActividad());
        existingUser.setObjetivo(user.getObjetivo());
        
        // Recalcular calorías meta diaria
        existingUser.setCaloriasMetaDiaria(calorieCalculatorService.calculateCalories(existingUser, CalorieCalculatorService.Formula.MIFFLIN_ST_JEOR));
        
        return userRepository.save(existingUser);
    }
} 