-- Usuarios de ejemplo
INSERT INTO users (nombre, email, password_hash, edad, peso_kg, altura_cm, genero, nivel_actividad, objetivo, calorias_meta_diaria, fecha_creacion, is_admin)
VALUES
('Administrador', 'admin@calorietracker.com', '$2a$10$EjemploHashAdmin', 30, 75, 180, 'hombre', 'alta', 'mantener', 2500, NOW(), TRUE),
('Usuario Prueba', 'usuario@calorietracker.com', '$2a$10$EjemploHashUsuario', 25, 65, 170, 'mujer', 'media', 'perder', 1800, NOW(), FALSE),
('Laura Martínez', 'laura.martinez@gmail.com', '$2a$10$EjemploHashLaura', 28, 60, 165, 'mujer', 'baja', 'ganar', 2200, NOW(), FALSE),
('Carlos Pérez', 'carlos.perez@gmail.com', '$2a$10$EjemploHashCarlos', 35, 80, 175, 'hombre', 'media', 'mantener', 2400, NOW(), FALSE),
('Ana Gómez', 'ana.gomez@gmail.com', '$2a$10$EjemploHashAna', 22, 55, 160, 'mujer', 'alta', 'perder', 1700, NOW(), FALSE),
('David Ruiz', 'david.ruiz@gmail.com', '$2a$10$EjemploHashDavid', 40, 90, 185, 'hombre', 'baja', 'mantener', 2600, NOW(), FALSE),
('Marta López', 'marta.lopez@gmail.com', '$2a$10$EjemploHashMarta', 31, 68, 172, 'mujer', 'media', 'ganar', 2300, NOW(), FALSE),
('Javier Torres', 'javier.torres@gmail.com', '$2a$10$EjemploHashJavier', 27, 77, 178, 'hombre', 'alta', 'mantener', 2500, NOW(), FALSE),
('Sara Fernández', 'sara.fernandez@gmail.com', '$2a$10$EjemploHashSara', 24, 62, 168, 'mujer', 'media', 'perder', 1800, NOW(), FALSE),
('Pedro Sánchez', 'pedro.sanchez@gmail.com', '$2a$10$EjemploHashPedro', 38, 85, 182, 'hombre', 'baja', 'ganar', 2700, NOW(), FALSE);

-- Alimentos de ejemplo
INSERT INTO foods (name, brand, calories, proteins, carbs, fats, portion_size, portion_unit, category)
VALUES
('Manzana', 'Genérico', 52, 0.3, 14, 0.2, 100, 'g', 'Fruta'),
('Pechuga de Pollo', 'Genérico', 165, 31, 0, 3.6, 100, 'g', 'Carne'),
('Arroz Integral', 'Genérico', 111, 2.6, 23, 0.9, 100, 'g', 'Cereal'),
('Aceite de Oliva', 'Genérico', 884, 0, 0, 100, 10, 'ml', 'Grasa'),
('Plátano', 'Genérico', 89, 1.1, 23, 0.3, 100, 'g', 'Fruta'),
('Salmón', 'Genérico', 208, 20, 0, 13, 100, 'g', 'Pescado'),
('Pan Integral', 'Genérico', 247, 8.7, 41, 3.4, 100, 'g', 'Cereal'),
('Leche Desnatada', 'Genérico', 35, 3.4, 5, 0.1, 100, 'ml', 'Lácteo'),
('Huevo', 'Genérico', 155, 13, 1.1, 11, 100, 'g', 'Huevo'),
('Tomate', 'Genérico', 18, 0.9, 3.9, 0.2, 100, 'g', 'Verdura'),
('Aguacate', 'Genérico', 160, 2, 9, 15, 100, 'g', 'Fruta'),
('Yogur Natural', 'Genérico', 61, 3.5, 4.7, 3.3, 100, 'g', 'Lácteo'),
('Queso Fresco', 'Genérico', 98, 11, 3.4, 4.3, 100, 'g', 'Lácteo'),
('Pasta Integral', 'Genérico', 124, 5, 25, 0.9, 100, 'g', 'Cereal'),
('Atún en Aceite', 'Genérico', 198, 25, 0, 11, 100, 'g', 'Pescado'),
('Garbanzos Cocidos', 'Genérico', 120, 7, 17, 2, 100, 'g', 'Legumbre'),
('Espinacas', 'Genérico', 23, 2.9, 1.1, 0.4, 100, 'g', 'Verdura'),
('Zanahoria', 'Genérico', 41, 0.9, 10, 0.2, 100, 'g', 'Verdura'),
('Pera', 'Genérico', 57, 0.4, 15, 0.1, 100, 'g', 'Fruta'),
('Pollo Asado', 'Genérico', 190, 28, 0, 8, 100, 'g', 'Carne');

-- Recetas de ejemplo
INSERT INTO recipes (name, ingredients, instructions, calories, proteins, carbs, fats, created_at, user_id)
VALUES
('Ensalada de Pollo', 'Pechuga de Pollo, Lechuga, Tomate, Aceite de Oliva', 'Cocinar el pollo, mezclar con los demás ingredientes y aliñar.', 350, 35, 10, 15, NOW(), 1),
('Arroz con Verduras', 'Arroz Integral, Zanahoria, Guisantes, Aceite de Oliva', 'Cocinar el arroz y las verduras, mezclar y servir.', 250, 6, 45, 5, NOW(), 2),
('Tortilla de Espinacas', 'Huevo, Espinacas, Aceite de Oliva, Sal', 'Batir los huevos, añadir espinacas y freír.', 200, 14, 3, 15, NOW(), 3),
('Salmón al Horno', 'Salmón, Aceite de Oliva, Limón, Sal', 'Hornear el salmón con aceite y limón.', 300, 25, 0, 20, NOW(), 4),
('Pasta Integral con Atún', 'Pasta Integral, Atún en Aceite, Tomate, Aceite de Oliva', 'Cocer la pasta, mezclar con atún y tomate.', 400, 20, 60, 10, NOW(), 5),
('Garbanzos con Espinacas', 'Garbanzos Cocidos, Espinacas, Aceite de Oliva, Ajo', 'Saltear espinacas y garbanzos con ajo.', 320, 12, 40, 8, NOW(), 6),
('Yogur con Fruta', 'Yogur Natural, Plátano, Manzana, Pera', 'Mezclar el yogur con la fruta troceada.', 180, 6, 30, 3, NOW(), 7),
('Pollo Asado con Verduras', 'Pollo Asado, Zanahoria, Tomate, Aceite de Oliva', 'Asar el pollo con las verduras.', 420, 38, 15, 18, NOW(), 8),
('Queso Fresco con Aguacate', 'Queso Fresco, Aguacate, Aceite de Oliva', 'Cortar el queso y el aguacate, aliñar.', 250, 13, 10, 18, NOW(), 9),
('Pan Integral con Huevo', 'Pan Integral, Huevo, Aceite de Oliva', 'Tostar el pan, añadir huevo frito.', 300, 12, 35, 10, NOW(), 10);

-- Entradas de comidas de ejemplo
INSERT INTO meal_entries (user_id, food_id, fecha, comida_tipo, cantidad_g, calorias, notas)
VALUES
(1, 1, CURRENT_DATE, 'desayuno', 150, 78, 'Manzana en el desayuno'),
(2, 2, CURRENT_DATE, 'almuerzo', 200, 330, 'Pechuga de pollo a la plancha'),
(3, 5, CURRENT_DATE, 'merienda', 120, 107, 'Plátano para merendar'),
(4, 6, CURRENT_DATE, 'cena', 180, 374, 'Salmón al horno para cenar'),
(5, 7, CURRENT_DATE, 'desayuno', 50, 124, 'Pan integral en el desayuno'),
(6, 16, CURRENT_DATE, 'almuerzo', 100, 120, 'Garbanzos cocidos en el almuerzo'),
(7, 12, CURRENT_DATE, 'merienda', 125, 76, 'Yogur natural con fruta'),
(8, 20, CURRENT_DATE, 'cena', 200, 380, 'Pollo asado con verduras'),
(9, 11, CURRENT_DATE, 'desayuno', 80, 128, 'Aguacate en el desayuno'),
(10, 9, CURRENT_DATE, 'almuerzo', 60, 93, 'Huevo en el almuerzo'),
(1, 3, CURRENT_DATE, 'cena', 100, 111, 'Arroz integral para cenar'),
(2, 4, CURRENT_DATE, 'merienda', 10, 88, 'Aceite de oliva en la merienda'),
(3, 8, CURRENT_DATE, 'cena', 200, 70, 'Leche desnatada para cenar'),
(4, 10, CURRENT_DATE, 'almuerzo', 100, 18, 'Tomate en el almuerzo'),
(5, 13, CURRENT_DATE, 'desayuno', 100, 98, 'Queso fresco en el desayuno'),
(6, 14, CURRENT_DATE, 'cena', 150, 186, 'Pasta integral para cenar'),
(7, 15, CURRENT_DATE, 'almuerzo', 90, 178, 'Atún en aceite en el almuerzo'),
(8, 17, CURRENT_DATE, 'merienda', 100, 23, 'Espinacas en la merienda'),
(9, 18, CURRENT_DATE, 'cena', 100, 41, 'Zanahoria para cenar'),
(10, 19, CURRENT_DATE, 'desayuno', 100, 57, 'Pera en el desayuno');
