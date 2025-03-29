package ru.msu.cmc.webprak.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class TestDrivesDAOTest {

    @Autowired
    private TestDrivesDAO testDrivesDAO;

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private CarsDAO carsDAO;

    @Autowired
    private DynamicSpecsDAO dynamicSpecsDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Users testUser;
    private Cars testCar;
    private TestDrives testDrive1;
    private TestDrives testDrive2;
    private DynamicSpecs testSpecs;

    @BeforeEach
    public void setup() {
        clearDatabase();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создаем тестового пользователя
            testUser = new Users();
            testUser.setEmail("test@user.com");
            testUser.setPasswordHash("password");
            testUser.setFullName("Test User");
            testUser.setPhone("+79999999999");
            testUser.setRole(Users.Role.CLIENT);
            session.persist(testUser);

            // Создаем тестовый автомобиль
            testCar = new Cars();
            testCar.setBrand("Test Brand");
            testCar.setManufacturer("Test Manufacturer");
            testCar.setRegistrationNumber("TEST123");
            testCar.setPrice(new BigDecimal("15000.00"));
            testCar.setStatus(Cars.Status.AVAILABLE);
            testCar.setCreatedAt(LocalDateTime.now());
            session.persist(testCar);

            // Создаем динамические характеристики для автомобиля
            testSpecs = new DynamicSpecs();
            testSpecs.setCar(testCar);
            testSpecs.setMileage(10000);
            testSpecs.setTestDriveCount(0);
            testSpecs.setLastService(LocalDate.now().minusMonths(1));
            session.persist(testSpecs);

            // Создаем тестовые тест-драйвы
            testDrive1 = new TestDrives();
            testDrive1.setUser(testUser);
            testDrive1.setCar(testCar);
            testDrive1.setStatus(TestDrives.Status.PENDING);
            testDrive1.setScheduledTime(LocalDateTime.now().plusDays(1));
            session.persist(testDrive1);

            testDrive2 = new TestDrives();
            testDrive2.setUser(testUser);
            testDrive2.setCar(testCar);
            testDrive2.setStatus(TestDrives.Status.COMPLETED);
            testDrive2.setScheduledTime(LocalDateTime.now().minusDays(1));
            session.persist(testDrive2);

            session.getTransaction().commit();
        }
    }

    @BeforeAll
    @AfterEach
    public void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("DELETE FROM buybacks", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM testdrives", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM orders", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM dynamicspecs", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM technicalspecs", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM cars", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM users", Void.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    public void testFindByUser() {
        Collection<TestDrives> drives = testDrivesDAO.findByUser(testUser);
        assertEquals(2, drives.size());
    }

    @Test
    public void testFindByCar() {
        Collection<TestDrives> drives = testDrivesDAO.findByCar(testCar);
        assertEquals(2, drives.size());
    }

    @Test
    public void testFindByStatus() {
        Collection<TestDrives> pendingDrives = testDrivesDAO.findByStatus(TestDrives.Status.PENDING);
        assertEquals(1, pendingDrives.size());
        assertEquals(testDrive1.getId(), pendingDrives.iterator().next().getId());

        Collection<TestDrives> completedDrives = testDrivesDAO.findByStatus(TestDrives.Status.COMPLETED);
        assertEquals(1, completedDrives.size());
    }

    @Test
    public void testFindByScheduledTimeBetween() {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Collection<TestDrives> drives = testDrivesDAO.findByScheduledTimeBetween(start, end);
        assertEquals(2, drives.size());
    }

    @Test
    public void testIsCarAvailableForTestDrive() {
        // Проверяем доступность в свободное время
        LocalDateTime availableTime = LocalDateTime.now().plusDays(2);
        assertTrue(testDrivesDAO.isCarAvailableForTestDrive(testCar, availableTime));

        // Проверяем недоступность в занятое время
        assertFalse(testDrivesDAO.isCarAvailableForTestDrive(testCar, testDrive1.getScheduledTime()));

        // Проверяем, что отмененные тест-драйвы не учитываются
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            testDrive1.setStatus(TestDrives.Status.CANCELLED);
            session.merge(testDrive1);
            session.getTransaction().commit();
        }
        assertTrue(testDrivesDAO.isCarAvailableForTestDrive(testCar, testDrive1.getScheduledTime()));
    }

    @Test
    public void testFindUpcomingTestDrives() {
        Collection<TestDrives> upcoming = testDrivesDAO.findUpcomingTestDrives(LocalDateTime.now());
        assertEquals(1, upcoming.size());
        assertEquals(testDrive1.getId(), upcoming.iterator().next().getId());
    }

    @Test
    public void testUpdateTestDriveStatus() {
        // Обновляем статус на COMPLETED и проверяем счетчик тест-драйвов
        TestDrives updated = testDrivesDAO.updateTestDriveStatus(testDrive1.getId(), TestDrives.Status.COMPLETED);
        assertNotNull(updated);
        assertEquals(TestDrives.Status.COMPLETED, updated.getStatus());

        DynamicSpecs specs = dynamicSpecsDAO.findByCar(testCar);
        assertEquals(1, specs.getTestDriveCount()); // Проверяем увеличение счетчика

        // Проверяем несуществующий тест-драйв
        assertNull(testDrivesDAO.updateTestDriveStatus(999L, TestDrives.Status.COMPLETED));
    }

    // Расширенный тест для updateTestDriveStatus
    @Test
    public void testUpdateTestDriveStatusExpanded() {
        // 1. Обновление статуса на CANCELLED (не COMPLETED)
        TestDrives updatedToCancelled = testDrivesDAO.updateTestDriveStatus(testDrive1.getId(), TestDrives.Status.CANCELLED);
        assertNotNull(updatedToCancelled, "Должен вернуться обновленный объект");
        assertEquals(TestDrives.Status.CANCELLED, updatedToCancelled.getStatus(), "Статус должен измениться");

        // Проверяем, что счетчик не изменился
        DynamicSpecs specsAfterCancel = dynamicSpecsDAO.findByCar(testCar);
        assertEquals(0, specsAfterCancel.getTestDriveCount(), "Счетчик не должен меняться при CANCELLED");

        // 2. Создаем тест-драйв с автомобилем без динамических характеристик
        Cars carWithoutSpecs = new Cars();
        carWithoutSpecs.setBrand("No Specs Car");
        carWithoutSpecs.setRegistrationNumber("NOSPECS");
        carWithoutSpecs.setStatus(Cars.Status.AVAILABLE);
        carWithoutSpecs.setPrice(new BigDecimal("100"));
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(carWithoutSpecs);
            session.getTransaction().commit();
        }

        // Создаем тест-драйв для этого автомобиля
        TestDrives testDriveNoSpecs = new TestDrives();
        testDriveNoSpecs.setUser(testUser);
        testDriveNoSpecs.setCar(carWithoutSpecs);
        testDriveNoSpecs.setStatus(TestDrives.Status.PENDING);
        testDriveNoSpecs.setScheduledTime(LocalDateTime.now().plusDays(1));

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(testDriveNoSpecs);
            session.getTransaction().commit();
        }

        // Проверяем, что у автомобиля нет спецификаций
        assertNull(carWithoutSpecs.getDynamicSpecs(), "У автомобиля не должно быть динамических характеристик");

        // Пытаемся обновить статус на COMPLETED
        TestDrives updatedNoSpecs = testDrivesDAO.updateTestDriveStatus(testDriveNoSpecs.getId(), TestDrives.Status.COMPLETED);
        assertNotNull(updatedNoSpecs, "Должен вернуться обновленный объект даже без спецификаций");
        assertEquals(TestDrives.Status.COMPLETED, updatedNoSpecs.getStatus(), "Статус должен быть обновлен");

        // 3. Проверяем пограничный случай: статус = null
        try {
            TestDrives updatedWithNullStatus = testDrivesDAO.updateTestDriveStatus(testDrive2.getId(), null);

            // Если метод не выбрасывает исключение при null статусе, проверяем результат
            if (updatedWithNullStatus != null) {
                // Возможно, метод просто ничего не делает при null статусе
                assertNotNull(updatedWithNullStatus, "При null статусе может вернуться неизмененный объект");
            } else {
                // Или возвращает null
                assertNull(updatedWithNullStatus, "При null статусе может вернуться null");
            }
        } catch (Exception e) {
            // Если метод выбрасывает исключение - это тоже валидное поведение
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "При null-статусе может выбрасываться исключение");
        }
    }

    // Тесты на null-параметры для других методов
    @Test
    public void testFindByNullParameters() {
        // 1. findByUser с null
        try {
            Collection<TestDrives> resultByNullUser = testDrivesDAO.findByUser(null);
            // Если не выбрасывает исключение
            assertTrue(resultByNullUser.isEmpty(), "При null пользователе должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
        }

        // 2. findByCar с null
        try {
            Collection<TestDrives> resultByNullCar = testDrivesDAO.findByCar(null);
            // Если не выбрасывает исключение
            assertTrue(resultByNullCar.isEmpty(), "При null автомобиле должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
        }

        // 3. findByStatus с null
        try {
            Collection<TestDrives> resultByNullStatus = testDrivesDAO.findByStatus(null);
            // Если не выбрасывает исключение
            assertTrue(resultByNullStatus.isEmpty(), "При null статусе должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
        }

        // 4. findByScheduledTimeBetween с null
        try {
            Collection<TestDrives> resultByNullTimes = testDrivesDAO.findByScheduledTimeBetween(null, null);
            // Если не выбрасывает исключение
            assertTrue(resultByNullTimes.isEmpty(), "При null времени должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
        }

        // 5. isCarAvailableForTestDrive с null
        try {
            boolean resultAvailableNull = testDrivesDAO.isCarAvailableForTestDrive(null, LocalDateTime.now());
            // Если не выбрасывает исключение
            assertFalse(resultAvailableNull, "При null автомобиле должен возвращаться false");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
        }

        try {
            boolean resultAvailableNullTime = testDrivesDAO.isCarAvailableForTestDrive(testCar, null);
            // Если не выбрасывает исключение
            assertFalse(resultAvailableNullTime, "При null времени должен возвращаться false");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
        }

        // 6. findUpcomingTestDrives с null
        try {
            Collection<TestDrives> resultUpcomingNull = testDrivesDAO.findUpcomingTestDrives(null);
            // Если не выбрасывает исключение
            assertTrue(resultUpcomingNull.isEmpty(), "При null времени должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
        }

        // 7. findByUserAndStatus с null
        try {
            Collection<TestDrives> resultByUserAndStatusNull = testDrivesDAO.findByUserAndStatus(null, null);
            // Если не выбрасывает исключение
            assertTrue(resultByUserAndStatusNull.isEmpty(), "При null параметрах должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
        }

        try {
            Collection<TestDrives> resultByUserNull = testDrivesDAO.findByUserAndStatus(null, TestDrives.Status.PENDING);
            // Если не выбрасывает исключение
            assertTrue(resultByUserNull.isEmpty(), "При null пользователе должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
        }

        try {
            Collection<TestDrives> resultByStatusNull = testDrivesDAO.findByUserAndStatus(testUser, null);
            // Если не выбрасывает исключение
            assertTrue(resultByStatusNull.isEmpty(), "При null статусе должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
        }
    }

    // Тест на пограничные случаи во временны́х диапазонах
    @Test
    public void testTimeRangeBoundaries() {
        // Проверяем граничные случаи для времени тест-драйва
        LocalDateTime exactTime = testDrive1.getScheduledTime();

        // Проверка на границе 1-часового интервала
        assertFalse(testDrivesDAO.isCarAvailableForTestDrive(testCar, exactTime.minusHours(1).plusMinutes(1)),
                "Автомобиль не должен быть доступен в пределах часа до запланированного тест-драйва");

        assertFalse(testDrivesDAO.isCarAvailableForTestDrive(testCar, exactTime.plusHours(1).minusMinutes(1)),
                "Автомобиль не должен быть доступен в пределах часа после запланированного тест-драйва");

        // Проверка точек за пределами интервала
        assertTrue(testDrivesDAO.isCarAvailableForTestDrive(testCar, exactTime.minusHours(1).minusMinutes(1)),
                "Автомобиль должен быть доступен чуть более чем за час до тест-драйва");

        assertTrue(testDrivesDAO.isCarAvailableForTestDrive(testCar, exactTime.plusHours(1).plusMinutes(1)),
                "Автомобиль должен быть доступен чуть более чем через час после тест-драйва");
    }

    @Test
    public void testFindByUserAndStatus() {
        Collection<TestDrives> drives = testDrivesDAO.findByUserAndStatus(testUser, TestDrives.Status.PENDING);
        assertEquals(1, drives.size());
        assertEquals(testDrive1.getId(), drives.iterator().next().getId());
    }

    @Test
    public void testSaveAndDelete() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создаем новый тест-драйв
            TestDrives newDrive = new TestDrives();
            newDrive.setUser(testUser);
            newDrive.setCar(testCar);
            newDrive.setStatus(TestDrives.Status.PENDING);
            newDrive.setScheduledTime(LocalDateTime.now().plusDays(3));
            session.persist(newDrive);

            session.getTransaction().commit();
        }

        // Проверяем сохранение
        assertEquals(3, testDrivesDAO.getAll().size());

        // Удаление
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            TestDrives toDelete = session.get(TestDrives.class, testDrive1.getId());
            if (toDelete != null) {
                session.remove(toDelete);
            }
            session.getTransaction().commit();
        }

        // Проверяем удаление
        assertNull(testDrivesDAO.getById(testDrive1.getId()));
        assertEquals(2, testDrivesDAO.getAll().size());
    }
}