package com.example.demo.repository;

import com.example.demo.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByNombreContainingIgnoreCase(String nombre);
    Optional<Food> findByCodigoBarra(String codigoBarra);
} 