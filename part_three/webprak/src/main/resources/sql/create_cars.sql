DROP TABLE IF EXISTS Cars;
CREATE TABLE Cars (
                      car_id SERIAL PRIMARY KEY,
                      brand VARCHAR(255) NOT NULL,
                      manufacturer VARCHAR(255),
                      registration_number VARCHAR(50) UNIQUE NOT NULL,
                      price DECIMAL(10, 2) NOT NULL,
                      status VARCHAR(10) DEFAULT 'available' CHECK (status IN ('available', 'reserved', 'sold')),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);