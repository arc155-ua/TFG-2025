package com.example.demo.repository;

import com.example.demo.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByNombreContainingIgnoreCase(String nombre);
    Optional<Food> findByCodigoBarra(String codigoBarra);
} 