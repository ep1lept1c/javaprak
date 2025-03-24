package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.DynamicSpecs;
import ru.msu.cmc.webprak.models.Cars;
import java.time.LocalDate;
import java.util.Collection;

public interface DynamicSpecsDAO extends CommonDAO<DynamicSpecs, Long> {

    /**
     * Находит динамические характеристики по автомобилю
     * @param car автомобиль
     * @return динамические характеристики для указанного автомобиля или null, если не найдены
     */
    DynamicSpecs findByCar(Cars car);

    /**
     * Находит автомобили с пробегом менее указанного
     * @param maxMileage максимальный пробег
     * @return коллекция динамических характеристик автомобилей с подходящим пробегом
     */
    Collection<DynamicSpecs> findByMileageLessThan(int maxMileage);

    /**
     * Находит автомобили, прошедшие техобслуживание после указанной даты
     * @param date дата
     * @return коллекция динамических характеристик автомобилей с подходящей датой ТО
     */
    Collection<DynamicSpecs> findByLastServiceAfter(LocalDate date);

    /**
     * Увеличивает счетчик тест-драйвов для автомобиля
     * @param car автомобиль
     */
    void incrementTestDriveCount(Cars car);
}