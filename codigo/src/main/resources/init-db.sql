-- Crear la base de datos si no existe
SELECT 'CREATE DATABASE calorietracker'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'calorietracker');

-- Conectar a la base de datos (usando el puerto 5434)
\c calorietracker -p 5434;

-- Crear el esquema si no existe
CREATE SCHEMA IF NOT EXISTS public;

-- Establecer el esquema por defecto
SET search_path TO public;

-- Ejecutar el schema.sql
\i src/main/resources/schema.sql; 