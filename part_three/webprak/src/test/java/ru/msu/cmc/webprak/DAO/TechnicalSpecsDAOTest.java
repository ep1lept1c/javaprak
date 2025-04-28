package ru.msu.cmc.webprak.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.TechnicalSpecs;
import ru.msu.cmc.webprak.utils.TestDataUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class TechnicalSpecsDAOTest {

    @Autowired
    private TechnicalSpecsDAO technicalSpecsDAO;

    @Autowired
    private CarsDAO carsDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Cars testCar1;
    private Cars testCar2;
    private TechnicalSpecs testSpecs1;
    private TechnicalSpecs testSpecs2;

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

            // Создаем тестовые технические характеристики
            testSpecs1 = new TechnicalSpecs();
            testSpecs1.setCar(testCar1);
            testSpecs1.setFuelType(TechnicalSpecs.FuelType.petrol);
            testSpecs1.setPower(150);
            testSpecs1.setAutomaticTransmission(true);
            testSpecs1.setEngineVolume(new BigDecimal("2.0"));
            testSpecs1.setDoors(4);
            testSpecs1.setSeats(5);
            testSpecs1.setCruiseControl(true);
            testSpecs1.setFuelConsumption(new BigDecimal("8.5"));
            session.persist(testSpecs1);

            testSpecs2 = new TechnicalSpecs();
            testSpecs2.setCar(testCar2);
            testSpecs2.setFuelType(TechnicalSpecs.FuelType.diesel);
            testSpecs2.setPower(120);
            testSpecs2.setAutomaticTransmission(false);
            testSpecs2.setEngineVolume(new BigDecimal("1.6"));
            testSpecs2.setDoors(2);
            testSpecs2.setSeats(4);
            testSpecs2.setCruiseControl(false);
            testSpecs2.setFuelConsumption(new BigDecimal("5.5"));
            session.persist(testSpecs2);

            session.getTransaction().commit();
        }
    }

    @BeforeAll
    @AfterEach
    public void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("DELETE FROM technicalspecs", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM cars", Void.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    public void testFindByCar() {
        // Проверяем поиск по существующей машине
        TechnicalSpecs specs = technicalSpecsDAO.findByCar(testCar1);
        assertNotNull(specs);
        assertEquals(testSpecs1.getPower(), specs.getPower());

        // Проверяем поиск по несуществующей машине (правильный способ)
        Cars unsavedCar = new Cars();
        unsavedCar.setId(999L); // Устанавливаем несуществующий ID
        assertNull(technicalSpecsDAO.findByCar(unsavedCar));
    }

    @Test
    public void testFindByFuelType() {
        Collection<TechnicalSpecs> petrolSpecs = technicalSpecsDAO.findByFuelType(TechnicalSpecs.FuelType.petrol);
        assertEquals(1, petrolSpecs.size());
        assertEquals(testSpecs1.getId(), petrolSpecs.iterator().next().getId());

        Collection<TechnicalSpecs> dieselSpecs = technicalSpecsDAO.findByFuelType(TechnicalSpecs.FuelType.diesel);
        assertEquals(1, dieselSpecs.size());
    }

    @Test
    public void testFindByPowerBetween() {
        Collection<TechnicalSpecs> specs = technicalSpecsDAO.findByPowerBetween(100, 130);
        assertEquals(1, specs.size());
        assertEquals(testSpecs2.getId(), specs.iterator().next().getId());

        specs = technicalSpecsDAO.findByPowerBetween(100, 200);
        assertEquals(2, specs.size());
    }

    @Test
    public void testFindByAutomaticTransmission() {
        Collection<TechnicalSpecs> automaticSpecs = technicalSpecsDAO.findByAutomaticTransmission(true);
        assertEquals(1, automaticSpecs.size());
        assertEquals(testSpecs1.getId(), automaticSpecs.iterator().next().getId());

        Collection<TechnicalSpecs> manualSpecs = technicalSpecsDAO.findByAutomaticTransmission(false);
        assertEquals(1, manualSpecs.size());
    }

    @Test
    public void testFindByEngineVolumeBetween() {
        BigDecimal min = new BigDecimal("1.5");
        BigDecimal max = new BigDecimal("1.8");

        Collection<TechnicalSpecs> specs = technicalSpecsDAO.findByEngineVolumeBetween(min, max);
        assertEquals(1, specs.size());
        assertEquals(testSpecs2.getId(), specs.iterator().next().getId());
    }

    @Test
    public void testFindByDoorsAndSeats() {
        Collection<TechnicalSpecs> specs = technicalSpecsDAO.findByDoorsAndSeats(4, 5);
        assertEquals(1, specs.size());
        assertEquals(testSpecs1.getId(), specs.iterator().next().getId());

        specs = technicalSpecsDAO.findByDoorsAndSeats(2, 4);
        assertEquals(1, specs.size());
    }

    @Test
    public void testFindByCruiseControl() {
        Collection<TechnicalSpecs> withCruise = technicalSpecsDAO.findByCruiseControl(true);
        assertEquals(1, withCruise.size());
        assertEquals(testSpecs1.getId(), withCruise.iterator().next().getId());

        Collection<TechnicalSpecs> withoutCruise = technicalSpecsDAO.findByCruiseControl(false);
        assertEquals(1, withoutCruise.size());
    }

    @Test
    public void testFindByFuelConsumptionLessThan() {
        BigDecimal maxConsumption = new BigDecimal("7.0");
        Collection<TechnicalSpecs> specs = technicalSpecsDAO.findByFuelConsumptionLessThan(maxConsumption);
        assertEquals(1, specs.size());
        assertEquals(testSpecs2.getId(), specs.iterator().next().getId());

        specs = technicalSpecsDAO.findByFuelConsumptionLessThan(new BigDecimal("10.0"));
        assertEquals(2, specs.size());
    }

    @Test
    public void testSaveAndDelete() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создаем новый автомобиль и технические характеристики
            Cars newCar = TestDataUtil.createTestCar();
            newCar.setRegistrationNumber("NEWCAR" + System.currentTimeMillis());
            session.persist(newCar);

            TechnicalSpecs newSpecs = new TechnicalSpecs();
            newSpecs.setCar(newCar);
            newSpecs.setFuelType(TechnicalSpecs.FuelType.petrol);
            newSpecs.setPower(180);
            newSpecs.setEngineVolume(new BigDecimal("2.0"));
            newSpecs.setAutomaticTransmission(true);
            newSpecs.setDoors(4);
            newSpecs.setSeats(5);
            session.persist(newSpecs);

            session.getTransaction().commit();
        }


        assertEquals(3, technicalSpecsDAO.getAll().size());


        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            TechnicalSpecs toDelete = session.get(TechnicalSpecs.class, testSpecs1.getId());
            if (toDelete != null) {
                toDelete.getCar().setTechnicalSpecs(null);
                session.merge(toDelete.getCar());
                session.remove(toDelete);
            }
            session.getTransaction().commit();
        }

        // Проверяем удаление
        assertNull(technicalSpecsDAO.getById(testSpecs1.getId()));
        assertEquals(2, technicalSpecsDAO.getAll().size());
    }

}