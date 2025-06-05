package com.example.demo.service;

import com.example.demo.model.Food;
import com.example.demo.model.Recipe;
import com.example.demo.model.User;
import com.example.demo.repository.FoodRepository;
import com.example.demo.repository.RecipeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeRecommendationService {

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public Recipe getRecipeRecommendation(int remainingCalories, User user) {
        List<ChatMessage> messages = new ArrayList<>();
        
        // Obtener las recetas existentes del usuario
        List<Recipe> existingRecipes = recipeRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        String existingRecipesList = existingRecipes.stream()
                .map(Recipe::getName)
                .collect(Collectors.joining(", "));
        
        // Mensaje del sistema que define el comportamiento
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), 
            "Eres un experto nutricionista y chef que recomienda recetas saludables basadas en calorías restantes. " +
            "Es importante que NO repitas recetas que ya existen."));
        
        // Mensaje del usuario con la solicitud
        String userPrompt = String.format(
            "Recomienda una receta saludable que tenga aproximadamente %d calorías. " +
            "IMPORTANTE: El usuario ya tiene las siguientes recetas: %s. " +
            "Por favor, NO repitas ninguna de estas recetas y sugiere algo diferente.\n\n" +
            "La receta debe incluir:\n" +
            "- Nombre de la receta\n" +
            "- Ingredientes necesarios\n" +
            "- Instrucciones paso a paso\n" +
            "- Valores nutricionales (calorías, proteínas, carbohidratos y grasas)\n\n" +
            "Responde en formato JSON con la siguiente estructura:\n" +
            "{\n" +
            "  \"name\": \"nombre de la receta\",\n" +
            "  \"ingredients\": [\"ingrediente1\", \"ingrediente2\", ...],\n" +
            "  \"instructions\": [\"paso1\", \"paso2\", ...],\n" +
            "  \"nutrition\": {\n" +
            "    \"calories\": X,\n" +
            "    \"proteins\": X,\n" +
            "    \"carbs\": X,\n" +
            "    \"fats\": X\n" +
            "  }\n" +
            "}", remainingCalories, existingRecipesList);
        
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), userPrompt));

        // Crear la solicitud
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(messages)
            .temperature(0.7)
            .build();

        // Obtener la respuesta
        String response = openAiService.createChatCompletion(request)
            .getChoices().get(0).getMessage().getContent();

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            Recipe recipe = new Recipe();
            recipe.setName(jsonNode.get("name").asText());
            recipe.setIngredients(objectMapper.writeValueAsString(jsonNode.get("ingredients")));
            recipe.setInstructions(objectMapper.writeValueAsString(jsonNode.get("instructions")));
            
            JsonNode nutrition = jsonNode.get("nutrition");
            recipe.setCalories(nutrition.get("calories").asInt());
            recipe.setProteins(nutrition.get("proteins").asDouble());
            recipe.setCarbs(nutrition.get("carbs").asDouble());
            recipe.setFats(nutrition.get("fats").asDouble());
            
            recipe.setCreatedAt(LocalDateTime.now());
            recipe.setUser(user);
            
            // Guardar la receta
            recipe = recipeRepository.save(recipe);

            // Crear el alimento correspondiente
            Food food = new Food();
            food.setNombre(recipe.getName());
            food.setCalorias100g(recipe.getCalories().doubleValue());
            food.setProteinas(recipe.getProteins());
            food.setCarbohidratos(recipe.getCarbs());
            food.setGrasas(recipe.getFats());
            foodRepository.save(food);
            
            return recipe;
        } catch (Exception e) {
            throw new RuntimeException("Error procesando la respuesta de ChatGPT", e);
        }
    }

    public List<Recipe> getUserRecipes(User user) {
        return recipeRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
    }

} 