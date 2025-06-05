package com.example.demo.controller;

import com.example.demo.model.Recipe;
import com.example.demo.model.User;
import com.example.demo.model.DailySummaryFood;
import com.example.demo.service.RecipeRecommendationService;
import com.example.demo.service.DailySummaryService;
import com.example.demo.service.UserService;
import com.example.demo.service.FoodService;
import com.example.demo.repository.FoodRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.time.LocalDate;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeRecommendationService recipeService;

    @Autowired
    private DailySummaryService dailySummaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private FoodService foodService;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/history")
    public String showHistoryPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Recipe> recipes = recipeService.getUserRecipes(user);
        model.addAttribute("recipes", recipes);
        return "recipes/history";
    }

    @GetMapping("/recommend")
    public String showRecommendationPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        int remainingCalories = dailySummaryService.getRemainingCalories(user.getId(), java.time.LocalDate.now());
        if (remainingCalories > 600) {
            remainingCalories = 600;
        }
        
        Recipe recipe = recipeService.getRecipeRecommendation(remainingCalories, user);
        return "redirect:/recipes/" + recipe.getId();
    }

    @GetMapping("/{id}")
    public String showRecipeDetails(@PathVariable Long id, Model model) {
        Recipe recipe = recipeService.getRecipeById(id);
        try {
            List<String> ingredients = objectMapper.readValue(recipe.getIngredients(), 
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            List<String> instructions = objectMapper.readValue(recipe.getInstructions(), 
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            
            model.addAttribute("recipe", recipe);
            model.addAttribute("ingredients", ingredients);
            model.addAttribute("instructions", instructions);
            return "recipes/details";
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error procesando la receta", e);
        }
    }

    @GetMapping("/{id}/consume")
    public String showConsumeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeService.getRecipeById(id);
        model.addAttribute("recipe", recipe);
        model.addAttribute("dailySummaryFood", new DailySummaryFood());
        return "recipes/consume";
    }

    @PostMapping("/{id}/consume")
    public String consumeRecipe(
            @PathVariable Long id,
            @ModelAttribute DailySummaryFood dailySummaryFood,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            Recipe recipe = recipeService.getRecipeById(id);
            Long foodId = foodRepository.findByNombreContainingIgnoreCase(recipe.getName())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró el alimento correspondiente a la receta"))
                    .getId();
            
            foodService.addMealEntry(
                user,
                foodId,
                100.0,
                dailySummaryFood.getComidaTipo(),
                null
            );
            
            redirectAttributes.addFlashAttribute("success", "Receta añadida al resumen diario");
            return "redirect:/daily-summary/history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al añadir la receta: " + e.getMessage());
            return "redirect:/recipes/" + id + "/consume";
        }
    }

    @GetMapping("/api/recommend")
    @ResponseBody
    public ResponseEntity<Recipe> getRecipeRecommendation(
            @RequestParam int remainingCalories,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Recipe recommendation = recipeService.getRecipeRecommendation(remainingCalories, user);
            return ResponseEntity.ok(recommendation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/history")
    @ResponseBody
    public ResponseEntity<List<Recipe>> getUserRecipes(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            List<Recipe> recipes = recipeService.getUserRecipes(user);
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 