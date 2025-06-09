package com.example.demo.repository;

import com.example.demo.model.DailySummary;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, Long> {
    Optional<DailySummary> findByUserAndFecha(User user, LocalDate fecha);
    
    @Query("SELECT DISTINCT ds FROM DailySummary ds LEFT JOIN FETCH ds.alimentos WHERE ds.user = :user ORDER BY ds.fecha DESC")
    List<DailySummary> findByUserOrderByFechaDesc(@Param("user") User user);

    @Query("SELECT DISTINCT ds FROM DailySummary ds LEFT JOIN FETCH ds.alimentos WHERE ds.user = :user AND ds.fecha BETWEEN :startDate AND :endDate ORDER BY ds.fecha DESC")
    List<DailySummary> findByUserAndFechaBetweenOrderByFechaDesc(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT ds FROM DailySummary ds LEFT JOIN FETCH ds.alimentos WHERE ds.user = :user AND ds.fecha BETWEEN :startDate AND :endDate ORDER BY ds.fecha ASC")
    List<DailySummary> findByUserAndFechaBetweenOrderByFechaAsc(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT ds FROM DailySummary ds LEFT JOIN FETCH ds.alimentos WHERE ds.id = :id")
    Optional<DailySummary> findById(@Param("id") Long id);

    Optional<DailySummary> findByUserIdAndFecha(Long userId, LocalDate fecha);
} 