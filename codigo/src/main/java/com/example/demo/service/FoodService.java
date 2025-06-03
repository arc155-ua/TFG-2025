package com.example.demo.service;

import com.example.demo.model.Food;
import com.example.demo.model.MealEntry;
import com.example.demo.model.User;
import com.example.demo.repository.FoodRepository;
import com.example.demo.repository.MealEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private MealEntryRepository mealEntryRepository;

    @Autowired
    private OpenFoodFactsService openFoodFactsService;

    @Autowired
    private DailySummaryService dailySummaryService;

    public List<Food> searchFoods(String query, String sortBy, String sortOrder, 
                                Double minCalories, Double maxCalories, String marca) {
        // Primero buscar en la base de datos local
        List<Food> foods = foodRepository.findByNombreContainingIgnoreCase(query);
        
        // Si no hay resultados, buscar en OpenFoodFacts
        if (foods.isEmpty()) {
            foods = openFoodFactsService.searchFoods(query);
            // Guardar los resultados en la base de datos local
            for (Food food : foods) {
                foodRepository.save(food);
            }
        }
        
        // Aplicar filtros
        if (minCalories != null) {
            foods = foods.stream()
                    .filter(food -> food.getCalorias100g() >= minCalories)
                    .collect(Collectors.toList());
        }
        
        if (maxCalories != null) {
            foods = foods.stream()
                    .filter(food -> food.getCalorias100g() <= maxCalories)
                    .collect(Collectors.toList());
        }

        if (marca != null && !marca.trim().isEmpty()) {
            foods = foods.stream()
                    .filter(food -> food.getMarca() != null && 
                            food.getMarca().toLowerCase().contains(marca.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        // Aplicar ordenación
        if (sortBy != null && !sortBy.isEmpty()) {
            Comparator<Food> comparator = getComparator(sortBy);
            if ("desc".equalsIgnoreCase(sortOrder)) {
                comparator = comparator.reversed();
            }
            foods.sort(comparator);
        }
        
        return foods;
    }

    private Comparator<Food> getComparator(String sortBy) {
        Comparator<Food> comparator;
        switch (sortBy.toLowerCase()) {
            case "calorias":
                comparator = Comparator.comparing(Food::getCalorias100g);
                break;
            case "proteinas":
                comparator = Comparator.comparing(Food::getProteinas);
                break;
            case "carbohidratos":
                comparator = Comparator.comparing(Food::getCarbohidratos);
                break;
            case "grasas":
                comparator = Comparator.comparing(Food::getGrasas);
                break;
            case "nombre":
                comparator = Comparator.comparing(Food::getNombre);
                break;
            default:
                comparator = Comparator.comparing(Food::getNombre);
        }
        return comparator;
    }

    public List<String> getAllCategories() {
        return foodRepository.findAll().stream()
                .map(Food::getCategoria)
                .filter(category -> category != null && !category.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Transactional
    public MealEntry addMealEntry(User user, Long foodId, Double cantidadG, String comidaTipo, String notas) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Alimento no encontrado"));

        // Calcular calorías basadas en la cantidad
        Double calorias = (food.getCalorias100g() * cantidadG) / 100.0;

        MealEntry mealEntry = new MealEntry();
        mealEntry.setUser(user);
        mealEntry.setFood(food);
        mealEntry.setFecha(LocalDate.now());
        mealEntry.setComidaTipo(comidaTipo);
        mealEntry.setCantidadG(cantidadG);
        mealEntry.setCalorias(calorias);
        mealEntry.setNotas(notas);

        mealEntry = mealEntryRepository.save(mealEntry);

        // Actualizar el resumen diario
        dailySummaryService.updateCalories(
            dailySummaryService.getOrCreateDailySummary(user, LocalDate.now()),
            mealEntry.getCalorias()
        );

        return mealEntry;
    }

    public List<MealEntry> getMealEntriesForDate(User user, LocalDate date) {
        return mealEntryRepository.findByUserAndFecha(user, date);
    }

    public Optional<Food> getFoodById(Long id) {
        Optional<Food> food = foodRepository.findById(id);
        if (food.isEmpty()) {
            // Si no existe en la base de datos local, intentar buscarlo en OpenFoodFacts
            try {
                Food openFoodFactsFood = openFoodFactsService.getFoodById(id);
                if (openFoodFactsFood != null) {
                    // Guardar el alimento en la base de datos local
                    openFoodFactsFood = foodRepository.save(openFoodFactsFood);
                    return Optional.of(openFoodFactsFood);
                }
            } catch (Exception e) {
                // Si hay algún error al buscar en OpenFoodFacts, simplemente devolvemos el Optional vacío
                return Optional.empty();
            }
        }
        return food;
    }

    public Optional<Food> getFoodByBarcode(String barcode) {
        Optional<Food> food = foodRepository.findByCodigoBarra(barcode);
        if (food.isEmpty()) {
            // Si no existe en la base de datos local, intentar buscarlo en OpenFoodFacts
            try {
                Food openFoodFactsFood = openFoodFactsService.getFoodByBarcode(barcode);
                if (openFoodFactsFood != null) {
                    // Guardar el alimento en la base de datos local
                    openFoodFactsFood = foodRepository.save(openFoodFactsFood);
                    return Optional.of(openFoodFactsFood);
                }
            } catch (Exception e) {
                // Si hay algún error al buscar en OpenFoodFacts, simplemente devolvemos el Optional vacío
                return Optional.empty();
            }
        }
        return food;
    }
} 