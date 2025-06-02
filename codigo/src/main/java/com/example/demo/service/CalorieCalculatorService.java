package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

@Service
public class CalorieCalculatorService {

    public enum Formula {
        HARRIS_BENEDICT,
        MIFFLIN_ST_JEOR
    }

    public int calculateCalories(User user, Formula formula) {
        double bmr = calculateBMR(user, formula);
        double tdee = calculateTDEE(bmr, user.getNivelActividad());
        return adjustForGoal(tdee, user.getObjetivo());
    }

    private double calculateBMR(User user, Formula formula) {
        if (formula == Formula.HARRIS_BENEDICT) {
            return calculateHarrisBenedictBMR(user);
        } else {
            return calculateMifflinStJeorBMR(user);
        }
    }

    private double calculateHarrisBenedictBMR(User user) {
        if (user.getGenero().equalsIgnoreCase("hombre")) {
            return 88.362 + (13.397 * user.getPesoKg()) + (4.799 * user.getAlturaCm()) - (5.677 * user.getEdad());
        } else {
            return 447.593 + (9.247 * user.getPesoKg()) + (3.098 * user.getAlturaCm()) - (4.330 * user.getEdad());
        }
    }

    private double calculateMifflinStJeorBMR(User user) {
        if (user.getGenero().equalsIgnoreCase("hombre")) {
            return (10 * user.getPesoKg()) + (6.25 * user.getAlturaCm()) - (5 * user.getEdad()) + 5;
        } else {
            return (10 * user.getPesoKg()) + (6.25 * user.getAlturaCm()) - (5 * user.getEdad()) - 161;
        }
    }

    private double calculateTDEE(double bmr, String nivelActividad) {
        switch (nivelActividad.toLowerCase()) {
            case "sedentario":
                return bmr * 1.2;
            case "ligero":
                return bmr * 1.375;
            case "moderado":
                return bmr * 1.55;
            case "activo":
                return bmr * 1.725;
            case "muy activo":
                return bmr * 1.9;
            default:
                return bmr * 1.2;
        }
    }

    private int adjustForGoal(double tdee, String objetivo) {
        switch (objetivo.toLowerCase()) {
            case "perder peso":
                return (int) Math.round(tdee * 0.85); // Déficit de 15%
            case "ganar peso":
                return (int) Math.round(tdee * 1.15); // Superávit de 15%
            default:
                return (int) Math.round(tdee);
        }
    }
} 