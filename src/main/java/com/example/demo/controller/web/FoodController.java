package com.example.demo.controller.web;

import com.example.demo.model.Food;
import com.example.demo.model.DailySummaryFood;
import com.example.demo.model.User;
import com.example.demo.service.FoodService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public String showSearchPage() {
        return "food/search";
    }

    @GetMapping("/search/results")
    public String searchFoods(
            @RequestParam String query,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Double minCalories,
            @RequestParam(required = false) Double maxCalories,
            @RequestParam(required = false) String marca,
            Model model) {
        
        List<Food> foods = foodService.searchFoods(query, sortBy, sortOrder, minCalories, maxCalories, marca);
        
        model.addAttribute("foods", foods);
        model.addAttribute("query", query);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("minCalories", minCalories);
        model.addAttribute("maxCalories", maxCalories);
        model.addAttribute("marca", marca);
        
        return "food/search-results";
    }

    @GetMapping("/add/{foodId}")
    public String showAddFoodForm(@PathVariable Long foodId, Model model) {
        Food food = foodService.getFoodById(foodId)
                .orElseThrow(() -> new RuntimeException("Alimento no encontrado"));
        
        model.addAttribute("food", food);
        model.addAttribute("dailySummaryFood", new DailySummaryFood());
        return "food/add-meal";
    }

    @PostMapping("/add/{foodId}")
    public String addMealEntry(@PathVariable Long foodId,
                             @ModelAttribute DailySummaryFood dailySummaryFood,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            foodService.addMealEntry(
                user,
                foodId,
                dailySummaryFood.getCantidadG(),
                dailySummaryFood.getComidaTipo(),
                null
            );

            redirectAttributes.addFlashAttribute("success", "Comida añadida correctamente");
            return "redirect:/daily-summary/history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al añadir la comida: " + e.getMessage());
            return "redirect:/food/add/" + foodId;
        }
    }

} 