package com.example.demo.repository;

import com.example.demo.model.DailySummary;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailySummaryRepository extends JpaRepository<DailySummary, Long> {
    Optional<DailySummary> findByUserAndFecha(User user, LocalDate fecha);
    List<DailySummary> findByUserAndFechaBetween(User user, LocalDate startDate, LocalDate endDate);
} 