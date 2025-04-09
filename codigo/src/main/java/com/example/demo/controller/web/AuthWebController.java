package com.example.demo.controller.web;

import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthWebController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userLogin", new UserLoginDto());
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistration", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegistration") UserRegistrationDto registrationDto,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(registrationDto);
            return "redirect:/auth/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("userLogin") UserLoginDto loginDto,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/login";
        }

        try {
            authService.login(loginDto);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "auth/login";
        }
    }
} 