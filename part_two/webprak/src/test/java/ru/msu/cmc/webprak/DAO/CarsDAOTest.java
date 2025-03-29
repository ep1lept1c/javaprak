package ru.msu.cmc.webprak.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.*;
import ru.msu.cmc.webprak.utils.TestDataUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class CarsDAOTest {

    @Autowired
    private CarsDAO carsDAO;

    @Autowired
    private PromotionsDAO promotionsDAO;

    @Autowired
    private TechnicalSpecsDAO technicalSpecsDAO;

    @Autowired
    private ConsumerSpecsDAO consumerSpecsDAO;

    @Autowired
    private DynamicSpecsDAO dynamicSpecsDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Cars testCar1;
    private Cars testCar2;
    private Promotions testPromotion;

    @BeforeEach
    public void setup() {
        clearDatabase();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создаем тестовые автомобили с полной спецификацией
            testCar1 = TestDataUtil.createTestCarWithSpecs();
            testCar1.setRegistrationNumber("TEST001");
            session.persist(testCar1);

            testCar2 = TestDataUtil.createTestCarWithSpecs();
            testCar2.setRegistrationNumber("TEST002");
            testCar2.setBrand("BMW");
            testCar2.setStatus(Cars.Status.RESERVED);
            session.persist(testCar2);

            // Создаем тестовую акцию
            testPromotion = TestDataUtil.createTestPromotion();
            session.persist(testPromotion);

            session.getTransaction().commit();
        }
    }

    @BeforeAll
    @AfterEach
    public void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("DELETE FROM technicalspecs", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM consumerspecs", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM dynamicspecs", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM carspromotions", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM promotions", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM cars", Void.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    // Тест на получение всех автомобилей
    @Test
    public void testGetAll() {
        Collection<Cars> cars = carsDAO.getAll();
        assertEquals(2, cars.size());
    }

    // Тест на сохранение и получение по ID
    @Test
    public void testSaveAndGetById() {
        Cars newCar = TestDataUtil.createTestCar();
        carsDAO.save(newCar);

        assertNotNull(newCar.getId());
        assertNotNull(carsDAO.getById(newCar.getId()));
    }

    // Тест на обновление
    @Test
    public void testUpdate() {
        testCar1.setPrice(new BigDecimal("35000.00"));
        carsDAO.update(testCar1);

        Cars updated = carsDAO.getById(testCar1.getId());
        assertEquals(0, new BigDecimal("35000.00").compareTo(updated.getPrice()));
    }

    // Тест на удаление
    @Test
    public void testDelete() {
        carsDAO.delete(testCar1);
        assertNull(carsDAO.getById(testCar1.getId()));
    }

    // Тест на поиск по номеру регистрации
    @Test
    public void testFindByRegistrationNumber() {
        Cars found = carsDAO.findByRegistrationNumber("TEST001");
        assertNotNull(found);
        assertEquals(testCar1.getId(), found.getId());

        assertNull(carsDAO.findByRegistrationNumber("NONEXISTENT"));
    }

    // Тест на поиск по марке
    @Test
    public void testFindByBrand() {
        Collection<Cars> test_brand = carsDAO.findByBrand("Test Brand");
        assertEquals(1, test_brand.size());
        assertEquals(testCar1.getId(), test_brand.iterator().next().getId());

        assertTrue(carsDAO.findByBrand("Audi").isEmpty());
    }

    // Тест на поиск по статусу
    @Test
    public void testFindByStatus() {
        Collection<Cars> available = carsDAO.findByStatus(Cars.Status.AVAILABLE);
        assertEquals(1, available.size());

        Collection<Cars> reserved = carsDAO.findByStatus(Cars.Status.RESERVED);
        assertEquals(1, reserved.size());
    }

    // Тест на поиск по производителю
    @Test
    public void testFindByManufacturer() {
        String manufacturer = "Honda Motor Co.";
        testCar1.setManufacturer("Honda Motor Co.");
        testCar1.setBrand("Honda");
        carsDAO.update(testCar1);
        Collection<Cars> cars = carsDAO.findByManufacturer(manufacturer);

        assertEquals(1, cars.size(), "Should find 1 Honda car");

        Cars car = cars.iterator().next();
        assertEquals("Honda", car.getBrand(), "Brand should be Honda");
        assertEquals(manufacturer, car.getManufacturer(), "Manufacturer should match");
    }

    // Тест на поиск по диапазону цен
    @Test
    public void testFindByPriceBetween() {
        Collection<Cars> cars = carsDAO.findByPriceBetween(
                new BigDecimal("10000.00"),
                new BigDecimal("40000.00")
        );
        assertEquals(2, cars.size());

        cars = carsDAO.findByPriceBetween(
                new BigDecimal("50000.00"),
                new BigDecimal("100000.00")
        );
        assertTrue(cars.isEmpty());
    }

    // Тест на добавление и удаление акции
    @Test
    public void testAddAndRemovePromotion() {
        // Добавляем акцию
        Cars updated = carsDAO.addPromotionToCar(testCar1.getId(), testPromotion.getId());
        assertNotNull(updated);
        assertEquals(1, updated.getPromotions().size());

        // Проверяем поиск по акции
        Collection<Cars> carsWithPromo = carsDAO.findByPromotion(testPromotion);
        assertEquals(1, carsWithPromo.size());

        // Удаляем акцию
        updated = carsDAO.removePromotionFromCar(testCar1.getId(), testPromotion.getId());
        assertNotNull(updated);
        assertTrue(updated.getPromotions().isEmpty());
    }

    // Тест на обновление цены
    @Test
    public void testUpdatePrice() {
        BigDecimal newPrice = new BigDecimal("45000.00");
        Cars updated = carsDAO.updatePrice(testCar1.getId(), newPrice);

        assertNotNull(updated);
        assertEquals(0, newPrice.compareTo(updated.getPrice()));
    }

    // Тест на обновление статуса
    @Test
    public void testUpdateStatus() {
        Cars updated = carsDAO.updateStatus(testCar1.getId(), Cars.Status.SOLD);

        assertNotNull(updated);
        assertEquals(Cars.Status.SOLD, updated.getStatus());
    }

    // Тест на получение статистики по маркам
    @Test
    public void testGetCarCountByBrand() {
        Map<String, Long> counts = carsDAO.getCarCountByBrand();

        assertEquals(2, counts.size());
        assertEquals(1L, counts.get("Test Brand"));
        assertEquals(1L, counts.get("BMW"));
    }

    // Тест на получение технических характеристик
    @Test
    public void testGetTechnicalSpecs() {
        TechnicalSpecs specs = carsDAO.getTechnicalSpecs(testCar1.getId());

        assertNotNull(specs);
        assertEquals(testCar1.getTechnicalSpecs().getEngineVolume(), specs.getEngineVolume());

        assertNull(carsDAO.getTechnicalSpecs(999L));
    }

    // Тест на получение потребительских характеристик
    @Test
    public void testGetConsumerSpecs() {
        ConsumerSpecs specs = carsDAO.getConsumerSpecs(testCar1.getId());

        assertNotNull(specs);
        assertEquals(testCar1.getConsumerSpecs().getColor(), specs.getColor());

        assertNull(carsDAO.getConsumerSpecs(999L));
    }

    // Тест на получение динамических характеристик
    @Test
    public void testGetDynamicSpecs() {
        DynamicSpecs specs = carsDAO.getDynamicSpecs(testCar1.getId());

        assertNotNull(specs);
        assertEquals(testCar1.getDynamicSpecs().getMileage(), specs.getMileage());

        assertNull(carsDAO.getDynamicSpecs(999L));
    }

    // Тест на обновление технических характеристик
    @Test
    public void testUpdateTechnicalSpecs() {
        TechnicalSpecs newSpecs = new TechnicalSpecs();
        newSpecs.setFuelType(TechnicalSpecs.FuelType.DIESEL);
        newSpecs.setPower(200);
        newSpecs.setEngineVolume(new BigDecimal("0.1"));
        TechnicalSpecs updated = carsDAO.updateTechnicalSpecs(testCar1.getId(), newSpecs);

        assertNotNull(updated);
        assertEquals(TechnicalSpecs.FuelType.DIESEL, updated.getFuelType());
        assertEquals(200, updated.getPower());
    }

    // Тест на поиск полностью укомплектованных автомобилей
    @Test
    public void testFindFullyEquippedCars() {
        // Делаем второй автомобиль полностью укомплектованным
        testCar2.getConsumerSpecs().setHasAirConditioning(true);
        testCar2.getConsumerSpecs().setHasMultimedia(true);
        testCar2.getConsumerSpecs().setHasGps(true);
        carsDAO.update(testCar2);

        Collection<Cars> fullyEquipped = carsDAO.findFullyEquippedCars();
        assertEquals(1, fullyEquipped.size());
        testCar2.getConsumerSpecs().setHasGps(false);
        carsDAO.update(testCar2);
        Collection<Cars> fullyEquippedNo = carsDAO.findFullyEquippedCars();
        assertEquals(0, fullyEquippedNo.size());
        assertEquals(testCar2.getId(), fullyEquipped.iterator().next().getId());
    }

    // Тест на расширенный поиск
    @Test
    public void testFindWithAdvancedSearch() {
        // Поиск по марке и статусу
        Collection<Cars> results = carsDAO.findWithAdvancedSearch(
                null, null, null, Cars.Status.AVAILABLE, null, null, null
        );

        assertEquals(1, results.size());
        assertEquals(testCar1.getId(), results.iterator().next().getId());

        // Поиск без критериев
        results = carsDAO.findWithAdvancedSearch(null, null, null, null, null, null, null);
        assertEquals(2, results.size());

        // Поиск с несуществующими критериями
        results = carsDAO.findWithAdvancedSearch("Audi", null, null, null, null, null, null);
        assertTrue(results.isEmpty());
    }

    // Тест на обработку ошибок
    @Test
    public void testErrorCases() {
        // Несуществующий ID
        assertNull(carsDAO.updatePrice(999L, new BigDecimal("10000")));
        assertNull(carsDAO.updateStatus(999L, Cars.Status.AVAILABLE));
        assertNull(carsDAO.addPromotionToCar(999L, testPromotion.getId()));
        assertNull(carsDAO.removePromotionFromCar(999L, testPromotion.getId()));

        // Несуществующая акция
        assertNull(carsDAO.addPromotionToCar(testCar1.getId(), 999L));
    }

    // Добавьте эти тесты в ваш класс CarsDAOTest

    // Тест на передачу null-параметров в getTechnicalSpecs, getConsumerSpecs и getDynamicSpecs
    @Test
    public void testGetSpecsWithNullId() {
        // Проверка null ID для каждого метода получения характеристик
        assertNull(carsDAO.getTechnicalSpecs(null));
        assertNull(carsDAO.getConsumerSpecs(null));
        assertNull(carsDAO.getDynamicSpecs(null));
    }

    // Тест на передачу null-параметров в updateTechnicalSpecs
    @Test
    public void testUpdateTechnicalSpecsWithNullParams() {
        // Проверка null carId
        assertNull(carsDAO.updateTechnicalSpecs(null, new TechnicalSpecs()));

        // Проверка null specs
        assertNull(carsDAO.updateTechnicalSpecs(testCar1.getId(), null));

        // Проверка обоих null параметров
        assertNull(carsDAO.updateTechnicalSpecs(null, null));
    }

    // Тест на обновление статуса с null статусом
    @Test
    public void testUpdateStatusWithNullStatus() {
        assertNull(carsDAO.updateStatus(testCar1.getId(), null));
    }

    // Тест на расширенный поиск с невалидным типом топлива
    @Test
    public void testFindWithAdvancedSearchInvalidFuelType() {
        // Поиск с невалидным типом топлива (который вызовет IllegalArgumentException)
        Collection<Cars> results = carsDAO.findWithAdvancedSearch(
                null, null, null, null, null, "INVALID_FUEL_TYPE", null);

        // Проверяем, что метод обработал исключение и вернул пустой список
        assertTrue(results.isEmpty());
    }

    // Тест на расширенный поиск с проверкой всех возможных параметров
    @Test
    public void testFindWithAdvancedSearchAllParameters() {
        // Настраиваем тестовый автомобиль для точного совпадения
        testCar1.setBrand("TestBrand");
        testCar1.setPrice(new BigDecimal("25000.00"));
        testCar1.setStatus(Cars.Status.AVAILABLE);
        testCar1.getConsumerSpecs().setHasAirConditioning(true);
        testCar1.getConsumerSpecs().setColor("Red");
        testCar1.getTechnicalSpecs().setFuelType(TechnicalSpecs.FuelType.PETROL);
        carsDAO.update(testCar1);

        // Поиск со всеми параметрами
        Collection<Cars> results = carsDAO.findWithAdvancedSearch(
                "TestBrand",                   // brand
                new BigDecimal("20000.00"),    // minPrice
                new BigDecimal("30000.00"),    // maxPrice
                Cars.Status.AVAILABLE,         // status
                true,                          // hasAC
                "PETROL",                      // fuelType
                "Red"                          // color
        );

        assertEquals(1, results.size());
        assertEquals(testCar1.getId(), results.iterator().next().getId());
    }

    // Тест на пустой бренд в расширенном поиске
    @Test
    public void testFindWithAdvancedSearchEmptyBrand() {
        // Поиск с пустым брендом (должен быть проигнорирован)
        Collection<Cars> results = carsDAO.findWithAdvancedSearch(
                "",                            // пустой бренд
                null, null, null, null, null, null);

        assertEquals(2, results.size());  // Должны найтись все автомобили
    }

    // Тест на пустой цвет в расширенном поиске
    @Test
    public void testFindWithAdvancedSearchEmptyColor() {
        // Настраиваем цвет тестового автомобиля
        testCar1.getConsumerSpecs().setColor("Blue");
        carsDAO.update(testCar1);

        // Поиск с пустым цветом (должен быть проигнорирован)
        Collection<Cars> results = carsDAO.findWithAdvancedSearch(
                null, null, null, null, null, null, "");

        assertEquals(2, results.size());  // Должны найтись все автомобили
    }

    // Тест на поиск по акции с null значением
    @Test
    public void testFindByPromotionNull() {
        try {
            Collection<Cars> cars = carsDAO.findByPromotion(null);
            // Если метод обрабатывает null без исключения, проверяем результат
            assertTrue(cars.isEmpty());
        } catch (Exception e) {
            // Если метод выбрасывает исключение, это тоже валидное поведение для тестирования
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException);
        }
    }

    // Тест на случай, когда в getCarCountByBrand нет автомобилей
    @Test
    public void testGetCarCountByBrandEmptyDatabase() {
        // Очищаем базу данных
        clearDatabase();

        // Проверяем, что метод корректно обрабатывает отсутствие данных
        Map<String, Long> counts = carsDAO.getCarCountByBrand();
        assertTrue(counts.isEmpty());
    }
}