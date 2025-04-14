CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    edad INT,
    peso_kg DECIMAL(5,2),
    altura_cm DECIMAL(5,2),
    genero VARCHAR(20),
    nivel_actividad VARCHAR(50),
    objetivo VARCHAR(50),
    calorias_meta_diaria INT,
    fecha_creacion TIMESTAMP
); 