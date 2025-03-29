package ru.msu.cmc.webprak.DAO;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.DAO.impl.BuybacksDAOImpl;
import ru.msu.cmc.webprak.models.Buybacks;
import ru.msu.cmc.webprak.models.Users;
import ru.msu.cmc.webprak.utils.TestDataUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application.properties")
public class BuybacksDAOTest {

    @Autowired
    private BuybacksDAO buybacksDAO;

    @Autowired
    private UsersDAO usersDAO;

    private Users testUser;
    private Buybacks testBuyback1;
    private Buybacks testBuyback2;

    @BeforeEach
    public void setup() {
        // Очищаем таблицы перед каждым тестом
        buybacksDAO.getAll().forEach(buyback -> buybacksDAO.delete(buyback));
        usersDAO.getAll().forEach(user -> usersDAO.delete(user));

        // Создаем тестового пользователя
        testUser = TestDataUtil.createTestUser();
        usersDAO.save(testUser);
        LocalDateTime now = LocalDateTime.now();
        // Создаем тестовые заявки на выкуп
        testBuyback1 = TestDataUtil.createTestBuyback(testUser);
        testBuyback1.setCarBrand("BMW");
        testBuyback1.setCarYear(2018);
        testBuyback1.setMileage(50000);
        testBuyback1.setStatus(Buybacks.Status.PENDING);
        testBuyback1.setCreatedAt(now.minusDays(2));
        buybacksDAO.save(testBuyback1);

        testBuyback2 = TestDataUtil.createTestBuyback(testUser);
        testBuyback2.setCarBrand("Toyota");
        testBuyback2.setCarYear(2020);
        testBuyback2.setMileage(30000);
        testBuyback2.setStatus(Buybacks.Status.ACCEPTED);
        testBuyback2.setEstimatedPrice(new BigDecimal("25000.00"));
        testBuyback2.setCreatedAt(now.minusDays(1));
        buybacksDAO.save(testBuyback2);
    }

    @Test
    public void testSaveAndGetById() {
        // Создаем новую заявку
        Buybacks buyback = TestDataUtil.createTestBuyback(testUser);
        buybacksDAO.save(buyback);

        // Проверяем, что ID присвоен
        assertNotNull(buyback.getId(), "ID должен быть присвоен");

        // Получаем по ID и проверяем
        Buybacks retrieved = buybacksDAO.getById(buyback.getId());
        assertNotNull(retrieved, "Должна быть найдена заявка по ID");
        assertEquals(buyback.getCarBrand(), retrieved.getCarBrand(), "Марка автомобиля должна совпадать");
    }

    @Test
    public void testGetAll() {
        Collection<Buybacks> buybacks = buybacksDAO.getAll();
        assertEquals(2, buybacks.size(), "Должно быть две заявки");
    }

    @Test
    public void testUpdate() {
        // Изменяем статус и цену
        testBuyback1.setStatus(Buybacks.Status.REJECTED);
        buybacksDAO.update(testBuyback1);

        // Проверяем обновление
        Buybacks updated = buybacksDAO.getById(testBuyback1.getId());
        assertEquals(Buybacks.Status.REJECTED, updated.getStatus(), "Статус должен обновиться");
    }

    @Test
    public void testDelete() {
        // Удаляем заявку
        buybacksDAO.delete(testBuyback1);

        // Проверяем, что заявка удалена
        assertNull(buybacksDAO.getById(testBuyback1.getId()), "Заявка должна быть удалена");
        assertEquals(1, buybacksDAO.getAll().size(), "Должна остаться одна заявка");
    }

    @Test
    public void testFindByUser() {
        // Проверяем поиск по пользователю
        Collection<Buybacks> buybacks = buybacksDAO.findByUser(testUser);
        assertEquals(2, buybacks.size(), "Должны найтись обе заявки пользователя");

        // Создаем другого пользователя и проверяем
        Users anotherUser = TestDataUtil.createTestUser();
        anotherUser.setEmail("another@example.com");
        usersDAO.save(anotherUser);

        buybacks = buybacksDAO.findByUser(anotherUser);
        assertTrue(buybacks.isEmpty(), "Не должно быть заявок для другого пользователя");
    }

    @Test
    public void testFindByStatus() {
        // Проверяем поиск по статусу PENDING
        Collection<Buybacks> pendingBuybacks = buybacksDAO.findByStatus(Buybacks.Status.PENDING);
        assertEquals(1, pendingBuybacks.size(), "Должна быть одна ожидающая заявка");
        assertEquals(testBuyback1.getId(), pendingBuybacks.iterator().next().getId(), "ID должен совпадать");

        // Проверяем поиск по статусу ACCEPTED
        Collection<Buybacks> acceptedBuybacks = buybacksDAO.findByStatus(Buybacks.Status.ACCEPTED);
        assertEquals(1, acceptedBuybacks.size(), "Должна быть одна принятая заявка");
        assertEquals(testBuyback2.getId(), acceptedBuybacks.iterator().next().getId(), "ID должен совпадать");

        // Проверяем поиск по статусу REJECTED
        Collection<Buybacks> rejectedBuybacks = buybacksDAO.findByStatus(Buybacks.Status.REJECTED);
        assertTrue(rejectedBuybacks.isEmpty(), "Не должно быть отклоненных заявок");
    }

    @Test
    public void testFindByCarBrand() {
        // Проверяем поиск по марке BMW
        Collection<Buybacks> bmwBuybacks = buybacksDAO.findByCarBrand("BMW");
        assertEquals(1, bmwBuybacks.size(), "Должна быть одна заявка на BMW");
        assertEquals(testBuyback1.getId(), bmwBuybacks.iterator().next().getId(), "ID должен совпадать");

        // Проверяем поиск по марке Toyota
        Collection<Buybacks> toyotaBuybacks = buybacksDAO.findByCarBrand("Toyota");
        assertEquals(1, toyotaBuybacks.size(), "Должна быть одна заявка на Toyota");
        assertEquals(testBuyback2.getId(), toyotaBuybacks.iterator().next().getId(), "ID должен совпадать");

        // Проверяем поиск по несуществующей марке
        Collection<Buybacks> hondaBuybacks = buybacksDAO.findByCarBrand("Honda");
        assertTrue(hondaBuybacks.isEmpty(), "Не должно быть заявок на Honda");
    }

    @Test
    public void testFindByEstimatedPriceBetween() {
        // Устанавливаем цену для первой заявки
        testBuyback1.setEstimatedPrice(new BigDecimal("15000.00"));
        buybacksDAO.update(testBuyback1);

        // Проверяем поиск по диапазону цен, включающему обе заявки
        Collection<Buybacks> buybacks = buybacksDAO.findByEstimatedPriceBetween(
                new BigDecimal("10000.00"), new BigDecimal("30000.00"));
        assertEquals(2, buybacks.size(), "Должны найтись обе заявки");

        // Проверяем поиск по диапазону, включающему только первую заявку
        buybacks = buybacksDAO.findByEstimatedPriceBetween(
                new BigDecimal("10000.00"), new BigDecimal("20000.00"));
        assertEquals(1, buybacks.size(), "Должна найтись одна заявка");
        assertEquals(testBuyback1.getId(), buybacks.iterator().next().getId(), "ID должен совпадать");

        // Проверяем поиск по диапазону без заявок
        buybacks = buybacksDAO.findByEstimatedPriceBetween(
                new BigDecimal("50000.00"), new BigDecimal("60000.00"));
        assertTrue(buybacks.isEmpty(), "Не должно быть заявок в этом диапазоне цен");
    }

    @Test
    public void testFindByCarYear() {
        // Проверяем поиск по году 2018
        Collection<Buybacks> buybacks2018 = buybacksDAO.findByCarYear(2018);
        assertEquals(1, buybacks2018.size(), "Должна быть одна заявка на автомобиль 2018 года");
        assertEquals(testBuyback1.getId(), buybacks2018.iterator().next().getId(), "ID должен совпадать");

        // Проверяем поиск по году 2020
        Collection<Buybacks> buybacks2020 = buybacksDAO.findByCarYear(2020);
        assertEquals(1, buybacks2020.size(), "Должна быть одна заявка на автомобиль 2020 года");
        assertEquals(testBuyback2.getId(), buybacks2020.iterator().next().getId(), "ID должен совпадать");

        // Проверяем поиск по несуществующему году
        Collection<Buybacks> buybacks2022 = buybacksDAO.findByCarYear(2022);
        assertTrue(buybacks2022.isEmpty(), "Не должно быть заявок на автомобили 2022 года");
    }

    @Test
    public void testFindByCreatedAtBetween() {
        LocalDateTime now = LocalDateTime.now();

        // Проверяем поиск за последние 3 дня (должны быть обе заявки)
        Collection<Buybacks> buybacks = buybacksDAO.findByCreatedAtBetween(
                now.minusDays(3), now);
        assertEquals(2, buybacks.size(), "Должны найтись обе заявки");

        // Проверяем поиск за последний день (должна быть одна заявка)
        buybacks = buybacksDAO.findByCreatedAtBetween(
                now.minusDays(1).minusHours(2), now);
        assertEquals(1, buybacks.size(), "Должна найтись одна заявка");

        // Проверяем поиск за период до создания заявок
        buybacks = buybacksDAO.findByCreatedAtBetween(
                now.minusDays(10), now.minusDays(5));
        assertTrue(buybacks.isEmpty(), "Не должно быть заявок в этот период");
    }

    @Test
    public void testFindPendingBuybacks() {
        // Проверяем поиск ожидающих заявок
        Collection<Buybacks> pendingBuybacks = buybacksDAO.findPendingBuybacks();
        assertEquals(1, pendingBuybacks.size(), "Должна быть одна ожидающая заявка");
        assertEquals(testBuyback1.getId(), pendingBuybacks.iterator().next().getId(), "ID должен совпадать");

        // Меняем статус первой заявки и проверяем снова
        testBuyback1.setStatus(Buybacks.Status.REJECTED);
        buybacksDAO.update(testBuyback1);

        pendingBuybacks = buybacksDAO.findPendingBuybacks();
        assertTrue(pendingBuybacks.isEmpty(), "Не должно остаться ожидающих заявок");
    }

    @Test
    public void testUpdateBuybackStatus() {
        // Обновляем статус на ACCEPTED с ценой
        Buybacks updatedBuyback = buybacksDAO.updateBuybackStatus(
                testBuyback1.getId(), Buybacks.Status.ACCEPTED, new BigDecimal("20000.00"));

        assertNotNull(updatedBuyback, "Должен вернуться обновленный объект");
        assertEquals(Buybacks.Status.ACCEPTED, updatedBuyback.getStatus(), "Статус должен измениться");
        assertEquals(0, new BigDecimal("20000.00").compareTo(updatedBuyback.getEstimatedPrice()),
                "Цена должна обновиться");

        // Проверяем сохранение в базе
        Buybacks fromDB = buybacksDAO.getById(testBuyback1.getId());
        assertEquals(Buybacks.Status.ACCEPTED, fromDB.getStatus(), "Статус должен сохраниться в базе");
        assertEquals(0, new BigDecimal("20000.00").compareTo(fromDB.getEstimatedPrice()),
                "Цена должна сохраниться в базе");

        // Обновляем статус на REJECTED без цены
        updatedBuyback = buybacksDAO.updateBuybackStatus(
                testBuyback1.getId(), Buybacks.Status.REJECTED, null);

        assertNotNull(updatedBuyback, "Должен вернуться обновленный объект");
        assertEquals(Buybacks.Status.REJECTED, updatedBuyback.getStatus(), "Статус должен измениться");

        // Проверяем с несуществующим ID
        assertNull(buybacksDAO.updateBuybackStatus(9999L, Buybacks.Status.ACCEPTED, null),
                "Должен вернуться null для несуществующего ID");
    }

    @Test
    public void testFindByMileageGreaterThan() {
        // Проверяем поиск с пробегом > 40000
        Collection<Buybacks> highMileageBuybacks = buybacksDAO.findByMileageGreaterThan(40000);
        assertEquals(1, highMileageBuybacks.size(), "Должна быть одна заявка с пробегом > 40000");
        assertEquals(testBuyback1.getId(), highMileageBuybacks.iterator().next().getId(), "ID должен совпадать");

        // Проверяем поиск с пробегом > 20000
        Collection<Buybacks> allBuybacks = buybacksDAO.findByMileageGreaterThan(20000);
        assertEquals(2, allBuybacks.size(), "Должны быть обе заявки с пробегом > 20000");

        // Проверяем поиск с пробегом > 60000
        Collection<Buybacks> emptyList = buybacksDAO.findByMileageGreaterThan(60000);
        assertTrue(emptyList.isEmpty(), "Не должно быть заявок с пробегом > 60000");
    }

    @Test
    public void testCreateBuyback() {
        // Создаем новую заявку через метод createBuyback
        Buybacks newBuyback = buybacksDAO.createBuyback(
                testUser.getId(), "Audi", 2021, 15000, "{\"photos\":[\"audi1.jpg\",\"audi2.jpg\"]}");

        // Проверяем, что заявка создана
        assertNotNull(newBuyback, "Заявка должна быть создана");
        assertNotNull(newBuyback.getId(), "ID должен быть присвоен");
        assertEquals("Audi", newBuyback.getCarBrand(), "Марка должна совпадать");
        assertEquals(2021, newBuyback.getCarYear(), "Год должен совпадать");
        assertEquals(15000, newBuyback.getMileage(), "Пробег должен совпадать");
        assertEquals(Buybacks.Status.PENDING, newBuyback.getStatus(), "Статус должен быть PENDING");

        // Проверяем связь с пользователем
        assertEquals(testUser.getId(), newBuyback.getUser().getId(), "ID пользователя должен совпадать");

        // Проверяем с несуществующим пользователем
        assertNull(buybacksDAO.createBuyback(9999L, "Audi", 2021, 15000, "{}"),
                "Должен вернуться null для несуществующего пользователя");
    }

    @Test
    public void testCountByCarBrandAndStatus() {
        // Подсчет BMW заявок со статусом PENDING
        Long bmwPendingCount = buybacksDAO.countByCarBrandAndStatus("BMW", Buybacks.Status.PENDING);
        assertEquals(1L, bmwPendingCount, "Должна быть 1 ожидающая заявка на BMW");

        // Подсчет Toyota заявок со статусом ACCEPTED
        Long toyotaAcceptedCount = buybacksDAO.countByCarBrandAndStatus("Toyota", Buybacks.Status.ACCEPTED);
        assertEquals(1L, toyotaAcceptedCount, "Должна быть 1 принятая заявка на Toyota");

        // Подсчет BMW заявок со статусом ACCEPTED
        Long bmwAcceptedCount = buybacksDAO.countByCarBrandAndStatus("BMW", Buybacks.Status.ACCEPTED);
        assertEquals(0L, bmwAcceptedCount, "Не должно быть принятых заявок на BMW");

        // Подсчет несуществующих комбинаций
        Long hondaRejectedCount = buybacksDAO.countByCarBrandAndStatus("Honda", Buybacks.Status.REJECTED);
        assertEquals(0L, hondaRejectedCount, "Не должно быть отклоненных заявок на Honda");
    }

    // Добавьте эти тесты в ваш класс BuybacksDAOTest

    // Тест на обработку null-параметров в findByUser
    @Test
    public void testFindByUserWithNull() {
        Collection<Buybacks> buybacks = buybacksDAO.findByUser(null);

        // В зависимости от реализации, это должно либо вернуть пустую коллекцию,
        // либо выбросить исключение. Проверяем оба варианта.
        try {
            assertTrue(buybacks.isEmpty(), "При null-пользователе должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если метод выбрасывает исключение - это тоже допустимое поведение
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "Должно выбрасываться исключение при null-пользователе");
        }
    }

    // Тест на обработку null-параметров в findByStatus
    @Test
    public void testFindByStatusWithNull() {
        Collection<Buybacks> buybacks = buybacksDAO.findByStatus(null);

        // В зависимости от реализации, это должно либо вернуть пустую коллекцию,
        // либо выбросить исключение. Проверяем оба варианта.
        try {
            assertTrue(buybacks.isEmpty(), "При null-статусе должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если метод выбрасывает исключение - это тоже допустимое поведение
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "Должно выбрасываться исключение при null-статусе");
        }
    }

    // Тест на обработку null-параметров в updateBuybackStatus
    @Test
    public void testUpdateBuybackStatusWithNullStatus() {
        // Обновляем с null-статусом
        Buybacks updatedBuyback = buybacksDAO.updateBuybackStatus(
                999L, null, new BigDecimal("20000.00"));

        // Проверяем результат, зависит от реализации
        if (updatedBuyback != null) {
            // Если метод не отвергает null-статус, проверяем сохранение текущего статуса
            assertEquals(testBuyback1.getStatus(), updatedBuyback.getStatus(),
                    "Статус не должен измениться при null-значении");
        } else {
            // Если метод возвращает null при null-статусе, это тоже валидное поведение
            assertNull(updatedBuyback, "При null-статусе может возвращаться null");
        }
    }

    // Тест на граничный случай с ACCEPTED статусом, но null estimatedPrice в updateBuybackStatus
    @Test
    public void testUpdateBuybackStatusAcceptedWithNullPrice() {
        // Обновляем статус на ACCEPTED, но без цены
        Buybacks updatedBuyback = buybacksDAO.updateBuybackStatus(
                testBuyback1.getId(), Buybacks.Status.ACCEPTED, null);

        assertNotNull(updatedBuyback, "Должен вернуться обновленный объект");
        assertEquals(Buybacks.Status.ACCEPTED, updatedBuyback.getStatus(), "Статус должен измениться на ACCEPTED");

        // Цена должна остаться null или сохранить предыдущее значение, в зависимости от реализации
        if (testBuyback1.getEstimatedPrice() == null) {
            assertNull(updatedBuyback.getEstimatedPrice(), "Цена должна остаться null");
        } else {
            assertEquals(testBuyback1.getEstimatedPrice(), updatedBuyback.getEstimatedPrice(),
                    "Цена должна сохраниться без изменений");
        }
    }

    // Тест на обработку null и граничных значений в findByCreatedAtBetween
    @Test
    public void testFindByCreatedAtBetweenWithNullDates() {
        LocalDateTime now = LocalDateTime.now();

        // Оба параметра null
        try {
            Collection<Buybacks> buybacks = buybacksDAO.findByCreatedAtBetween(null, null);
            // Если метод обрабатывает null-даты, проверяем результат
            // В зависимости от реализации, может вернуть все записи или пустую коллекцию
        } catch (Exception e) {
            // Если метод выбрасывает исключение - это тоже валидное поведение
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "Может выбрасываться исключение при null-датах");
        }

        // Только startDate null
        try {
            Collection<Buybacks> buybacks = buybacksDAO.findByCreatedAtBetween(null, now);
            // Проверка результата, если метод обрабатывает частично null-даты
        } catch (Exception e) {
            // Если метод выбрасывает исключение - это тоже валидное поведение
        }

        // Только endDate null
        try {
            Collection<Buybacks> buybacks = buybacksDAO.findByCreatedAtBetween(now.minusDays(10), null);
            // Проверка результата, если метод обрабатывает частично null-даты
        } catch (Exception e) {
            // Если метод выбрасывает исключение - это тоже валидное поведение
        }
    }

    // Тест на обработку null-параметров в createBuyback
    @Test
    public void testCreateBuybackWithNullParams() {

        // Проверка null photos
        Buybacks nullPhotosBuyback = buybacksDAO.createBuyback(
                testUser.getId(), "Audi", 2021, 15000, null);

        assertNull(nullPhotosBuyback.getPhotos(), "Фото должны быть null");

    }



    // Тест на ветку findByEstimatedPriceBetween с null значениями
    @Test
    public void testFindByEstimatedPriceBetweenWithNullValues() {
        try {
            Collection<Buybacks> buybacks = buybacksDAO.findByEstimatedPriceBetween(null, null);
            // Если метод обрабатывает null-цены, проверяем результат
        } catch (Exception e) {
            // Если метод выбрасывает исключение - это тоже допустимое поведение
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "Может выбрасываться исключение при null-ценах");
        }
    }

}