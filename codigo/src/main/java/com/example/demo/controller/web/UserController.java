package com.example.demo.controller.web;

import com.example.demo.model.User;
import com.example.demo.service.DailySummaryService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "user/profile";
    }
} 