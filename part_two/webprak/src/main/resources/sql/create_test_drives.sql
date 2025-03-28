DROP TABLE IF EXISTS TestDrives;
CREATE TABLE TestDrives (
                             test_drive_id SERIAL PRIMARY KEY,
                             user_id INT NOT NULL REFERENCES Users(user_id),
                             car_id INT NOT NULL REFERENCES Cars(car_id),
                             scheduled_time TIMESTAMP NOT NULL,
                             status VARCHAR(10) DEFAULT 'pending' CHECK (status IN ('pending', 'completed', 'cancelled')),
                             notes TEXT
);
