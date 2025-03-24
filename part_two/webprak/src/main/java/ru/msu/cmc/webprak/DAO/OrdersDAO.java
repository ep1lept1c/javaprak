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
}