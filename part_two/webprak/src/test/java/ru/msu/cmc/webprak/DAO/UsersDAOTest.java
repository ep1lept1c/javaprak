package ru.msu.cmc.webprak.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.*;
import ru.msu.cmc.webprak.utils.TestDataUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class UsersDAOTest {

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private OrdersDAO ordersDAO;

    @Autowired
    private TestDrivesDAO testDrivesDAO;

    @Autowired
    private BuybacksDAO buybacksDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Users testUser1;
    private Users testUser2;
    private Orders testOrder;
    private TestDrives testTestDrive;
    private Buybacks testBuyback;

    @BeforeEach
    public void setup() {
        clearDatabase();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создаем тестовых пользователей
            testUser1 = new Users();
            testUser1.setEmail("user1@test.com");
            testUser1.setPasswordHash("password1");
            testUser1.setFullName("Иван Иванов");
            testUser1.setPhone("+79111111111");
            testUser1.setRole(Users.Role.CLIENT);
            session.persist(testUser1);

            testUser2 = new Users();
            testUser2.setEmail("user2@test.com");
            testUser2.setPasswordHash("password2");
            testUser2.setFullName("Петр Петров");
            testUser2.setPhone("+79222222222");
            testUser2.setRole(Users.Role.ADMIN);
            session.persist(testUser2);

            // Создаем тестовые связанные сущности
            Cars testCar = TestDataUtil.createTestCar();
            session.persist(testCar);

            testOrder = new Orders();
            testOrder.setUser(testUser1);
            testOrder.setCar(testCar);
            testOrder.setStatus(Orders.Status.PROCESSING);
            session.persist(testOrder);

            testTestDrive = new TestDrives();
            testTestDrive.setUser(testUser1);
            testTestDrive.setCar(testCar);
            testTestDrive.setStatus(TestDrives.Status.COMPLETED);
            testTestDrive.setScheduledTime(LocalDateTime.now().plusDays(1));
            session.persist(testTestDrive);

            testBuyback = new Buybacks();
            testBuyback.setUser(testUser1);
            testBuyback.setCarBrand("Toyota");
            testBuyback.setStatus(Buybacks.Status.PENDING);
            testBuyback.setCarYear(2010);
            testBuyback.setCreatedAt(LocalDateTime.now());
            testBuyback.setMileage(50000);
            testBuyback.setPhotos("{\"photos\":[\"photo1.jpg\",\"photo2.jpg\"]}");
            session.persist(testBuyback);

            session.getTransaction().commit();
        }
    }

    @BeforeAll
    @AfterEach
    public void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("DELETE FROM orders", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM testdrives", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM buybacks", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM users", Void.class).executeUpdate();
            session.createNativeQuery("DELETE FROM cars", Void.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    public void testFindByEmail() {
        Users found = usersDAO.findByEmail("user1@test.com");
        assertNotNull(found);
        assertEquals(testUser1.getId(), found.getId());

        assertNull(usersDAO.findByEmail("nonexistent@test.com"));
    }

    @Test
    public void testFindByRole() {
        Collection<Users> clients = usersDAO.findByRole(Users.Role.CLIENT);
        assertEquals(1, clients.size());
        assertEquals(testUser1.getId(), clients.iterator().next().getId());

        Collection<Users> admins = usersDAO.findByRole(Users.Role.ADMIN);
        assertEquals(1, admins.size());
    }

    @Test
    public void testFindByFullNameContaining() {
        Collection<Users> users = usersDAO.findByFullNameContaining("иван");
        assertEquals(1, users.size());
        assertEquals(testUser1.getId(), users.iterator().next().getId());

        users = usersDAO.findByFullNameContaining("петр");
        assertEquals(1, users.size());
        assertEquals(testUser2.getId(), users.iterator().next().getId());

        users = usersDAO.findByFullNameContaining("ов");
        assertEquals(2, users.size());
    }

    @Test
    public void testGetOrdersByUser() {
        Collection<Orders> orders = usersDAO.getOrdersByUser(testUser1.getId());
        assertEquals(1, orders.size());
        assertEquals(testOrder.getId(), orders.iterator().next().getId());

        assertTrue(usersDAO.getOrdersByUser(testUser2.getId()).isEmpty());
    }

    @Test
    public void testGetTestDrivesByUser() {
        Collection<TestDrives> testDrives = usersDAO.getTestDrivesByUser(testUser1.getId());
        assertEquals(1, testDrives.size());
        assertEquals(testTestDrive.getId(), testDrives.iterator().next().getId());

        assertTrue(usersDAO.getTestDrivesByUser(testUser2.getId()).isEmpty());
    }

    @Test
    public void testGetBuybacksByUser() {
        Collection<Buybacks> buybacks = usersDAO.getBuybacksByUser(testUser1.getId());
        assertEquals(1, buybacks.size());
        assertEquals(testBuyback.getId(), buybacks.iterator().next().getId());

        assertTrue(usersDAO.getBuybacksByUser(testUser2.getId()).isEmpty());
    }

    @Test
    public void testSaveAndDelete() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создаем нового пользователя
            Users newUser = new Users();
            newUser.setEmail("new@test.com");
            newUser.setPasswordHash("newpass");
            newUser.setFullName("Новый Пользователь");
            newUser.setPhone("+79333333333");
            newUser.setRole(Users.Role.CLIENT);
            session.persist(newUser);

            session.getTransaction().commit();
        }

        // Проверяем сохранение
        assertEquals(3, usersDAO.getAll().size());

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.createMutationQuery("DELETE FROM Orders WHERE user = :user")
                    .setParameter("user", testUser1)
                    .executeUpdate();
            session.createMutationQuery("DELETE FROM TestDrives WHERE user = :user")
                    .setParameter("user", testUser1)
                    .executeUpdate();
            session.createMutationQuery("DELETE FROM Buybacks WHERE user = :user")
                    .setParameter("user", testUser1)
                    .executeUpdate();

            Users toDelete = session.get(Users.class, testUser1.getId());
            if (toDelete != null) {
                session.remove(toDelete);
            }

            session.getTransaction().commit();
        }

        // Проверяем удаление
        assertNull(usersDAO.getById(testUser1.getId()));
        assertEquals(2, usersDAO.getAll().size());
    }

    // Тест на null-параметры и граничные случаи
    @Test
    public void testNullAndBoundaryParameters() {
        // 1. findByEmail с null
        try {
            Users result = usersDAO.findByEmail(null);
            // Если не выбрасывает исключение, проверяем результат
            assertNull(result, "При null email должен возвращаться null");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "При null email может выбрасываться исключение");
        }

        // 2. findByRole с null
        try {
            Collection<Users> result = usersDAO.findByRole(null);
            // Если не выбрасывает исключение, проверяем результат
            assertTrue(result.isEmpty(), "При null role должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "При null role может выбрасываться исключение");
        }

        // 3. findByFullNameContaining с null
        try {
            Collection<Users> result = usersDAO.findByFullNameContaining(null);
            // Если не выбрасывает исключение, проверяем результат
            assertTrue(result.isEmpty(), "При null name должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "При null name может выбрасываться исключение");
        }

        // 4. getOrdersByUser с null ID
        try {
            Collection<Orders> result = usersDAO.getOrdersByUser(null);
            // Если не выбрасывает исключение, проверяем результат
            assertTrue(result.isEmpty(), "При null userId должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "При null userId может выбрасываться исключение");
        }

        // 5. getTestDrivesByUser с null ID
        try {
            Collection<TestDrives> result = usersDAO.getTestDrivesByUser(null);
            // Если не выбрасывает исключение, проверяем результат
            assertTrue(result.isEmpty(), "При null userId должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "При null userId может выбрасываться исключение");
        }

        // 6. getBuybacksByUser с null ID
        try {
            Collection<Buybacks> result = usersDAO.getBuybacksByUser(null);
            // Если не выбрасывает исключение, проверяем результат
            assertTrue(result.isEmpty(), "При null userId должна возвращаться пустая коллекция");
        } catch (Exception e) {
            // Если выбрасывает исключение - это тоже ОК
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "При null userId может выбрасываться исключение");
        }
    }

    // Тест на несуществующие ID для методов getXxxByUser
    @Test
    public void testNonExistentUserIds() {
        // Очень большой ID, который точно не существует
        Long nonExistentId = 999999L;

        // 1. getOrdersByUser с несуществующим ID
        Collection<Orders> orders = usersDAO.getOrdersByUser(nonExistentId);
        assertTrue(orders.isEmpty(), "Для несуществующего пользователя должна возвращаться пустая коллекция заказов");

        // 2. getTestDrivesByUser с несуществующим ID
        Collection<TestDrives> testDrives = usersDAO.getTestDrivesByUser(nonExistentId);
        assertTrue(testDrives.isEmpty(), "Для несуществующего пользователя должна возвращаться пустая коллекция тест-драйвов");

        // 3. getBuybacksByUser с несуществующим ID
        Collection<Buybacks> buybacks = usersDAO.getBuybacksByUser(nonExistentId);
        assertTrue(buybacks.isEmpty(), "Для несуществующего пользователя должна возвращаться пустая коллекция заявок на выкуп");
    }

    // Тест на граничные случаи для поиска по имени
    @Test
    public void testFindByFullNameEdgeCases() {
        // 1. Поиск по пустой строке (должен вернуть всех пользователей)
        Collection<Users> allUsers = usersDAO.findByFullNameContaining("");
        assertEquals(2, allUsers.size(), "Поиск по пустой строке должен вернуть всех пользователей");

        // 2. Поиск по одной букве
        Collection<Users> usersWithI = usersDAO.findByFullNameContaining("и");
        assertTrue(usersWithI.size() > 0, "Должны найтись пользователи с буквой 'и' в имени");

        // 3. Поиск по строке с пробелами
        Collection<Users> usersWithSpace = usersDAO.findByFullNameContaining(" ");
        assertEquals(2, usersWithSpace.size(), "Должны найтись все пользователи с пробелами в имени");
        
    }

    // Тест на кейс-чувствительность поиска по имени
    @Test
    public void testCaseInsensitiveNameSearch() {
        // Тест на регистр букв (верхний регистр)
        Collection<Users> upperCaseUsers = usersDAO.findByFullNameContaining("ИВАН");
        assertEquals(1, upperCaseUsers.size(), "Должен найтись пользователь при поиске в верхнем регистре");
        assertEquals(testUser1.getId(), upperCaseUsers.iterator().next().getId(), "Должен найтись правильный пользователь");

        // Смешанный регистр
        Collection<Users> mixedCaseUsers = usersDAO.findByFullNameContaining("иВаН");
        assertEquals(1, mixedCaseUsers.size(), "Должен найтись пользователь при поиске в смешанном регистре");
    }


}