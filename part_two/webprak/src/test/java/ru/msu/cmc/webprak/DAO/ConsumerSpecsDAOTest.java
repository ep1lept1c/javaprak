package ru.msu.cmc.webprak.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.ConsumerSpecs;
import ru.msu.cmc.webprak.utils.TestDataUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class ConsumerSpecsDAOTest {

    @Autowired
    private ConsumerSpecsDAO consumerSpecsDAO;

    @Autowired
    private CarsDAO carsDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Cars testCar1;
    private Cars testCar2;
    private Cars testCar3;
    private ConsumerSpecs testSpecs1;
    private ConsumerSpecs testSpecs2;
    private ConsumerSpecs testSpecs3;

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

            testCar3 = TestDataUtil.createTestCar();
            testCar3.setRegistrationNumber("TEST3" + System.currentTimeMillis());
            session.persist(testCar3);

            // Создаем тестовые спецификации с разными характеристиками
            testSpecs1 = TestDataUtil.createTestConsumerSpecs(testCar1);
            testSpecs1.setColor("black");
            testSpecs1.setInteriorMaterial("leather");
            testSpecs1.setHasAirConditioning(true);
            testSpecs1.setHasMultimedia(true);
            testSpecs1.setHasGps(true);
            session.persist(testSpecs1);

            testSpecs2 = TestDataUtil.createTestConsumerSpecs(testCar2);
            testSpecs2.setColor("white");
            testSpecs2.setInteriorMaterial("alcantara");
            testSpecs2.setHasAirConditioning(true);
            testSpecs2.setHasMultimedia(false);
            testSpecs2.setHasGps(true);
            session.persist(testSpecs2);

            testSpecs3 = TestDataUtil.createTestConsumerSpecs(testCar3);
            testSpecs3.setColor("black");
            testSpecs3.setInteriorMaterial("fabric");
            testSpecs3.setHasAirConditioning(false);
            testSpecs3.setHasMultimedia(false);
            testSpecs3.setHasGps(false);
            session.persist(testSpecs3);

            session.getTransaction().commit();
        }
    }

    @BeforeAll
    @AfterEach
    public void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("DELETE FROM consumerspecs", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM cars", Void.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    public void testGetAll() {
        Collection<ConsumerSpecs> specs = consumerSpecsDAO.getAll();
        assertEquals(3, specs.size(), "Should have 3 consumer specs records");
    }

    @Test
    public void testGetById() {
        ConsumerSpecs specs = consumerSpecsDAO.getById(testSpecs1.getId());
        assertNotNull(specs, "Should retrieve specs by ID");
        assertEquals("black", specs.getColor(), "Color should match");

        assertNull(consumerSpecsDAO.getById(999L), "Should return null for non-existent ID");
    }


    @Test
    public void testSaveInSingleTransaction() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Create and persist car
            Cars newCar = TestDataUtil.createTestCar();
            newCar.setRegistrationNumber("NEW" + System.currentTimeMillis());
            session.persist(newCar);

            // Create and persist specs
            ConsumerSpecs newSpecs = TestDataUtil.createTestConsumerSpecs(newCar);
            newSpecs.setColor("red");
            session.persist(newSpecs);

            session.getTransaction().commit();

            // Verify
            ConsumerSpecs retrieved = consumerSpecsDAO.getById(newSpecs.getId());
            assertNotNull(retrieved);
            assertEquals(newCar.getId(), retrieved.getCar().getId());
        }
    }

    @Test
    public void testUpdate() {
        testSpecs1.setColor("blue");
        consumerSpecsDAO.update(testSpecs1);

        ConsumerSpecs updated = consumerSpecsDAO.getById(testSpecs1.getId());
        assertEquals("blue", updated.getColor(), "Color should be updated");
    }

    @Test
    public void testDelete() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // First remove the reference from the car
            testCar1.setConsumerSpecs(null);
            session.merge(testCar1);

            // Then delete the specs
            ConsumerSpecs toDelete = session.get(ConsumerSpecs.class, testSpecs1.getId());
            if (toDelete != null) {
                session.remove(toDelete);
            }

            session.getTransaction().commit();
        }

        // Verify deletion
        assertNull(consumerSpecsDAO.getById(testSpecs1.getId()), "Specs should be deleted");
        assertEquals(2, consumerSpecsDAO.getAll().size(), "Should have 2 specs left");

        // Verify the car still exists
        try (Session session = sessionFactory.openSession()) {
            Cars car = session.get(Cars.class, testCar1.getId());
            assertNotNull(car, "Car should still exist");
            assertNull(car.getConsumerSpecs(), "Car's specs reference should be null");
        }
    }

    @Test
    public void testFindByCar() {
        ConsumerSpecs specs = consumerSpecsDAO.findByCar(testCar1);
        assertNotNull(specs, "Should find specs by car");
        assertEquals(testSpecs1.getId(), specs.getId(), "IDs should match");

        Cars newCar = TestDataUtil.createTestCar();
        newCar.setRegistrationNumber("NEWCAR" + System.currentTimeMillis());
        carsDAO.save(newCar);

        assertNull(consumerSpecsDAO.findByCar(newCar), "Should return null for car without specs");
    }

    @Test
    public void testFindByColor() {
        Collection<ConsumerSpecs> blackSpecs = consumerSpecsDAO.findByColor("black");
        assertEquals(2, blackSpecs.size(), "Should find 2 cars with black color");

        Collection<ConsumerSpecs> whiteSpecs = consumerSpecsDAO.findByColor("white");
        assertEquals(1, whiteSpecs.size(), "Should find 1 car with white color");

        Collection<ConsumerSpecs> redSpecs = consumerSpecsDAO.findByColor("red");
        assertTrue(redSpecs.isEmpty(), "Should not find cars with red color");
    }

    @Test
    public void testFindByInteriorMaterial() {
        Collection<ConsumerSpecs> leatherSpecs = consumerSpecsDAO.findByInteriorMaterial("leather");
        assertEquals(1, leatherSpecs.size(), "Should find 1 car with leather interior");

        Collection<ConsumerSpecs> alcantaraSpecs = consumerSpecsDAO.findByInteriorMaterial("alcantara");
        assertEquals(1, alcantaraSpecs.size(), "Should find 1 car with alcantara interior");

        Collection<ConsumerSpecs> fabricSpecs = consumerSpecsDAO.findByInteriorMaterial("fabric");
        assertEquals(1, fabricSpecs.size(), "Should find 1 car with fabric interior");

        Collection<ConsumerSpecs> unknownSpecs = consumerSpecsDAO.findByInteriorMaterial("unknown");
        assertTrue(unknownSpecs.isEmpty(), "Should not find cars with unknown interior");
    }

    @Test
    public void testFindByHasAirConditioning() {
        Collection<ConsumerSpecs> withAC = consumerSpecsDAO.findByHasAirConditioning(true);
        assertEquals(2, withAC.size(), "Should find 2 cars with AC");

        Collection<ConsumerSpecs> withoutAC = consumerSpecsDAO.findByHasAirConditioning(false);
        assertEquals(1, withoutAC.size(), "Should find 1 car without AC");
    }

    @Test
    public void testFindByHasMultimedia() {
        Collection<ConsumerSpecs> withMultimedia = consumerSpecsDAO.findByHasMultimedia(true);
        assertEquals(1, withMultimedia.size(), "Should find 1 car with multimedia");

        Collection<ConsumerSpecs> withoutMultimedia = consumerSpecsDAO.findByHasMultimedia(false);
        assertEquals(2, withoutMultimedia.size(), "Should find 2 cars without multimedia");
    }

    @Test
    public void testFindByHasGps() {
        Collection<ConsumerSpecs> withGps = consumerSpecsDAO.findByHasGps(true);
        assertEquals(2, withGps.size(), "Should find 2 cars with GPS");

        Collection<ConsumerSpecs> withoutGps = consumerSpecsDAO.findByHasGps(false);
        assertEquals(1, withoutGps.size(), "Should find 1 car without GPS");
    }

    @Test
    public void testFindByMultipleFeatures() {
        // AC=true, Multimedia=true, GPS=true
        Collection<ConsumerSpecs> specs1 = consumerSpecsDAO.findByMultipleFeatures(true, true, true);
        assertEquals(1, specs1.size(), "Should find 1 car with all features");
        assertEquals(testSpecs1.getId(), specs1.iterator().next().getId(), "Should be testSpecs1");

        // AC=true, Multimedia=false, GPS=true
        Collection<ConsumerSpecs> specs2 = consumerSpecsDAO.findByMultipleFeatures(true, false, true);
        assertEquals(1, specs2.size(), "Should find 1 car with this combination");
        assertEquals(testSpecs2.getId(), specs2.iterator().next().getId(), "Should be testSpecs2");

        // AC=false, Multimedia=false, GPS=false
        Collection<ConsumerSpecs> specs3 = consumerSpecsDAO.findByMultipleFeatures(false, false, false);
        assertEquals(1, specs3.size(), "Should find 1 car with no features");
        assertEquals(testSpecs3.getId(), specs3.iterator().next().getId(), "Should be testSpecs3");

        // Несуществующая комбинация
        Collection<ConsumerSpecs> specs4 = consumerSpecsDAO.findByMultipleFeatures(true, true, false);
        assertTrue(specs4.isEmpty(), "Should not find cars with this combination");
    }

    @Test
    public void testUpdateConsumerSpecs() {
        // Обновляем все поля
        ConsumerSpecs updated = consumerSpecsDAO.updateConsumerSpecs(
                testSpecs1.getId(),
                "silver",
                "alcantara",
                false,
                false,
                false
        );

        assertNotNull(updated, "Should return updated specs");
        assertEquals("silver", updated.getColor(), "Color should be updated");
        assertEquals("alcantara", updated.getInteriorMaterial(), "Material should be updated");
        assertFalse(updated.getHasAirConditioning(), "AC should be updated");
        assertFalse(updated.getHasMultimedia(), "Multimedia should be updated");
        assertFalse(updated.getHasGps(), "GPS should be updated");

        // Проверяем в базе
        ConsumerSpecs fromDb = consumerSpecsDAO.getById(testSpecs1.getId());
        assertEquals("silver", fromDb.getColor(), "Changes should persist in DB");

        // Несуществующий ID
        assertNull(consumerSpecsDAO.updateConsumerSpecs(999L, "red", "leather", true, true, true),
                "Should return null for non-existent ID");
    }

    @Test
    public void testFindPopularColors() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Create and persist extra car
            Cars extraCar = TestDataUtil.createTestCar();
            extraCar.setRegistrationNumber("EXTRA" + System.currentTimeMillis());
            session.persist(extraCar);

            // Create and persist extra specs
            ConsumerSpecs extraSpecs = TestDataUtil.createTestConsumerSpecs(extraCar);
            extraSpecs.setColor("black");
            session.persist(extraSpecs);

            session.getTransaction().commit();
        }

        // Now test the DAO method
        Collection<String> popularColors = consumerSpecsDAO.findPopularColors(2);
        assertEquals(2, popularColors.size(), "Should return 2 colors");
        assertEquals("black", popularColors.iterator().next(), "Black should be first");

        Collection<String> singleColor = consumerSpecsDAO.findPopularColors(1);
        assertEquals(1, singleColor.size(), "Should return only 1 color");
        assertEquals("black", singleColor.iterator().next(), "Black should be the most popular");
    }

    @Test
    public void testComplexScenario() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Create and persist new car
            Cars newCar = TestDataUtil.createTestCar();
            newCar.setRegistrationNumber("COMPLEX" + System.currentTimeMillis());
            session.persist(newCar);

            // Create and persist new specs
            ConsumerSpecs newSpecs = new ConsumerSpecs();
            newSpecs.setCar(newCar);
            newSpecs.setColor("blue");
            newSpecs.setInteriorMaterial("leather");
            newSpecs.setHasAirConditioning(true);
            newSpecs.setHasMultimedia(false);
            newSpecs.setHasGps(true);
            session.persist(newSpecs);

            session.getTransaction().commit();
        }

        // Now test the DAO methods
        ConsumerSpecs created = consumerSpecsDAO.findByCar(testCar3); // Using existing test car
        assertNotNull(created, "Should find created specs");

        ConsumerSpecs updated = consumerSpecsDAO.updateConsumerSpecs(
                created.getId(),
                "green",
                "alcantara",
                false,
                true,
                false
        );
        assertNotNull(updated, "Update should succeed");
    }
}