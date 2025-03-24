DROP TABLE IF EXISTS ConsumerSpecs;
CREATE TABLE ConsumerSpecs (
                               car_id INT PRIMARY KEY REFERENCES Cars(car_id) ON DELETE CASCADE,
                               interior_material VARCHAR(255) DEFAULT 'fabric',
                               color VARCHAR(100) NOT NULL,
                               has_air_conditioning BOOLEAN DEFAULT FALSE,
                               has_multimedia BOOLEAN DEFAULT FALSE,
                               has_gps BOOLEAN DEFAULT FALSE
);
