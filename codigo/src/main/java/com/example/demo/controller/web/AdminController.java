package com.example.demo.controller.web;

import com.example.demo.model.User;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private DailySummaryService dailySummaryService;

    @Autowired
    private RecipeRecommendationService recipeService;

    @Autowired
    private FoodService foodService;

    private boolean isAdmin(UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.isAdmin();
    }

    @GetMapping
    public String adminPanel(@AuthenticationPrincipal UserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            return "redirect:/user/profile";
        }
        return "admin/index";
    }

    @GetMapping("/users")
    public String listUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        if (!isAdmin(userDetails)) {
            return "redirect:/user/profile";
        }
        Pageable pageable = PageRequest.of(page, 50);
        Page<User> users = userService.findAll(pageable);
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/daily-summaries")
    public String listDailySummaries(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        if (!isAdmin(userDetails)) {
            return "redirect:/user/profile";
        }
        Pageable pageable = PageRequest.of(page, 50);
        var summaries = dailySummaryService.findAll(pageable);
        model.addAttribute("summaries", summaries);
        return "admin/daily-summaries";
    }

    @GetMapping("/recipes")
    public String listRecipes(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        if (!isAdmin(userDetails)) {
            return "redirect:/user/profile";
        }
        Pageable pageable = PageRequest.of(page, 50);
        var recipes = recipeService.findAll(pageable);
        model.addAttribute("recipes", recipes);
        return "admin/recipes";
    }

    @GetMapping("/foods")
    public String listFoods(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        if (!isAdmin(userDetails)) {
            return "redirect:/user/profile";
        }
        Pageable pageable = PageRequest.of(page, 50);
        var foods = foodService.findAll(pageable);
        model.addAttribute("foods", foods);
        return "admin/foods";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/daily-summaries/{id}/delete")
    public String deleteDailySummary(@PathVariable Long id) {
        dailySummaryService.deleteById(id);
        return "redirect:/admin/daily-summaries";
    }

    @PostMapping("/recipes/{id}/delete")
    public String deleteRecipe(@PathVariable Long id) {
        recipeService.deleteById(id);
        return "redirect:/admin/recipes";
    }

    @PostMapping("/foods/{id}/delete")
    public String deleteFood(@PathVariable Long id) {
        foodService.deleteById(id);
        return "redirect:/admin/foods";
    }
} 