-- Crear secuencias
CREATE SEQUENCE IF NOT EXISTS users_id_seq;
CREATE SEQUENCE IF NOT EXISTS foods_id_seq;
CREATE SEQUENCE IF NOT EXISTS meal_entries_id_seq;
CREATE SEQUENCE IF NOT EXISTS daily_summary_id_seq;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGINT DEFAULT nextval('users_id_seq') PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    edad INTEGER,
    altura_cm DOUBLE PRECISION,
    peso_kg DOUBLE PRECISION,
    genero VARCHAR(50),
    nivel_actividad VARCHAR(50),
    objetivo VARCHAR(50),
    calorias_meta_diaria INTEGER,
    fecha_creacion TIMESTAMP
);

-- Tabla de alimentos
CREATE TABLE IF NOT EXISTS foods (
    id BIGINT DEFAULT nextval('foods_id_seq') PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    marca VARCHAR(255),
    categoria VARCHAR(255),
    calorias_100g DOUBLE PRECISION,
    proteinas DOUBLE PRECISION,
    carbohidratos DOUBLE PRECISION,
    grasas DOUBLE PRECISION,
    codigo_barra VARCHAR(255),
    fuente_datos VARCHAR(255)
);

-- Tabla de registros de comidas
CREATE TABLE IF NOT EXISTS meal_entries (
    id BIGINT DEFAULT nextval('meal_entries_id_seq') PRIMARY KEY,
    user_id BIGINT NOT NULL,
    food_id BIGINT,
    fecha DATE NOT NULL,
    comida_tipo VARCHAR(50),
    cantidad_g DOUBLE PRECISION,
    calorias DOUBLE PRECISION,
    notas VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (food_id) REFERENCES foods(id)
);

-- Tabla de resumen diario
CREATE TABLE IF NOT EXISTS daily_summary (
    id BIGINT DEFAULT nextval('daily_summary_id_seq') PRIMARY KEY,
    user_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    calorias_totales DOUBLE PRECISION,
    calorias_objetivo DOUBLE PRECISION,
    FOREIGN KEY (user_id) REFERENCES users(id)
); 