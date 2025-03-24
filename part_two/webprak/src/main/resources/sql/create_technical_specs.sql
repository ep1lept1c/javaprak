DROP TABLE IF EXISTS TechnicalSpecs;
CREATE TABLE TechnicalSpecs (
                                car_id INT PRIMARY KEY REFERENCES Cars(car_id) ON DELETE CASCADE,
                                engine_volume DECIMAL(4, 2) NOT NULL,
                                power INT NOT NULL,
                                fuel_consumption DECIMAL(5, 2),
                                doors INT DEFAULT 4,
                                seats INT DEFAULT 5,
                                automatic_transmission BOOLEAN DEFAULT FALSE,
                                fuel_type VARCHAR(10) NOT NULL CHECK (fuel_type IN ('petrol', 'diesel', 'electric')),
                                cruise_control BOOLEAN DEFAULT FALSE
);
