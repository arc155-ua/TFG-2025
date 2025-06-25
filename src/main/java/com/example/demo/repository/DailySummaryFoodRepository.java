package com.example.demo.repository;

import com.example.demo.model.DailySummaryFood;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailySummaryFoodRepository extends JpaRepository<DailySummaryFood, Long> {
    List<DailySummaryFood> findByDailySummaryId(Long dailySummaryId);
    List<DailySummaryFood> findByFoodId(Long foodId);
    @Query("SELECT dsf FROM DailySummaryFood dsf JOIN FETCH dsf.dailySummary ds WHERE ds.user = :user AND ds.fecha = :fecha")
    List<DailySummaryFood> findByUserAndFecha(@Param("user") User user, @Param("fecha") LocalDate fecha);
} 