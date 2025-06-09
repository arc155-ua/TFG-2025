package com.example.demo.controller.web;

import com.example.demo.model.User;
import com.example.demo.service.AnalyticsService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showAnalytics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Si no se proporcionan fechas, usar el último mes por defecto
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Obtener datos para el análisis
        Map<LocalDate, Double> caloriasPorDia = analyticsService.getCaloriasPorDia(user, startDate, endDate);
        List<String> alertas = analyticsService.getAlertas(user, startDate, endDate);
        Map<String, Double> estadisticas = analyticsService.getEstadisticas(caloriasPorDia, user.getCaloriasMetaDiaria());

        model.addAttribute("caloriasPorDia", caloriasPorDia);
        model.addAttribute("alertas", alertas);
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("caloriasObjetivo", user.getCaloriasMetaDiaria());
        
        return "analytics/analisys";
    }
} 