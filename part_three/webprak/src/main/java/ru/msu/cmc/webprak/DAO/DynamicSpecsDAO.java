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
     * Находит автомобили с количеством тест-драйвов больше указанного
     * @param count количество тест-драйвов
     * @return коллекция динамических характеристик автомобилей с подходящим числом тест-драйвов
     */
    Collection<DynamicSpecs> findByTestDriveCountGreaterThan(int count);

    /**
     * Увеличивает счетчик тест-драйвов для автомобиля
     * @param car автомобиль
     * @return обновленные динамические характеристики
     */
    DynamicSpecs incrementTestDriveCount(Cars car);

    /**
     * Находит автомобили с последним техобслуживанием в указанном диапазоне дат
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return коллекция динамических характеристик автомобилей с подходящей датой ТО
     */
    Collection<DynamicSpecs> findByLastServiceBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Обновляет пробег автомобиля
     * @param carId ID автомобиля
     * @param newMileage новый пробег
     * @return обновленные динамические характеристики или null, если автомобиль не найден
     */
    DynamicSpecs updateMileage(Long carId, int newMileage);

    /**
     * Обновляет дату последнего технического обслуживания
     * @param carId ID автомобиля
     * @param serviceDate дата технического обслуживания
     * @return обновленные динамические характеристики или null, если автомобиль не найден
     */
    DynamicSpecs updateLastServiceDate(Long carId, LocalDate serviceDate);

    /**
     * Находит автомобили, требующие технического обслуживания (прошло больше года с последнего ТО)
     * @param currentDate текущая дата
     * @return коллекция динамических характеристик автомобилей, требующих ТО
     */
    Collection<DynamicSpecs> findCarsNeedingService(LocalDate currentDate);
}