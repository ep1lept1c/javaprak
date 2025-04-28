-- USERS
INSERT INTO Users (role, full_name, email, password_hash, phone, address)
VALUES
    ('client', 'test', 'test@gmail.com', 'test', '+79663112503', 'ул. Теста, д. 0'),
    ('client', 'Иван Иванов', 'ivan1@example.com', 'hashed1', '+7900000001', 'ул. Ленина, д.1'),
    ('client', 'Мария Смирнова', 'maria2@example.com', 'hashed2', '+7900000002', 'ул. Ленина, д.2'),
    ('client', 'Сергей Кузнецов', 'sergey3@example.com', 'hashed3', '+7900000003', 'ул. Ленина, д.3'),
    ('client', 'Ольга Петрова', 'olga4@example.com', 'hashed4', '+7900000004', 'ул. Ленина, д.4'),
    ('client', 'Дмитрий Сидоров', 'dmitry5@example.com', 'hashed5', '+7900000005', 'ул. Ленина, д.5'),
    ('client', 'Анна Фролова', 'anna6@example.com', 'hashed6', '+7900000006', 'ул. Ленина, д.6'),
    ('client', 'Алексей Николаев', 'alex7@example.com', 'hashed7', '+7900000007', 'ул. Ленина, д.7'),
    ('client', 'Елена Морозова', 'elena8@example.com', 'hashed8', '+7900000008', 'ул. Ленина, д.8'),
    ('client', 'Игорь Павлов', 'igor9@example.com', 'hashed9', '+7900000009', 'ул. Ленина, д.9'),
    ('admin', 'Админ Админов', 'admin@example.com', 'adminhashed', '+7900000010', 'Офис');

-- CARS
INSERT INTO Cars (brand, manufacturer, registration_number, price, status)
VALUES
    ('Audi A6', 'Audi', 'AA001AA77', 4900000, 'available'),
    ('Audi A8', 'Audi', 'AA002AA77', 7300000, 'available'),
    ('Audi Q3', 'Audi', 'AA003AA77', 3800000, 'available'),
    ('Audi Q5', 'Audi', 'AA004AA77', 4700000, 'available'),
    ('Audi RS5', 'Audi', 'AA005AA77', 8200000, 'available'),
    ('BMW 5', 'BMW', 'B001BB77', 5100000, 'available'),
    ('BMW M4', 'BMW', 'BB002BB77', 8700000, 'available'),
    ('BMW M5', 'BMW', 'BB003BB77', 9600000, 'available'),
    ('BMW X1', 'BMW', 'BB005BB77', 3600000, 'available'),
    ('BMW X7', 'BMW', 'BB004BB77', 9800000, 'available'),
    ('Kia EV6', 'Kia', 'KK001KK77', 4100000, 'sold'),
    ('Mercedes AMG', 'Mercedes', 'MM001MM77', 12500000, 'reserved'),
    ('Mercedes C-class', 'Mercedes', 'MM004MM77', 4300000, 'available'),
    ('Mercedes E-class', 'Mercedes', 'MM003MM77', 5800000, 'sold'),
    ('Mercedes G-class', 'Mercedes', 'MM002MM77', 14900000, 'reserved'),
    ('Skoda Kylaq', 'Skoda', 'SS001SS77', 3300000, 'available'),
    ('Skoda Slavia', 'Skoda', 'SS002SS77', 2700000, 'reserved'),
    ('Toyota Camry', 'Toyota', 'TT001TT77', 3600000, 'sold'),
    ('Toyota Land Cruiser', 'Toyota', 'TT004TT77', 8200000, 'reserved'),
    ('Toyota Urban', 'Toyota', 'TT002TT77', 3100000, 'available'),
    ('Toyota Vellfire', 'Toyota', 'TT003TT77', 6700000, 'sold'),
    ('Volkswagen Taigun', 'VW', 'VV001VV77', 2800000, 'available'),
    ('Volkswagen Tiguan', 'VW', 'VV002VV77', 3700000, 'reserved');

-- TECHNICAL SPECS
INSERT INTO TechnicalSpecs (car_id, engine_volume, power, fuel_consumption, doors, seats, automatic_transmission, fuel_type, cruise_control)
VALUES
    (1, 2.5, 181, 8.2, 4, 5, TRUE, 'petrol', TRUE),
    (2, 3.0, 249, 9.5, 5, 5, TRUE, 'diesel', TRUE),
    (3, 1.6, 106, 7.0, 4, 5, FALSE, 'petrol', FALSE),
    (4, 1.4, 100, 6.0, 4, 5, TRUE, 'petrol', FALSE),
    (5, 1.6, 123, 6.8, 4, 5, TRUE, 'petrol', TRUE),
    (6, 2.0, 184, 8.5, 4, 5, TRUE, 'diesel', TRUE),
    (7, 1.6, 110, 6.5, 4, 5, FALSE, 'petrol', FALSE),
    (8, 2.0, 190, 8.0, 4, 5, TRUE, 'diesel', TRUE),
    (9, 1.8, 150, 7.2, 4, 5, TRUE, 'petrol', FALSE),
    (10, 1.6, 105, 6.9, 4, 5, FALSE, 'petrol', FALSE),
    (11, 2.0, 150, 7.5, 4, 5, TRUE, 'petrol', TRUE),
    (12, 3.0, 250, 9.0, 5, 5, TRUE, 'petrol', TRUE),
    (13, 2.0, 190, 8.5, 4, 5, TRUE, 'petrol', TRUE),
    (14, 2.5, 200, 9.5, 4, 5, TRUE, 'petrol', TRUE),
    (15, 4.0, 400, 15.0, 5, 5, TRUE, 'petrol', TRUE),
    (16, 1.4, 110, 6.5, 4, 5, TRUE, 'petrol', FALSE),
    (17, 1.0, 85, 5.5, 4, 5, TRUE, 'petrol', FALSE),
    (18, 2.5, 180, 8.0, 4, 5, TRUE, 'petrol', TRUE),
    (19, 4.5, 280, 14.0, 5, 5, TRUE, 'petrol', TRUE),
    (20, 2.0, 170, 7.8, 4, 5, TRUE, 'petrol', TRUE),
    (21, 2.5, 200, 8.5, 5, 5, TRUE, 'petrol', TRUE),
    (22, 1.4, 150, 6.7, 4, 5, TRUE, 'petrol', TRUE),
    (23, 2.0, 180, 7.8, 5, 5, TRUE, 'petrol', TRUE);

-- CONSUMER SPECS
INSERT INTO ConsumerSpecs (car_id, interior_material, color, has_air_conditioning, has_multimedia, has_gps)
VALUES
    (1, 'leather', 'black', TRUE, TRUE, TRUE),
    (2, 'leather', 'verdant', TRUE, TRUE, TRUE),
    (3, 'fabric', 'orange', TRUE, FALSE, FALSE),
    (4, 'fabric', 'white', TRUE, TRUE, FALSE),
    (5, 'fabric', 'sky blue', TRUE, FALSE, FALSE),
    (6, 'leather', 'dark blue', TRUE, TRUE, TRUE),
    (7, 'fabric', 'emerald', FALSE, FALSE, FALSE),
    (8, 'leather', 'gray', TRUE, TRUE, TRUE),
    (9, 'fabric', 'white', TRUE, TRUE, FALSE),
    (10, 'fabric', 'gray silver', TRUE, FALSE, FALSE),
    (11, 'leather', 'milk silver', TRUE, TRUE, TRUE),
    (12, 'leather', 'black', TRUE, TRUE, TRUE),
    (13, 'fabric', 'gray', TRUE, TRUE, FALSE),
    (14, 'fabric', 'dark blue', TRUE, TRUE, FALSE),
    (15, 'leather', 'dark blue', TRUE, TRUE, TRUE),
    (16, 'fabric', 'yellow', TRUE, FALSE, FALSE),
    (17, 'fabric', 'sky blue', TRUE, FALSE, FALSE),
    (18, 'leather', 'silver', TRUE, TRUE, TRUE),
    (19, 'leather', 'white', TRUE, TRUE, TRUE),
    (20, 'fabric', 'blue', TRUE, FALSE, FALSE),
    (21, 'leather', 'black', TRUE, TRUE, TRUE),
    (22, 'fabric', 'black', TRUE, FALSE, FALSE),
    (23, 'fabric', 'orange', TRUE, FALSE, FALSE);

-- DYNAMIC SPECS
INSERT INTO DynamicSpecs (car_id, mileage, last_service, test_drive_count)
VALUES
    (1, 15000, '2024-10-01', 3),
    (2, 5000, '2024-11-15', 1),
    (3, 30000, '2024-09-10', 5),
    (4, 10000, '2024-08-20', 2),
    (5, 12000, '2024-09-05', 4),
    (6, 20000, '2024-07-22', 6),
    (7, 25000, '2024-10-12', 2),
    (8, 18000, '2024-11-01', 3),
    (9, 8000, '2024-09-30', 1),
    (10, 6000, '2024-10-28', 2),
    (11, 10000, '2024-10-01', 2),
    (12, 15000, '2024-11-15', 3),
    (13, 5000, '2024-09-10', 1),
    (14, 12000, '2024-08-20', 2),
    (15, 3000, '2024-09-05', 1),
    (16, 8000, '2024-07-22', 2),
    (17, 25000, '2024-10-12', 4),
    (18, 18000, '2024-11-01', 3),
    (19, 22000, '2024-09-30', 5),
    (20, 9000, '2024-10-28', 2),
    (21, 7000, '2024-11-11', 1),
    (22, 6000, '2024-12-01', 1),
    (23, 4000, '2024-12-15', 0);

-- PROMOTIONS
INSERT INTO Promotions (title, description, start_date, end_date, discount)
VALUES
    ('Осенняя скидка', 'Скидка на седаны до конца октября', '2024-09-01', '2025-10-31', 10.00),
    ('Новогодняя акция', 'Скидки до 15% на все авто!', '2024-12-01', '2025-01-10', 15.00),
    ('Весенний бонус', 'Скидка 7% на автомобили до 2020 года', '2025-03-01', '2027-04-01', 7.00),
    ('Черная пятница', 'Ограниченная распродажа!', '2024-11-25', '2028-11-30', 20.00),
    ('Летний драйв', 'Скидка 5% на внедорожники', '2024-06-01', '2024-08-31', 5.00),
    ('Скидка на тест-драйв', '5% при записи на тест-драйв', '2024-07-01', '2029-08-15', 5.00),
    ('Утилизация', 'Выкуп старых машин с бонусом', '2024-10-01', '2030-12-01', 8.00),
    ('Первый клиент', 'Скидка новым клиентам', '2024-09-15', '2026-11-15', 5.00),
    ('Возврат налога', 'Компенсация НДС', '2024-08-01', '2027-10-01', 12.00),
    ('Кредит 0%', '0% на год при рассрочке', '2024-09-01', '2024-12-31', 0.00),
    ('Mock', 'Plain', '1990-01-01', '2100-12-31', 0.00);

-- CARS_PROMOTIONS
INSERT INTO CarsPromotions (car_id, promotion_id)
VALUES
    (1, 1), (2, 2), (3, 3), (4, 4), (5, 5),
    (6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
    (11, 11), (12, 11), (13, 11), (14, 11), (15, 11), (16, 11), (17, 11), (18, 11), (19, 11), (20, 11), (22, 11), (23, 11);

-- ORDERS
INSERT INTO Orders (user_id, car_id, test_drive_required, status)
VALUES
    (1, 1, TRUE, 'processing'),
    (2, 2, FALSE, 'completed'),
    (3, 3, TRUE, 'test_drive'),
    (4, 4, FALSE, 'awaiting_delivery'),
    (5, 5, TRUE, 'in_stock'),
    (6, 6, FALSE, 'completed'),
    (7, 7, TRUE, 'cancelled'),
    (8, 8, FALSE, 'processing'),
    (9, 9, TRUE, 'test_drive'),
    (10, 10, FALSE, 'processing');

-- TEST DRIVES
INSERT INTO TestDrives (user_id, car_id, scheduled_time, status, notes)
VALUES
    (1, 1, '2025-04-25 10:00:00', 'pending', 'Утро'),
    (2, 2, '2025-04-26 11:00:00', 'completed', 'Уже тестировал'),
    (3, 3, '2025-04-27 12:00:00', 'pending', ''),
    (4, 4, '2025-04-28 13:00:00', 'cancelled', 'Отменил'),
    (5, 5, '2025-04-29 14:00:00', 'pending', ''),
    (6, 6, '2025-04-30 15:00:00', 'completed', ''),
    (7, 7, '2025-05-01 16:00:00', 'pending', ''),
    (8, 8, '2025-05-02 17:00:00', 'completed', ''),
    (9, 9, '2025-05-03 18:00:00', 'pending', ''),
    (10, 10, '2025-05-04 19:00:00', 'cancelled', '');

-- BUYBACKS
INSERT INTO Buybacks (user_id, car_brand, car_year, mileage, photos, status, estimated_price)
VALUES
    (1, 'Toyota', 2015, 80000, '[]', 'pending', 700000),
    (2, 'BMW', 2018, 50000, '[]', 'accepted', 1500000),
    (3, 'LADA', 2012, 100000, '[]', 'rejected', 200000),
    (4, 'Kia', 2016, 70000, '[]', 'pending', 600000),
    (5, 'Ford', 2017, 60000, '[]', 'pending', 750000),
    (6, 'Audi', 2019, 40000, '[]', 'accepted', 1800000),
    (7, 'Hyundai', 2020, 30000, '[]', 'accepted', 1300000),
    (8, 'Skoda', 2014, 85000, '[]', 'rejected', 400000),
    (9, 'VW', 2013, 90000, '[]', 'pending', 500000),
    (10, 'Nissan', 2015, 80000, '[]', 'pending', 650000);
