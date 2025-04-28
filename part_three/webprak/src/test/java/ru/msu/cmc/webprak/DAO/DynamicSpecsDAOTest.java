package ru.msu.cmc.webprak.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.DynamicSpecs;
import ru.msu.cmc.webprak.utils.TestDataUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class DynamicSpecsDAOTest {

    @Autowired
    private DynamicSpecsDAO dynamicSpecsDAO;

    @Autowired
    private CarsDAO carsDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Cars testCar1;
    private Cars testCar2;
    private DynamicSpecs testSpecs1;
    private DynamicSpecs testSpecs2;

    @BeforeEach
    public void setup() {
        clearDatabase();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создаем тестовые автомобили
            testCar1 = TestDataUtil.createTestCar();
            testCar1.setRegistrationNumber("TEST1" + System.currentTimeMillis());
            session.persist(testCar1);

            testCar2 = TestDataUtil.createTestCar();
            testCar2.setRegistrationNumber("TEST2" + System.currentTimeMillis());
            session.persist(testCar2);

            // Создаем тестовые динамические характеристики
            testSpecs1 = new DynamicSpecs();
            testSpecs1.setCar(testCar1);
            testSpecs1.setMileage(5000);
            testSpecs1.setLastService(LocalDate.now().minusMonths(6));
            testSpecs1.setTestDriveCount(3);
            session.persist(testSpecs1);

            testSpecs2 = new DynamicSpecs();
            testSpecs2.setCar(testCar2);
            testSpecs2.setMileage(15000);
            testSpecs2.setLastService(LocalDate.now().minusMonths(13));
            testSpecs2.setTestDriveCount(10);
            session.persist(testSpecs2);

            session.getTransaction().commit();
        }
    }

    @BeforeAll
    @AfterEach
    public void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("DELETE FROM dynamicspecs", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM cars", Void.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    public void testFindByCar() {
        // Проверяем поиск по существующей машине
        DynamicSpecs specs = dynamicSpecsDAO.findByCar(testCar1);
        assertNotNull(specs);
        assertEquals(testSpecs1.getMileage(), specs.getMileage());

        // Проверяем поиск по несуществующей машине (правильный способ)
        try (Session session = sessionFactory.openSession()) {
            Cars unsavedCar = new Cars();
            unsavedCar.setId(999L); // Устанавливаем несуществующий ID

            DynamicSpecs result = dynamicSpecsDAO.findByCar(unsavedCar);
            assertNull(result);
        }
    }

    @Test
    public void testFindByMileageLessThan() {
        Collection<DynamicSpecs> specs = dynamicSpecsDAO.findByMileageLessThan(10000);
        assertEquals(1, specs.size());
        assertEquals(testSpecs1.getId(), specs.iterator().next().getId());

        specs = dynamicSpecsDAO.findByMileageLessThan(20000);
        assertEquals(2, specs.size());
    }

    @Test
    public void testFindByLastServiceAfter() {
        LocalDate date = LocalDate.now().minusYears(1);
        Collection<DynamicSpecs> specs = dynamicSpecsDAO.findByLastServiceAfter(date);
        assertEquals(1, specs.size());
        assertEquals(testSpecs1.getId(), specs.iterator().next().getId());
    }

    @Test
    public void testFindByTestDriveCountGreaterThan() {
        Collection<DynamicSpecs> specs = dynamicSpecsDAO.findByTestDriveCountGreaterThan(5);
        assertEquals(1, specs.size());
        assertEquals(testSpecs2.getId(), specs.iterator().next().getId());

        specs = dynamicSpecsDAO.findByTestDriveCountGreaterThan(15);
        assertTrue(specs.isEmpty());
    }

    @Test
    public void testIncrementTestDriveCount() {
        int initialCount = testSpecs1.getTestDriveCount();
        DynamicSpecs updated = dynamicSpecsDAO.incrementTestDriveCount(testCar1);

        assertNotNull(updated);
        assertEquals(initialCount + 1, updated.getTestDriveCount());

        // Проверяем в базе
        DynamicSpecs fromDb = dynamicSpecsDAO.findByCar(testCar1);
        assertEquals(initialCount + 1, fromDb.getTestDriveCount());
        DynamicSpecs updated2 = dynamicSpecsDAO.incrementTestDriveCount(null);
        assertNull(updated2);
    }

    @Test
    public void testFindByLastServiceBetween() {
        LocalDate start = LocalDate.now().minusYears(1);
        LocalDate end = LocalDate.now();

        Collection<DynamicSpecs> specs = dynamicSpecsDAO.findByLastServiceBetween(start, end);
        assertEquals(1, specs.size());
        assertEquals(testSpecs1.getId(), specs.iterator().next().getId());
    }

    @Test
    public void testUpdateMileage() {
        DynamicSpecs updated = dynamicSpecsDAO.updateMileage(testCar1.getId(), 7500);

        assertNotNull(updated);
        assertEquals(7500, updated.getMileage());

        // Проверяем валидацию (новый пробег не может быть меньше старого)
        assertNull(dynamicSpecsDAO.updateMileage(testCar1.getId(), 5000));
        DynamicSpecs updated2 = dynamicSpecsDAO.updateMileage(999L, 7500);
        assertNull(updated2);
    }

    @Test
    public void testUpdateLastServiceDate() {
        LocalDate newDate = LocalDate.now().minusMonths(3);
        DynamicSpecs updated = dynamicSpecsDAO.updateLastServiceDate(testCar1.getId(), newDate);

        assertNotNull(updated);
        assertEquals(newDate, updated.getLastService());

        // Проверяем в базе
        DynamicSpecs fromDb = dynamicSpecsDAO.findByCar(testCar1);
        assertEquals(newDate, fromDb.getLastService());
        DynamicSpecs updated2 = dynamicSpecsDAO.updateLastServiceDate(999L, newDate);
        assertNull(updated2);
    }

    @Test
    public void testFindCarsNeedingService() {
        // Настраиваем статус автомобиля (не sold)
        testCar1.setStatus(Cars.Status.available);
        testCar2.setStatus(Cars.Status.available);
        carsDAO.update(testCar1);
        carsDAO.update(testCar2);

        Collection<DynamicSpecs> needingService = dynamicSpecsDAO.findCarsNeedingService(LocalDate.now());
        assertEquals(1, needingService.size());
        assertEquals(testSpecs2.getId(), needingService.iterator().next().getId());

        // Проверяем, что проданные авто не включаются
        testCar2.setStatus(Cars.Status.sold);
        carsDAO.update(testCar2);
        needingService = dynamicSpecsDAO.findCarsNeedingService(LocalDate.now());
        assertTrue(needingService.isEmpty());
    }

    @Test
    public void testSaveAndDelete() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создаем новый автомобиль и спецификации
            Cars newCar = TestDataUtil.createTestCar();
            newCar.setRegistrationNumber("NEWCAR" + System.currentTimeMillis());
            session.persist(newCar);

            DynamicSpecs newSpecs = new DynamicSpecs();
            newSpecs.setCar(newCar);
            newSpecs.setMileage(8000);
            newSpecs.setLastService(LocalDate.now());
            session.persist(newSpecs);

            session.getTransaction().commit();
        }

        // Проверяем сохранение
        assertEquals(3, dynamicSpecsDAO.getAll().size());

        // Удаление
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            DynamicSpecs toDelete = session.get(DynamicSpecs.class, testSpecs1.getId());
            if (toDelete != null) {
                // Разрываем связь перед удалением
                toDelete.getCar().setDynamicSpecs(null);
                session.merge(toDelete.getCar());
                session.remove(toDelete);
            }
            session.getTransaction().commit();
        }

        // Проверяем удаление
        assertNull(dynamicSpecsDAO.getById(testSpecs1.getId()));
        assertEquals(2, dynamicSpecsDAO.getAll().size());
    }
}