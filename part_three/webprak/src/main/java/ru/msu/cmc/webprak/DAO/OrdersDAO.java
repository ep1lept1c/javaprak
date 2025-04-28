package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Orders;
import ru.msu.cmc.webprak.models.Users;
import ru.msu.cmc.webprak.models.Cars;
import java.time.LocalDateTime;
import java.util.Collection;

public interface OrdersDAO extends CommonDAO<Orders, Long> {
    /**
     * Находит заказы по пользователю
     * @param user пользователь
     * @return коллекция заказов указанного пользователя
     */
    Collection<Orders> findByUser(Users user);

    /**
     * Находит заказы по автомобилю
     * @param car автомобиль
     * @return коллекция заказов для указанного автомобиля
     */
    Collection<Orders> findByCar(Cars car);

    /**
     * Находит заказы по статусу
     * @param status статус заказа
     * @return коллекция заказов с указанным статусом
     */
    Collection<Orders> findByStatus(Orders.Status status);

    /**
     * Находит заказы по дате заказа в заданном диапазоне
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return коллекция заказов в указанном диапазоне дат
     */
    Collection<Orders> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Находит заказы, требующие тест-драйв
     * @param required требуется ли тест-драйв
     * @return коллекция заказов с тест-драйвом или без
     */
    Collection<Orders> findByTestDriveRequired(boolean required);

    /**
     * Получает последние заказы в указанном количестве
     * @param limit количество заказов
     * @return коллекция последних заказов
     */
    Collection<Orders> findRecentOrders(int limit);

    /**
     * Обновляет статус заказа
     * @param orderId ID заказа
     * @param newStatus новый статус
     * @return обновленный заказ или null, если заказ не найден
     */
    Orders updateOrderStatus(Long orderId, Orders.Status newStatus);

    /**
     * Находит заказы по пользователю и статусу
     * @param user пользователь
     * @param status статус заказа
     * @return коллекция заказов с указанным пользователем и статусом
     */
    Collection<Orders> findByUserAndStatus(Users user, Orders.Status status);

    /**
     * Подсчитывает количество заказов с указанным статусом
     * @param status статус заказа
     * @return количество заказов с указанным статусом
     */
    Long countByStatus(Orders.Status status);

    /**
     * Создает новый заказ с указанными параметрами
     * @param userId ID пользователя
     * @param carId ID автомобиля
     * @param testDriveRequired требуется ли тест-драйв
     * @return созданный заказ
     */
    Orders createOrder(Long userId, Long carId, boolean testDriveRequired);
}