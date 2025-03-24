DROP TABLE IF EXISTS DynamicSpecs;
CREATE TABLE DynamicSpecs (
                              car_id INT PRIMARY KEY REFERENCES Cars(car_id) ON DELETE CASCADE,
                              mileage INT DEFAULT 0,
                              last_service DATE NOT NULL,
                              test_drive_count INT DEFAULT 0
);