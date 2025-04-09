package com.example.demo.repository;

import com.example.demo.model.MealEntry;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {
    List<MealEntry> findByUserAndFecha(User user, LocalDate fecha);
    List<MealEntry> findByUserAndFechaBetween(User user, LocalDate startDate, LocalDate endDate);
} 