package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Buybacks;
import ru.msu.cmc.webprak.models.Users;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

public interface BuybacksDAO extends CommonDAO<Buybacks, Long> {

    /**
     * Находит заявки на выкуп по пользователю
     * @param user пользователь
     * @return коллекция заявок на выкуп указанного пользователя
     */
    Collection<Buybacks> findByUser(Users user);

    /**
     * Находит заявки на выкуп по статусу
     * @param status статус заявки
     * @return коллекция заявок на выкуп с указанным статусом
     */
    Collection<Buybacks> findByStatus(Buybacks.Status status);

    /**
     * Находит заявки на выкуп по марке автомобиля
     * @param brand марка автомобиля
     * @return коллекция заявок на выкуп автомобилей указанной марки
     */
    Collection<Buybacks> findByCarBrand(String brand);

    /**
     * Находит заявки на выкуп с оценочной стоимостью в указанном диапазоне
     * @param minPrice минимальная стоимость
     * @param maxPrice максимальная стоимость
     * @return коллекция заявок на выкуп с подходящей оценочной стоимостью
     */
    Collection<Buybacks> findByEstimatedPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}