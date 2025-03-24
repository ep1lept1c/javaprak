package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Cars;
import java.math.BigDecimal;
import java.util.Collection;

public interface CarsDAO extends CommonDAO<Cars, Long> {

    /**
     * Находит автомобиль по регистрационному номеру
     * @param regNumber регистрационный номер
     * @return автомобиль с указанным регистрационным номером или null, если не найден
     */
    Cars findByRegistrationNumber(String regNumber);

    /**
     * Находит автомобили по марке
     * @param brand марка автомобиля
     * @return коллекция автомобилей указанной марки
     */
    Collection<Cars> findByBrand(String brand);

    /**
     * Находит автомобили по статусу
     * @param status статус автомобиля
     * @return коллекция автомобилей с указанным статусом
     */
    Collection<Cars> findByStatus(Cars.Status status);

    /**
     * Находит автомобили в заданном ценовом диапазоне
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @return коллекция автомобилей в указанном ценовом диапазоне
     */
    Collection<Cars> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Находит автомобили по производителю
     * @param manufacturer производитель
     * @return коллекция автомобилей указанного производителя
     */
    Collection<Cars> findByManufacturer(String manufacturer);
}