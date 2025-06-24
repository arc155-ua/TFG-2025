-- Datos de prueba para los tests

-- Usuario de prueba
INSERT INTO users (nombre, email, password_hash, edad, peso_kg, altura_cm, genero, nivel_actividad, objetivo, calorias_meta_diaria, fecha_creacion, is_admin)
VALUES ('Test User', 'test@example.com', '$2a$10$test.hash', 25, 70, 175, 'hombre', 'media', 'mantener', 2000, NOW(), false);

-- Usuario administrador de prueba
INSERT INTO users (nombre, email, password_hash, edad, peso_kg, altura_cm, genero, nivel_actividad, objetivo, calorias_meta_diaria, fecha_creacion, is_admin)
VALUES ('Test Admin', 'admin@example.com', '$2a$10$test.hash', 30, 80, 180, 'hombre', 'alta', 'mantener', 2500, NOW(), true);

-- Alimentos de prueba
INSERT INTO foods (name, brand, calories, proteins, carbs, fats, portion_size, portion_unit, category)
VALUES 
('Manzana Test', 'Test Brand', 52, 0.3, 14, 0.2, 100, 'g', 'Fruta'),
('Pollo Test', 'Test Brand', 165, 31, 0, 3.6, 100, 'g', 'Carne'),
('Arroz Test', 'Test Brand', 111, 2.6, 23, 0.9, 100, 'g', 'Cereal');

-- Receta de prueba
INSERT INTO recipes (name, ingredients, instructions, calories, proteins, carbs, fats, created_at, user_id)
VALUES ('Receta Test', 'Ingrediente 1, Ingrediente 2', 'Instrucciones de prueba', 300, 20, 30, 10, NOW(), 1); 