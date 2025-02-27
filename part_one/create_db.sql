CREATE TABLE Users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    role ENUM('client', 'admin') NOT NULL DEFAULT 'client',
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE Cars (
    car_id INT PRIMARY KEY AUTO_INCREMENT,
    brand VARCHAR(255) NOT NULL,
    manufacturer VARCHAR(255),
    registration_number VARCHAR(50) UNIQUE NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    status ENUM('available', 'reserved', 'sold') DEFAULT 'available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TechnicalSpecs (
    car_id INT PRIMARY KEY,
    engine_volume DECIMAL(4, 2) NOT NULL,
    power INT NOT NULL,
    fuel_consumption DECIMAL(5, 2),
    doors INT DEFAULT 4,
    seats INT DEFAULT 5,
    automatic_transmission BOOLEAN DEFAULT FALSE,
    fuel_type ENUM('petrol', 'diesel', 'electric') NOT NULL,
    cruise_control BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (car_id) REFERENCES Cars(car_id) ON DELETE CASCADE
);

CREATE TABLE ConsumerSpecs (
    car_id INT PRIMARY KEY,
    interior_material VARCHAR(255) DEFAULT 'fabric',
    color VARCHAR(100) NOT NULL,
    has_air_conditioning BOOLEAN DEFAULT FALSE,
    has_multimedia BOOLEAN DEFAULT FALSE,
    has_gps BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (car_id) REFERENCES Cars(car_id) ON DELETE CASCADE
);

CREATE TABLE DynamicSpecs (
    car_id INT PRIMARY KEY,
    mileage INT DEFAULT 0,
    last_service DATE NOT NULL,
    test_drive_count INT DEFAULT 0,
    FOREIGN KEY (car_id) REFERENCES Cars(car_id) ON DELETE CASCADE
);

CREATE TABLE Orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    car_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    test_drive_required BOOLEAN DEFAULT FALSE,
    status ENUM(
        'processing', 
        'awaiting_delivery', 
        'in_stock', 
        'test_drive', 
        'completed', 
        'cancelled'
    ) DEFAULT 'processing',
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (car_id) REFERENCES Cars(car_id)
);

CREATE TABLE Promotions (
    promotion_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    discount DECIMAL(5, 2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE CarsPromotions (
    car_id INT,
    promotion_id INT,
    PRIMARY KEY (car_id, promotion_id),
    FOREIGN KEY (car_id) REFERENCES Cars(car_id),
    FOREIGN KEY (promotion_id) REFERENCES Promotions(promotion_id)
);

CREATE TABLE Test_Drives (
    test_drive_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    car_id INT NOT NULL,
    scheduled_time DATETIME NOT NULL,
    status ENUM('pending', 'completed', 'cancelled') DEFAULT 'pending',
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (car_id) REFERENCES Cars(car_id)
);

CREATE TABLE Buybacks (
    buyback_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    car_brand VARCHAR(255) NOT NULL,
    car_year INT NOT NULL,
    mileage INT NOT NULL,
    photos JSON,
    status ENUM('pending', 'accepted', 'rejected') DEFAULT 'pending',
    estimated_price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);