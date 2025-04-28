DROP TABLE IF EXISTS
    CarsPromotions,
    Orders,
    TestDrives,
    Buybacks,
    Promotions,
    DynamicSpecs,
    ConsumerSpecs,
    TechnicalSpecs,
    Cars,
    Users
    CASCADE;

-- Создание таблицы Users
CREATE TABLE Users (
                       user_id SERIAL PRIMARY KEY,
                       role VARCHAR(10) DEFAULT 'client' CHECK (role IN ('client', 'admin')),
                       full_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       address TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Создание таблицы Cars
CREATE TABLE Cars (
                      car_id SERIAL PRIMARY KEY,
                      brand VARCHAR(255) NOT NULL,
                      manufacturer VARCHAR(255),
                      registration_number VARCHAR(50) UNIQUE NOT NULL,
                      price DECIMAL(10, 2) NOT NULL,
                      status VARCHAR(10) DEFAULT 'available' CHECK (status IN ('available', 'reserved', 'sold')),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы TechnicalSpecs
CREATE TABLE TechnicalSpecs (
                                car_id INT PRIMARY KEY REFERENCES Cars(car_id) ON DELETE CASCADE,
                                engine_volume DECIMAL(4,2) NOT NULL,
                                power INT NOT NULL,
                                fuel_consumption DECIMAL(5,2),
                                doors INT DEFAULT 4,
                                seats INT DEFAULT 5,
                                automatic_transmission BOOLEAN DEFAULT FALSE,
                                fuel_type VARCHAR(10) NOT NULL CHECK (fuel_type IN ('petrol', 'diesel', 'electric')),
                                cruise_control BOOLEAN DEFAULT FALSE
);

-- Создание таблицы ConsumerSpecs
CREATE TABLE ConsumerSpecs (
                               car_id INT PRIMARY KEY REFERENCES Cars(car_id) ON DELETE CASCADE,
                               interior_material VARCHAR(255) DEFAULT 'fabric',
                               color VARCHAR(100) NOT NULL,
                               has_air_conditioning BOOLEAN DEFAULT FALSE,
                               has_multimedia BOOLEAN DEFAULT FALSE,
                               has_gps BOOLEAN DEFAULT FALSE
);

-- Создание таблицы DynamicSpecs
CREATE TABLE DynamicSpecs (
                              car_id INT PRIMARY KEY REFERENCES Cars(car_id) ON DELETE CASCADE,
                              mileage INT DEFAULT 0,
                              last_service DATE NOT NULL,
                              test_drive_count INT DEFAULT 0
);

-- Создание таблицы Orders
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

-- Создание таблицы Promotions
CREATE TABLE Promotions (
                            promotion_id SERIAL PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            description TEXT,
                            start_date DATE NOT NULL,
                            end_date DATE NOT NULL,
                            discount DECIMAL(5,2),
                            is_active BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы CarsPromotions (связь M:N между Cars и Promotions)
CREATE TABLE CarsPromotions (
                                car_id INT NOT NULL REFERENCES Cars(car_id) ON DELETE CASCADE,
                                promotion_id INT NOT NULL REFERENCES Promotions(promotion_id) ON DELETE CASCADE,
                                PRIMARY KEY (car_id, promotion_id)
);

-- Создание таблицы Test_Drives
CREATE TABLE TestDrives (
                             test_drive_id SERIAL PRIMARY KEY,
                             user_id INT NOT NULL REFERENCES Users(user_id),
                             car_id INT NOT NULL REFERENCES Cars(car_id),
                             scheduled_time TIMESTAMP NOT NULL,
                             status VARCHAR(10) DEFAULT 'pending' CHECK (status IN ('pending', 'completed', 'cancelled')),
                             notes TEXT
);

-- Создание таблицы Buybacks
CREATE TABLE Buybacks (
                          buyback_id SERIAL PRIMARY KEY,
                          user_id INT NOT NULL REFERENCES Users(user_id),
                          car_brand VARCHAR(255) NOT NULL,
                          car_year INT NOT NULL,
                          mileage INT NOT NULL,
                          photos JSONB,
                          status VARCHAR(10) DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'rejected')),
                          estimated_price DECIMAL(10,2),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
