package com.example.demo.service;

import com.example.demo.model.Food;
import com.example.demo.model.DailySummaryFood;
import com.example.demo.model.User;
import com.example.demo.repository.FoodRepository;
import com.example.demo.repository.DailySummaryFoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodService {

    private static final Logger log = LoggerFactory.getLogger(FoodService.class);

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private DailySummaryFoodRepository dailySummaryFoodRepository;

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
    public DailySummaryFood addMealEntry(User user, Long foodId, Double cantidadG, String comidaTipo, String notas) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Alimento no encontrado"));

        // Calcular calorías basadas en la cantidad
        Double calorias = (food.getCalorias100g() * cantidadG) / 100.0;

        // Obtener o crear el resumen diario
        var dailySummary = dailySummaryService.getOrCreateDailySummary(user, LocalDate.now());

        // Crear la entrada de alimento
        DailySummaryFood dailySummaryFood = new DailySummaryFood();
        dailySummaryFood.setDailySummary(dailySummary);
        dailySummaryFood.setFood(food);
        dailySummaryFood.setComidaTipo(comidaTipo);
        dailySummaryFood.setCantidadG(cantidadG);
        dailySummaryFood.setCalorias(calorias);

        // Guardar la entrada
        dailySummaryFood = dailySummaryFoodRepository.save(dailySummaryFood);

        // Actualizar el resumen diario
        dailySummaryService.updateCalories(dailySummary, calorias);

        return dailySummaryFood;
    }

    public List<DailySummaryFood> getMealEntriesForDate(User user, LocalDate date) {
        return dailySummaryFoodRepository.findByUserAndFecha(user, date);
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

    public List<DailySummaryFood> getDailySummaryFoodsForDate(User user, LocalDate date) {
        return dailySummaryFoodRepository.findByUserAndFecha(user, date);
    }

    public Food findByBarcode(String barcode) {
        return foodRepository.findByCodigoBarra(barcode)
                .orElse(null);
    }

    public Food searchFoodFromOpenFoodFacts(String barcode) {
        try {
            String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                
                if (root.path("status").asInt() == 1) {
                    JsonNode product = root.path("product");
                    
                    Food food = new Food();
                    food.setNombre(product.path("product_name").asText());
                    food.setCalorias100g(product.path("nutriments").path("energy-kcal_100g").asDouble());
                    food.setProteinas(product.path("nutriments").path("proteins_100g").asDouble());
                    food.setCarbohidratos(product.path("nutriments").path("carbohydrates_100g").asDouble());
                    food.setGrasas(product.path("nutriments").path("fat_100g").asDouble());
                    food.setCodigoBarra(barcode);
                    
                    return foodRepository.save(food);
                }
            }
        } catch (Exception e) {
            log.error("Error al buscar alimento en Open Food Facts", e);
        }
        return null;
    }
} 