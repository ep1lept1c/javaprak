package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.Promotions;
import ru.msu.cmc.webprak.models.TechnicalSpecs;
import ru.msu.cmc.webprak.models.ConsumerSpecs;
import ru.msu.cmc.webprak.models.DynamicSpecs;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

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

    /**
     * Находит автомобили, участвующие в указанной акции
     * @param promotion акция
     * @return коллекция автомобилей, участвующих в акции
     */
    Collection<Cars> findByPromotion(Promotions promotion);

    /**
     * Добавляет автомобиль к акции
     * @param carId ID автомобиля
     * @param promotionId ID акции
     * @return обновленный автомобиль или null, если автомобиль или акция не найдены
     */
    Cars addPromotionToCar(Long carId, Long promotionId);

    /**
     * Удаляет автомобиль из акции
     * @param carId ID автомобиля
     * @param promotionId ID акции
     * @return обновленный автомобиль или null, если автомобиль или акция не найдены
     */
    Cars removePromotionFromCar(Long carId, Long promotionId);

    /**
     * Получает технические характеристики автомобиля
     * @param carId ID автомобиля
     * @return технические характеристики или null, если не найдены
     */
    TechnicalSpecs getTechnicalSpecs(Long carId);

    /**
     * Получает потребительские характеристики автомобиля
     * @param carId ID автомобиля
     * @return потребительские характеристики или null, если не найдены
     */
    ConsumerSpecs getConsumerSpecs(Long carId);

    /**
     * Получает динамические характеристики автомобиля
     * @param carId ID автомобиля
     * @return динамические характеристики или null, если не найдены
     */
    DynamicSpecs getDynamicSpecs(Long carId);

    /**
     * Обновляет технические характеристики автомобиля
     * @param carId ID автомобиля
     * @param specs технические характеристики
     * @return обновленные технические характеристики или null, если автомобиль не найден
     */
    TechnicalSpecs updateTechnicalSpecs(Long carId, TechnicalSpecs specs);

    /**
     * Обновляет цену автомобиля
     * @param carId ID автомобиля
     * @param newPrice новая цена
     * @return обновленный автомобиль или null, если автомобиль не найден
     */
    Cars updatePrice(Long carId, BigDecimal newPrice);

    /**
     * Обновляет статус автомобиля
     * @param carId ID автомобиля
     * @param newStatus новый статус
     * @return обновленный автомобиль или null, если автомобиль не найден
     */
    Cars updateStatus(Long carId, Cars.Status newStatus);

    /**
     * Находит автомобили с полной комплектацией (кондиционер, мультимедиа, GPS)
     * @return коллекция полностью укомплектованных автомобилей
     */
    Collection<Cars> findFullyEquippedCars();

    /**
     * Находит автомобили с расширенным поиском по различным критериям
     * @param brand марка автомобиля (опционально)
     * @param minPrice минимальная цена (опционально)
     * @param maxPrice максимальная цена (опционально)
     * @param status статус автомобиля (опционально)
     * @param hasAC наличие кондиционера (опционально)
     * @param fuelType тип топлива (опционально)
     * @param color цвет (опционально)
     * @return коллекция автомобилей, соответствующих критериям поиска
     */
    Collection<Cars> findWithAdvancedSearch(String brand, BigDecimal minPrice, BigDecimal maxPrice,
                                            Cars.Status status, Boolean hasAC, String fuelType, String color);

    /**
     * Получает статистику по количеству автомобилей каждой марки
     * @return карта, где ключ - марка автомобиля, значение - количество
     */
    Map<String, Long> getCarCountByBrand();
}