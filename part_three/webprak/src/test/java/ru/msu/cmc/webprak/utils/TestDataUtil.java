package ru.msu.cmc.webprak.utils;

import ru.msu.cmc.webprak.models.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestDataUtil {

    // Пользователи
    public static Users createTestUser() {
        Users user = new Users();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash("testhash");
        user.setPhone("+79001234567");
        user.setAddress("Test Address");
        user.setRole(Users.Role.client);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static List<Users> createTestUsers() {
        List<Users> users = new ArrayList<>();

        Users client = new Users();
        client.setFullName("John Doe");
        client.setEmail("john@example.com");
        client.setPasswordHash("password123");
        client.setPhone("+79001234567");
        client.setAddress("123 client St");
        client.setRole(Users.Role.client);
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        users.add(client);

        Users admin = new Users();
        admin.setFullName("admin User");
        admin.setEmail("admin@example.com");
        admin.setPasswordHash("admin123");
        admin.setPhone("+79009876543");
        admin.setAddress("456 admin St");
        admin.setRole(Users.Role.admin);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        users.add(admin);

        return users;
    }

    // Автомобили
    public static Cars createTestCar() {
        Cars car = new Cars();
        car.setBrand("Test Brand");
        car.setManufacturer("Test Manufacturer");
        car.setRegistrationNumber("TEST123");
        car.setPrice(new BigDecimal("15000.00"));
        car.setStatus(Cars.Status.available);
        car.setCreatedAt(LocalDateTime.now());
        return car;
    }

    public static List<Cars> createTestCars() {
        List<Cars> cars = new ArrayList<>();

        Cars car1 = new Cars();
        car1.setBrand("Toyota");
        car1.setManufacturer("Toyota Motor Corporation");
        car1.setRegistrationNumber("TO123YO");
        car1.setPrice(new BigDecimal("25000.00"));
        car1.setStatus(Cars.Status.available);
        car1.setCreatedAt(LocalDateTime.now());
        cars.add(car1);

        Cars car2 = new Cars();
        car2.setBrand("Honda");
        car2.setManufacturer("Honda Motor Co.");
        car2.setRegistrationNumber("HO456ND");
        car2.setPrice(new BigDecimal("20000.00"));
        car2.setStatus(Cars.Status.available);
        car2.setCreatedAt(LocalDateTime.now());
        cars.add(car2);

        return cars;
    }

    // Технические характеристики
    public static TechnicalSpecs createTestTechnicalSpecs(Cars car) {
        TechnicalSpecs specs = new TechnicalSpecs();
        specs.setCar(car);
        specs.setEngineVolume(new BigDecimal("2.0"));
        specs.setPower(150);
        specs.setFuelConsumption(new BigDecimal("7.5"));
        specs.setDoors(4);
        specs.setSeats(5);
        specs.setAutomaticTransmission(true);
        specs.setFuelType(TechnicalSpecs.FuelType.petrol);
        specs.setCruiseControl(true);
        return specs;
    }

    // Потребительские характеристики
    public static ConsumerSpecs createTestConsumerSpecs(Cars car) {
        ConsumerSpecs specs = new ConsumerSpecs();
        specs.setCar(car);
        specs.setInteriorMaterial("leather");
        specs.setColor("black");
        specs.setHasAirConditioning(true);
        specs.setHasMultimedia(true);
        specs.setHasGps(true);
        return specs;
    }

    // Динамические характеристики
    public static DynamicSpecs createTestDynamicSpecs(Cars car) {
        DynamicSpecs specs = new DynamicSpecs();
        specs.setCar(car);
        specs.setMileage(10000);
        specs.setLastService(LocalDate.now().minusMonths(3));
        specs.setTestDriveCount(5);
        return specs;
    }

    // Акции
    public static Promotions createTestPromotion() {
        Promotions promotion = new Promotions();
        promotion.setTitle("Test Promotion");
        promotion.setDescription("Test Description");
        promotion.setStartDate(LocalDate.now());
        promotion.setEndDate(LocalDate.now().plusMonths(1));
        promotion.setDiscount(new BigDecimal("10.00"));
        promotion.setIsActive(true);
        promotion.setCreatedAt(LocalDateTime.now());
        return promotion;
    }

    // Заказы
    public static Orders createTestOrder(Users user, Cars car) {
        Orders order = new Orders();
        order.setUser(user);
        order.setCar(car);
        order.setOrderDate(LocalDateTime.now());
        order.setTestDriveRequired(false);
        order.setStatus(Orders.Status.processing);
        return order;
    }

    // Тест-драйвы
    public static TestDrives createTestTestDrive(Users user, Cars car) {
        TestDrives testDrive = new TestDrives();
        testDrive.setUser(user);
        testDrive.setCar(car);
        testDrive.setScheduledTime(LocalDateTime.now().plusDays(1));
        testDrive.setStatus(TestDrives.Status.pending);
        testDrive.setNotes("Test Notes");
        return testDrive;
    }

    // Заявки на выкуп
    public static Buybacks createTestBuyback(Users user) {
        Buybacks buyback = new Buybacks();
        buyback.setUser(user);
        buyback.setCarBrand("Test Brand");
        buyback.setCarYear(2018);
        buyback.setMileage(50000);
        buyback.setPhotos("{\"photos\":[\"photo1.jpg\",\"photo2.jpg\"]}");
        buyback.setStatus(Buybacks.Status.pending);
        buyback.setCreatedAt(LocalDateTime.now());
        return buyback;
    }
    public static Cars createTestCarWithSpecs() {
        Cars car = createTestCar();

        TechnicalSpecs techSpecs = new TechnicalSpecs();
        techSpecs.setCar(car);
        techSpecs.setEngineVolume(new BigDecimal("10.00"));
        techSpecs.setFuelType(TechnicalSpecs.FuelType.petrol);
        techSpecs.setPower(150);
        techSpecs.setAutomaticTransmission(true);
        car.setTechnicalSpecs(techSpecs);

        ConsumerSpecs consumerSpecs = new ConsumerSpecs();
        consumerSpecs.setCar(car);
        consumerSpecs.setColor("Black");
        consumerSpecs.setHasAirConditioning(true);
        consumerSpecs.setHasMultimedia(true);
        consumerSpecs.setHasGps(false);
        car.setConsumerSpecs(consumerSpecs);

        DynamicSpecs dynamicSpecs = new DynamicSpecs();
        dynamicSpecs.setLastService(LocalDate.now());
        dynamicSpecs.setCar(car);
        dynamicSpecs.setMileage(10);
        car.setDynamicSpecs(dynamicSpecs);

        return car;
    }

    public static Cars createTestCarWithFullEquipment() {
        Cars car = createTestCar();

        ConsumerSpecs consumerSpecs = new ConsumerSpecs();
        consumerSpecs.setCar(car);
        consumerSpecs.setColor("Silver");
        consumerSpecs.setHasAirConditioning(true);
        consumerSpecs.setHasMultimedia(true);
        consumerSpecs.setHasGps(true);
        car.setConsumerSpecs(consumerSpecs);

        return car;
    }

    public static Cars createTestCarWithBasicEquipment() {
        Cars car = new Cars();
        car.setBrand("Test Brand");
        car.setManufacturer("Test Manufacturer");
        car.setRegistrationNumber("TEST321");
        car.setPrice(new BigDecimal("15000.00"));
        car.setStatus(Cars.Status.available);
        car.setCreatedAt(LocalDateTime.now());

        ConsumerSpecs consumerSpecs = new ConsumerSpecs();
        consumerSpecs.setCar(car);
        consumerSpecs.setColor("White");
        consumerSpecs.setHasAirConditioning(true);
        consumerSpecs.setHasMultimedia(false);
        consumerSpecs.setHasGps(false);
        car.setConsumerSpecs(consumerSpecs);

        return car;
    }

    public static Cars createLuxuryCar() {
        Cars car = new Cars();
        car.setBrand("BMW");
        car.setRegistrationNumber("BMW123");
        car.setCreatedAt(LocalDateTime.now());
        car.setManufacturer("BMW AG");
        car.setPrice(new BigDecimal("85000.00"));
        car.setStatus(Cars.Status.available);

        TechnicalSpecs techSpecs = new TechnicalSpecs();
        techSpecs.setCar(car);
        techSpecs.setEngineVolume(new BigDecimal("80.00"));
        techSpecs.setFuelType(TechnicalSpecs.FuelType.petrol);
        techSpecs.setPower(320);
        techSpecs.setAutomaticTransmission(true);
        car.setTechnicalSpecs(techSpecs);

        ConsumerSpecs consumerSpecs = new ConsumerSpecs();
        consumerSpecs.setCar(car);
        consumerSpecs.setColor("Black");
        consumerSpecs.setHasAirConditioning(true);
        consumerSpecs.setHasMultimedia(true);
        consumerSpecs.setHasGps(true);
        car.setConsumerSpecs(consumerSpecs);

        return car;
    }

    public static Cars createEconomyCar() {
        Cars car = new Cars();
        car.setRegistrationNumber("Toyota123");
        car.setCreatedAt(LocalDateTime.now());
        car.setBrand("Toyota");
        car.setManufacturer("Toyota Motor Corporation");
        car.setPrice(new BigDecimal("22000.00"));
        car.setStatus(Cars.Status.available);

        TechnicalSpecs techSpecs = new TechnicalSpecs();
        techSpecs.setCar(car);
        techSpecs.setEngineVolume(new BigDecimal("80.00"));
        techSpecs.setFuelType(TechnicalSpecs.FuelType.petrol);
        techSpecs.setPower(122);
        techSpecs.setAutomaticTransmission(true);
        car.setTechnicalSpecs(techSpecs);

        ConsumerSpecs consumerSpecs = new ConsumerSpecs();
        consumerSpecs.setCar(car);
        consumerSpecs.setColor("White");
        consumerSpecs.setHasAirConditioning(false);
        consumerSpecs.setHasMultimedia(true);
        consumerSpecs.setHasGps(false);
        car.setConsumerSpecs(consumerSpecs);

        return car;
    }
}

