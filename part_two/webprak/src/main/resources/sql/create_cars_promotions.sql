DROP TABLE IF EXISTS CarsPromotions;
CREATE TABLE CarsPromotions (
                                car_id INT NOT NULL REFERENCES Cars(car_id),
                                promotion_id INT NOT NULL REFERENCES Promotions(promotion_id),
                                PRIMARY KEY (car_id, promotion_id)
);
