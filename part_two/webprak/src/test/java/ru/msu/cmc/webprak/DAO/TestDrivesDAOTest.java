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