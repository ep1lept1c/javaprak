package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.ConsumerSpecs;
import ru.msu.cmc.webprak.models.Cars;
import java.util.Collection;

public interface ConsumerSpecsDAO extends CommonDAO<ConsumerSpecs, Long> {
    /**
     * Находит потребительские характеристики по автомобилю
     * @param car автомобиль
     * @return потребительские характеристики для указанного автомобиля или null, если не найдены
     */
    ConsumerSpecs findByCar(Cars car);

    /**
     * Находит автомобили указанного цвета
     * @param color цвет
     * @return коллекция потребительских характеристик автомобилей указанного цвета
     */
    Collection<ConsumerSpecs> findByColor(String color);

    /**
     * Находит автомобили с указанным материалом интерьера
     * @param material материал интерьера
     * @return коллекция потребительских характеристик автомобилей с указанным материалом интерьера
     */
    Collection<ConsumerSpecs> findByInteriorMaterial(String material);

    /**
     * Находит автомобили с кондиционером или без него
     * @param hasAC признак наличия кондиционера
     * @return коллекция потребительских характеристик автомобилей с/без кондиционера
     */
    Collection<ConsumerSpecs> findByHasAirConditioning(boolean hasAC);

    /**
     * Находит автомобили с мультимедиа системой или без неё
     * @param hasMultimedia признак наличия мультимедиа системы
     * @return коллекция потребительских характеристик автомобилей с/без мультимедиа системы
     */
    Collection<ConsumerSpecs> findByHasMultimedia(boolean hasMultimedia);

    /**
     * Находит автомобили с GPS-навигацией или без неё
     * @param hasGps признак наличия GPS-навигации
     * @return коллекция потребительских характеристик автомобилей с/без GPS-навигации
     */
    Collection<ConsumerSpecs> findByHasGps(boolean hasGps);

    /**
     * Находит автомобили по комбинации потребительских характеристик
     * @param hasAC признак наличия кондиционера
     * @param hasMultimedia признак наличия мультимедиа системы
     * @param hasGps признак наличия GPS-навигации
     * @return коллекция потребительских характеристик с указанной комбинацией опций
     */
    Collection<ConsumerSpecs> findByMultipleFeatures(boolean hasAC, boolean hasMultimedia, boolean hasGps);

    /**
     * Обновляет потребительские характеристики автомобиля
     * @param carId ID автомобиля
     * @param color цвет
     * @param interiorMaterial материал интерьера
     * @param hasAC признак наличия кондиционера
     * @param hasMultimedia признак наличия мультимедиа системы
     * @param hasGps признак наличия GPS-навигации
     * @return обновленные потребительские характеристики или null, если автомобиль не найден
     */
    ConsumerSpecs updateConsumerSpecs(Long carId, String color, String interiorMaterial,
                                      boolean hasAC, boolean hasMultimedia, boolean hasGps);

    /**
     * Находит популярные цвета автомобилей
     * @param limit количество цветов в результате
     * @return коллекция популярных цветов автомобилей
     */
    Collection<String> findPopularColors(int limit);
}