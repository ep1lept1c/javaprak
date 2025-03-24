DROP TABLE IF EXISTS Orders;
CREATE TABLE Orders (
                        order_id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL REFERENCES Users(user_id),
                        car_id INT NOT NULL REFERENCES Cars(car_id),
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        test_drive_required BOOLEAN DEFAULT FALSE,
                        status VARCHAR(20) DEFAULT 'processing' CHECK (
                            status IN ('processing', 'awaiting_delivery', 'in_stock', 'test_drive', 'completed', 'cancelled')
                            )
);
