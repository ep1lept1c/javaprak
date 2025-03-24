package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.DynamicSpecs;
import ru.msu.cmc.webprak.models.Cars;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DynamicSpecsDAO extends CommonDAO<DynamicSpecs, Long> {

    /**
     * Находит динамические характеристики по автомобилю
     * @param car автомобиль
     * @return Optional с найденными динамическими характеристиками или пустой Optional
     */
    Optional<DynamicSpecs> findByCar(Cars car);

    /**
     * Находит автомобили с пробегом менее указанного
     * @param maxMileage максимальный пробег
     * @return список динамических характеристик автомобилей с подходящим пробегом
     */
    List<DynamicSpecs> findByMileageLessThan(int maxMileage);

    /**
     * Находит автомобили, прошедшие техобслуживание после указанной даты
     * @param date дата
     * @return список динамических характеристик автомобилей с подходящей датой ТО
     */
    List<DynamicSpecs> findByLastServiceAfter(LocalDate date);

    /**
     * Находит автомобили с количеством тест-драйвов больше указанного
     * @param count количество тест-драйвов
     * @return список динамических характеристик автомобилей с подходящим числом тест-драйвов
     */
    List<DynamicSpecs> findByTestDriveCountGreaterThan(int count);

    /**
     * Увеличивает счетчик тест-драйвов для автомобиля
     * @param car автомобиль
     * @return обновленные динамические характеристики
     */
    DynamicSpecs incrementTestDriveCount(Cars car);
}