CREATE TABLE recipes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    ingredients TEXT NOT NULL,
    instructions TEXT NOT NULL,
    calories INT NOT NULL,
    proteins DECIMAL(5,2) NOT NULL,
    carbs DECIMAL(5,2) NOT NULL,
    fats DECIMAL(5,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
); 