-- Esquema de base de datos para tests

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    edad INTEGER,
    peso_kg DOUBLE PRECISION,
    altura_cm DOUBLE PRECISION,
    genero VARCHAR(50),
    nivel_actividad VARCHAR(50),
    objetivo VARCHAR(50),
    calorias_meta_diaria INTEGER,
    fecha_creacion TIMESTAMP NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tabla de alimentos
CREATE TABLE IF NOT EXISTS foods (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    calories INTEGER NOT NULL,
    proteins DOUBLE PRECISION NOT NULL,
    carbs DOUBLE PRECISION NOT NULL,
    fats DOUBLE PRECISION NOT NULL,
    portion_size DOUBLE PRECISION NOT NULL,
    portion_unit VARCHAR(50) NOT NULL,
    category VARCHAR(100)
);

-- Tabla de recetas
CREATE TABLE IF NOT EXISTS recipes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    ingredients TEXT NOT NULL,
    instructions TEXT NOT NULL,
    calories INTEGER NOT NULL,
    proteins DOUBLE PRECISION NOT NULL,
    carbs DOUBLE PRECISION NOT NULL,
    fats DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de entradas de comidas
CREATE TABLE IF NOT EXISTS meal_entries (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    food_id BIGINT,
    fecha DATE NOT NULL,
    comida_tipo VARCHAR(50),
    cantidad_g DOUBLE PRECISION,
    calorias DOUBLE PRECISION,
    notas TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (food_id) REFERENCES foods(id)
);

-- Tabla de resúmenes diarios
CREATE TABLE IF NOT EXISTS daily_summaries (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    calorias_consumidas INTEGER DEFAULT 0,
    proteinas_consumidas DOUBLE PRECISION DEFAULT 0,
    carbohidratos_consumidos DOUBLE PRECISION DEFAULT 0,
    grasas_consumidas DOUBLE PRECISION DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE(user_id, fecha)
); 