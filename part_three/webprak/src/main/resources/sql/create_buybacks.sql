DROP TABLE IF EXISTS Buybacks;
CREATE TABLE Buybacks (
                          buyback_id SERIAL PRIMARY KEY,
                          user_id INT NOT NULL REFERENCES Users(user_id),
                          car_brand VARCHAR(255) NOT NULL,
                          car_year INT NOT NULL,
                          mileage INT NOT NULL,
                          photos JSONB,
                          status VARCHAR(10) DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'rejected')),
                          estimated_price DECIMAL(10, 2),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
