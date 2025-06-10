package com.example.demo.service;

import com.example.demo.model.Food;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenFoodFactsService {

    @Value("${openfoodfacts.api.url:https://world.openfoodfacts.org/cgi/search.pl}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public OpenFoodFactsService() {
        this.restTemplate = new RestTemplate();
    }

    public List<Food> searchFoods(String query) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("search_terms", query)
                .queryParam("search_simple", "1")
                .queryParam("action", "process")
                .queryParam("json", "1")
                .build()
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> products = (List<Map<String, Object>>) response.get("products");
            
            List<Food> foods = new ArrayList<>();
            for (Map<String, Object> product : products) {
                Food food = new Food();
                food.setNombre((String) product.get("product_name"));
                food.setMarca((String) product.get("brands"));
                food.setCategoria((String) product.get("categories"));
                food.setCodigoBarra((String) product.get("code"));
                food.setFuenteDatos("OpenFoodFacts");

                // Nutrientes por 100g
                Map<String, Object> nutriments = (Map<String, Object>) product.get("nutriments");
                if (nutriments != null) {
                    food.setCalorias100g(getDoubleValue(nutriments, "energy-kcal_100g"));
                    food.setProteinas(getDoubleValue(nutriments, "proteins_100g"));
                    food.setCarbohidratos(getDoubleValue(nutriments, "carbohydrates_100g"));
                    food.setGrasas(getDoubleValue(nutriments, "fat_100g"));
                }

                foods.add(food);
            }
            return foods;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar alimentos en OpenFoodFacts: " + e.getMessage());
        }
    }

    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Food getFoodById(Long id) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("id", id)
                .queryParam("action", "process")
                .queryParam("json", "1")
                .build()
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> products = (List<Map<String, Object>>) response.get("products");
            
            if (products != null && !products.isEmpty()) {
                Map<String, Object> product = products.get(0);
                return mapProductToFood(product);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar alimento por ID en OpenFoodFacts: " + e.getMessage());
        }
    }

    private Food mapProductToFood(Map<String, Object> product) {
        Food food = new Food();
        food.setNombre((String) product.get("product_name"));
        food.setMarca((String) product.get("brands"));
        food.setCategoria((String) product.get("categories"));
        food.setCodigoBarra((String) product.get("code"));
        food.setFuenteDatos("OpenFoodFacts");

        // Nutrientes por 100g
        Map<String, Object> nutriments = (Map<String, Object>) product.get("nutriments");
        if (nutriments != null) {
            food.setCalorias100g(getDoubleValue(nutriments, "energy-kcal_100g"));
            food.setProteinas(getDoubleValue(nutriments, "proteins_100g"));
            food.setCarbohidratos(getDoubleValue(nutriments, "carbohydrates_100g"));
            food.setGrasas(getDoubleValue(nutriments, "fat_100g"));
        }

        return food;
    }
} 