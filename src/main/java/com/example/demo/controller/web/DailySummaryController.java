package com.example.demo.controller.web;

import com.example.demo.model.DailySummary;
import com.example.demo.model.User;
import com.example.demo.service.DailySummaryService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/daily-summary")
public class DailySummaryController {

    @Autowired
    private DailySummaryService dailySummaryService;

    @Autowired
    private UserService userService;

    @GetMapping("/history")
    public String showHistory(
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

        // Validar que la fecha de inicio no sea posterior a la fecha de fin
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        List<DailySummary> summaries = dailySummaryService.getDailySummariesForUserInDateRange(user, startDate, endDate);

        model.addAttribute("summaries", summaries);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "daily-summary/history";
    }

    @GetMapping("/{id}")
    public String showSummaryDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DailySummary summary = dailySummaryService.getDailySummaryById(id)
                .orElseThrow(() -> new RuntimeException("Resumen diario no encontrado"));

        // Verificar que el resumen pertenece al usuario actual
        if (!summary.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para ver este resumen");
        }

        model.addAttribute("summary", summary);
        return "daily-summary/details";
    }

    @PostMapping("/{id}/remove-food")
    public String removeFoodFromSummary(
            @PathVariable Long id,
            @RequestParam Long foodEntryId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            DailySummary summary = dailySummaryService.getDailySummaryById(id)
                    .orElseThrow(() -> new RuntimeException("Resumen diario no encontrado"));

            // Verificar que el resumen pertenece al usuario actual
            if (!summary.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("No tienes permiso para modificar este resumen");
            }

            dailySummaryService.removeFoodFromSummary(summary, foodEntryId);
            redirectAttributes.addFlashAttribute("success", "Alimento eliminado correctamente");
            return "redirect:/daily-summary/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el alimento: " + e.getMessage());
            return "redirect:/daily-summary/" + id;
        }
    }
} 