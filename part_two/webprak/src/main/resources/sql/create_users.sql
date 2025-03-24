DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
                       user_id SERIAL PRIMARY KEY,
                       role VARCHAR(10) NOT NULL CHECK (role IN ('client', 'admin')),
                       full_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       address TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);