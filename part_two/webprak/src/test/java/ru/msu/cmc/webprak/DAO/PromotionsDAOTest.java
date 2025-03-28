package ru.msu.cmc.webprak.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.Promotions;
import ru.msu.cmc.webprak.utils.TestDataUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class PromotionsDAOTest {

    @Autowired
    private PromotionsDAO promotionsDAO;

    @Autowired
    private CarsDAO carsDAO;

    private Promotions testPromotion;
    private Cars testCar;

    @BeforeEach
    public void setup() {
        // Очищаем таблицы перед каждым тестом

        promotionsDAO.getAll().forEach(promotion -> promotionsDAO.delete(promotion));
        carsDAO.getAll().forEach(car -> carsDAO.delete(car));

        // Создаем тестовую акцию
        testPromotion = TestDataUtil.createTestPromotion();
        promotionsDAO.save(testPromotion);

        // Создаем тестовый автомобиль
        testCar = TestDataUtil.createTestCar();
        carsDAO.save(testCar);
    }

    @Test
    public void testGetAll() {
        Collection<Promotions> promotions = promotionsDAO.getAll();
        assertEquals(1, promotions.size(), "Should have 1 promotion");
    }

    @Test
    public void testSaveAndGetById() {
        // Проверяем, что запись создана и ID присвоен
        assertNotNull(testPromotion.getId(), "Promotion ID should be assigned");

        // Получаем запись по ID
        Promotions retrievedPromotion = promotionsDAO.getById(testPromotion.getId());
        assertNotNull(retrievedPromotion, "Should retrieve promotion by ID");
        assertEquals(testPromotion.getTitle(), retrievedPromotion.getTitle(), "Title should match");
    }

    @Test
    public void testUpdate() {
        // Изменяем скидку
        BigDecimal newDiscount = new BigDecimal("20.00");
        testPromotion.setDiscount(newDiscount);
        promotionsDAO.update(testPromotion);

        // Проверяем, что изменения сохранились
        Promotions updatedPromotion = promotionsDAO.getById(testPromotion.getId());
        assertEquals(0, newDiscount.compareTo(updatedPromotion.getDiscount()), "Discount should be updated");
    }

    @Test
    public void testDelete() {
        // Проверяем, что акция создана
        assertNotNull(promotionsDAO.getById(testPromotion.getId()), "Promotion should exist");

        // Удаляем акцию
        promotionsDAO.delete(testPromotion);

        // Проверяем, что акция удалена
        assertNull(promotionsDAO.getById(testPromotion.getId()), "Promotion should be deleted");
    }

    @Test
    public void testFindByIsActiveTrue() {
        Collection<Promotions> promotions = promotionsDAO.findByIsActiveTrue();

        assertEquals(1, promotions.size(), "Should find 1 active promotion");

        // Деактивируем акцию
        testPromotion.setIsActive(false);
        promotionsDAO.update(testPromotion);

        // Проверяем, что нет активных акций
        promotions = promotionsDAO.findByIsActiveTrue();
        assertTrue(promotions.isEmpty(), "Should not find active promotions");
    }

    @Test
    public void testFindActiveOnDate() {
        LocalDate today = LocalDate.now();
        Collection<Promotions> promotions = promotionsDAO.findActiveOnDate(today);

        assertEquals(1, promotions.size(), "Should find 1 promotion active today");

        // Проверяем поиск на дату за пределами срока действия акции
        promotions = promotionsDAO.findActiveOnDate(today.plusYears(1));
        assertTrue(promotions.isEmpty(), "Should not find promotions active in 1 year");
    }

    @Test
    public void testFindByDiscountGreaterThanEqual() {
        BigDecimal minDiscount = new BigDecimal("5.00");
        Collection<Promotions> promotions = promotionsDAO.findByDiscountGreaterThanEqual(minDiscount);

        assertEquals(1, promotions.size(), "Should find 1 promotion with discount >= 500");

        // Проверяем поиск с большим порогом скидки
        promotions = promotionsDAO.findByDiscountGreaterThanEqual(new BigDecimal("2000.00"));
        assertTrue(promotions.isEmpty(), "Should not find promotions with discount >= 2000");
    }

    @Test
    public void testFindByTitleContaining() {
        Collection<Promotions> promotions = promotionsDAO.findByTitleContaining("Test");

        assertEquals(1, promotions.size(), "Should find 1 promotion containing 'Test' in title");

        // Проверяем поиск по другому тексту
        promotions = promotionsDAO.findByTitleContaining("NonExistent");
        assertTrue(promotions.isEmpty(), "Should not find promotions containing 'NonExistent' in title");
    }

    @Test
    public void testAddAndRemoveCarToPromotion() {
        // Добавляем автомобиль к акции
        promotionsDAO.addCarToPromotion(testPromotion.getId(), testCar.getId());

        // Проверяем, что автомобиль связан с акцией
        Collection<Promotions> promotions = promotionsDAO.findByCar(testCar);
        assertEquals(1, promotions.size(), "Should find 1 promotion for car");

        // Проверяем, что это та же акция
        Promotions promotion = promotions.iterator().next();
        assertEquals(testPromotion.getId(), promotion.getId(), "Promotion ID should match");

        // Удаляем автомобиль из акции
        promotionsDAO.removeCarFromPromotion(testPromotion.getId(), testCar.getId());

        // Проверяем, что связь удалена
        promotions = promotionsDAO.findByCar(testCar);
        assertTrue(promotions.isEmpty(), "Should not find promotions for car");
    }

    @Test
    public void testFindUpcomingPromotions() {
        // Создаем предстоящую акцию
        Promotions upcomingPromotion = new Promotions();
        upcomingPromotion.setTitle("Upcoming Promotion");
        upcomingPromotion.setStartDate(LocalDate.now().plusDays(7));
        upcomingPromotion.setEndDate(LocalDate.now().plusDays(30));
        upcomingPromotion.setDiscount(new BigDecimal("15.00"));
        upcomingPromotion.setIsActive(true);
        promotionsDAO.save(upcomingPromotion);

        Collection<Promotions> promotions = promotionsDAO.findUpcomingPromotions(LocalDate.now());

        assertEquals(1, promotions.size(), "Should find 1 upcoming promotion");

        Promotions promotion = promotions.iterator().next();
        assertEquals("Upcoming Promotion", promotion.getTitle(), "Title should match");
    }

    @Test
    public void testFindExpiredActivePromotions() {
        // Создаем истекшую, но активную акцию
        Promotions expiredPromotion = new Promotions();
        expiredPromotion.setTitle("Expired Promotion");
        expiredPromotion.setStartDate(LocalDate.now().minusDays(30));
        expiredPromotion.setEndDate(LocalDate.now().minusDays(1));
        expiredPromotion.setDiscount(new BigDecimal("15.00"));
        expiredPromotion.setIsActive(true);
        promotionsDAO.save(expiredPromotion);

        Collection<Promotions> promotions = promotionsDAO.findExpiredActivePromotions(LocalDate.now());

        assertEquals(1, promotions.size(), "Should find 1 expired active promotion");

        Promotions promotion = promotions.iterator().next();
        assertEquals("Expired Promotion", promotion.getTitle(), "Title should match");
    }

    @Test
    public void testDeactivateExpiredPromotions() {
        // Создаем истекшую, но активную акцию
        Promotions expiredPromotion = new Promotions();
        expiredPromotion.setTitle("Expired Promotion");
        expiredPromotion.setStartDate(LocalDate.now().minusDays(30));
        expiredPromotion.setEndDate(LocalDate.now().minusDays(1));
        expiredPromotion.setDiscount(new BigDecimal("15.00"));
        expiredPromotion.setIsActive(true);
        promotionsDAO.save(expiredPromotion);

        int count = promotionsDAO.deactivateExpiredPromotions(LocalDate.now());

        assertEquals(1, count, "Should deactivate 1 promotion");

        // Проверяем, что акция деактивирована
        Promotions updatedPromotion = promotionsDAO.getById(expiredPromotion.getId());
        assertFalse(updatedPromotion.getIsActive(), "Promotion should be deactivated");
    }
}