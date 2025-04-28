package ru.msu.cmc.webprak.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class OrdersDAOTest {

    @Autowired
    private OrdersDAO ordersDAO;

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private CarsDAO carsDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Users testUser;
    private Cars testCar1;
    private Cars testCar2;
    private Orders testOrder1;
    private Orders testOrder2;

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
            testUser.setRole(Users.Role.client);
            session.persist(testUser);

            // Создаем тестовые автомобили
            testCar1 = new Cars();
            testCar1.setBrand("Test Brand");
            testCar1.setManufacturer("Test Manufacturer");
            testCar1.setRegistrationNumber("TEST123");
            testCar1.setPrice(new BigDecimal("15000.00"));
            testCar1.setStatus(Cars.Status.available);
            testCar1.setCreatedAt(LocalDateTime.now());
            session.persist(testCar1);

            testCar2 = new Cars();
            testCar2.setBrand("Test Brand");
            testCar2.setManufacturer("Test Manufacturer");
            testCar2.setRegistrationNumber("TEST456");
            testCar2.setPrice(new BigDecimal("33000.00"));
            testCar2.setStatus(Cars.Status.available);
            testCar2.setCreatedAt(LocalDateTime.now());
            session.persist(testCar2);

            // Создаем тестовые заказы
            testOrder1 = new Orders();
            testOrder1.setUser(testUser);
            testOrder1.setCar(testCar1);
            testOrder1.setStatus(Orders.Status.processing);
            testOrder1.setOrderDate(LocalDateTime.now().minusDays(2));
            testOrder1.setTestDriveRequired(true);
            session.persist(testOrder1);

            testOrder2 = new Orders();
            testOrder2.setUser(testUser);
            testOrder2.setCar(testCar2);
            testOrder2.setStatus(Orders.Status.awaiting_delivery);
            testOrder2.setOrderDate(LocalDateTime.now().minusDays(1));
            testOrder2.setTestDriveRequired(false);
            session.persist(testOrder2);

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
        Collection<Orders> orders = ordersDAO.findByUser(testUser);
        assertEquals(2, orders.size());
    }

    @Test
    public void testFindByCar() {
        Collection<Orders> orders = ordersDAO.findByCar(testCar1);
        assertEquals(1, orders.size());
        assertEquals(testOrder1.getId(), orders.iterator().next().getId());
    }

    @Test
    public void testFindByStatus() {
        Collection<Orders> processingOrders = ordersDAO.findByStatus(Orders.Status.processing);
        assertEquals(1, processingOrders.size());
        assertEquals(testOrder1.getId(), processingOrders.iterator().next().getId());

        Collection<Orders> awaitingOrders = ordersDAO.findByStatus(Orders.Status.awaiting_delivery);
        assertEquals(1, awaitingOrders.size());
    }

    @Test
    public void testFindByOrderDateBetween() {
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now();

        Collection<Orders> orders = ordersDAO.findByOrderDateBetween(start, end);
        assertEquals(2, orders.size());
    }

    @Test
    public void testFindByTestDriveRequired() {
        Collection<Orders> withTestDrive = ordersDAO.findByTestDriveRequired(true);
        assertEquals(1, withTestDrive.size());
        assertEquals(testOrder1.getId(), withTestDrive.iterator().next().getId());

        Collection<Orders> withoutTestDrive = ordersDAO.findByTestDriveRequired(false);
        assertEquals(1, withoutTestDrive.size());
    }

    @Test
    public void testFindRecentOrders() {
        Collection<Orders> recentOrders = ordersDAO.findRecentOrders(1);
        assertEquals(1, recentOrders.size());
        assertEquals(testOrder2.getId(), recentOrders.iterator().next().getId());
    }

    @Test
    public void testUpdateOrderStatus() {
        // Проверяем обновление статуса на completed
        Orders updated = ordersDAO.updateOrderStatus(testOrder1.getId(), Orders.Status.completed);
        assertNotNull(updated);
        assertEquals(Orders.Status.completed, updated.getStatus());
        assertEquals(Cars.Status.sold, updated.getCar().getStatus());

        // Проверяем обновление статуса на cancelled
        updated = ordersDAO.updateOrderStatus(testOrder2.getId(), Orders.Status.cancelled);
        assertNotNull(updated);
        assertEquals(Orders.Status.cancelled, updated.getStatus());
        assertEquals(Cars.Status.available, updated.getCar().getStatus());

        // Проверяем обновление статуса на processing
        updated = ordersDAO.updateOrderStatus(testOrder2.getId(), Orders.Status.processing);
        assertNotNull(updated);
        assertEquals(Orders.Status.processing, updated.getStatus());
        assertEquals(Cars.Status.reserved, updated.getCar().getStatus());

        // Проверяем обновление статуса на awaiting_delivery
        updated = ordersDAO.updateOrderStatus(testOrder2.getId(), Orders.Status.awaiting_delivery);
        assertNotNull(updated);
        assertEquals(Orders.Status.awaiting_delivery, updated.getStatus());
        assertEquals(Cars.Status.reserved, updated.getCar().getStatus());

        // Проверяем несуществующий заказ
        assertNull(ordersDAO.updateOrderStatus(999L, Orders.Status.processing));

        // Проверяем несуществующий статус
        assertNull(ordersDAO.updateOrderStatus(testOrder2.getId(), null));
    }

    @Test
    public void testFindByUserAndStatus() {
        Collection<Orders> orders = ordersDAO.findByUserAndStatus(testUser, Orders.Status.processing);
        assertEquals(1, orders.size());
        assertEquals(testOrder1.getId(), orders.iterator().next().getId());
    }

    @Test
    public void testCountByStatus() {
        Long count = ordersDAO.countByStatus(Orders.Status.processing);
        assertEquals(1, count);

        count = ordersDAO.countByStatus(Orders.Status.awaiting_delivery);
        assertEquals(1, count);
    }

    @Test
    public void testCreateOrder() {
        // Создаем новый автомобиль для теста
        Cars newCar;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            newCar = new Cars();
            newCar.setBrand("New Brand");
            newCar.setManufacturer("New Manufacturer");
            newCar.setRegistrationNumber("NEW123");
            newCar.setPrice(new BigDecimal("115000.00"));
            newCar.setStatus(Cars.Status.available);
            newCar.setCreatedAt(LocalDateTime.now());
            session.persist(newCar);
            session.getTransaction().commit();
        }

        // Создаем заказ через DAO
        Orders newOrder = ordersDAO.createOrder(testUser.getId(), newCar.getId(), true);
        assertNotNull(newOrder);
        assertEquals(Orders.Status.processing, newOrder.getStatus());
        assertEquals(testUser.getId(), newOrder.getUser().getId());
        assertEquals(newCar.getId(), newOrder.getCar().getId());
        assertTrue(newOrder.getTestDriveRequired());
        assertEquals(Cars.Status.reserved, newOrder.getCar().getStatus());

        // Проверяем, что заказ сохранен в БД
        Orders fromDb = ordersDAO.getById(newOrder.getId());
        assertNotNull(fromDb);
        assertEquals(newOrder.getStatus(), fromDb.getStatus());

        // Проверяем ошибки при создании
        assertNull(ordersDAO.createOrder(999L, newCar.getId(), true)); // Несуществующий пользователь
        assertNull(ordersDAO.createOrder(testUser.getId(), 999L, true)); // Несуществующий автомобиль

        // Пытаемся создать заказ для уже зарезервированного автомобиля
        assertNull(ordersDAO.createOrder(testUser.getId(), newCar.getId(), true));
    }

    @Test
    public void testSaveAndDelete() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создаем новый заказ
            Orders newOrder = new Orders();
            newOrder.setUser(testUser);
            newOrder.setCar(testCar1);
            newOrder.setStatus(Orders.Status.processing);
            newOrder.setOrderDate(LocalDateTime.now());
            newOrder.setTestDriveRequired(false);
            session.persist(newOrder);

            session.getTransaction().commit();
        }

        // Проверяем сохранение
        assertEquals(3, ordersDAO.getAll().size());

        // Удаление
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Orders toDelete = session.get(Orders.class, testOrder1.getId());
            if (toDelete != null) {
                // Возвращаем автомобиль в доступные, если он был зарезервирован
                if (toDelete.getCar().getStatus() == Cars.Status.reserved) {
                    toDelete.getCar().setStatus(Cars.Status.available);
                    session.merge(toDelete.getCar());
                }
                session.remove(toDelete);
            }
            session.getTransaction().commit();
        }

        // Проверяем удаление
        assertNull(ordersDAO.getById(testOrder1.getId()));
        assertEquals(2, ordersDAO.getAll().size());
    }
}