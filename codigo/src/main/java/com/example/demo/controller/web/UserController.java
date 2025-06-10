package com.example.demo.controller.web;

import com.example.demo.model.User;
import com.example.demo.service.DailySummaryService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DailySummaryService dailySummaryService;

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        var dailySummary = dailySummaryService.getOrCreateDailySummary(user, LocalDate.now());
        
        model.addAttribute("user", user);
        model.addAttribute("dailySummary", dailySummary);
        model.addAttribute("isAdmin", user.isAdmin());
        
        return "user/profile";
    }

    @GetMapping("/edit")
    public String showEditProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("user", user);
        return "user/edit-profile";
    }

    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                              @ModelAttribute User user,
                              RedirectAttributes redirectAttributes) {
        try {
            User existingUser = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            user.setId(existingUser.getId());
            user.setEmail(existingUser.getEmail());
            user.setContraseñaHash(existingUser.getContraseñaHash());
            user.setFechaCreacion(existingUser.getFechaCreacion());
            
            userService.updateUserProfile(user);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
            return "redirect:/user/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/user/edit";
        }
    }
} 