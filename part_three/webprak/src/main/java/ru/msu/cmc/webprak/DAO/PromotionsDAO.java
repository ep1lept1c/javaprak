package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Promotions;
import ru.msu.cmc.webprak.models.Cars;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface PromotionsDAO extends CommonDAO<Promotions, Long> {
    List<Promotions> getAllWithCars() ;
    /**
     * Находит активные акции
     * @return коллекция активных акций
     */
    Collection<Promotions> findByIsActiveTrue();

    /**
     * Находит акции, активные в указанную дату
     * @param date дата
     * @return коллекция акций, активных в указанную дату
     */
    Collection<Promotions> findActiveOnDate(LocalDate date);

    /**
     * Находит акции со скидкой не менее указанной
     * @param minDiscount минимальная скидка
     * @return коллекция акций с подходящей скидкой
     */
    Collection<Promotions> findByDiscountGreaterThanEqual(BigDecimal minDiscount);

    /**
     * Поиск акций по заголовку
     * @param title часть заголовка
     * @return коллекция акций с подходящим заголовком
     */
    Collection<Promotions> findByTitleContaining(String title);

    /**
     * Находит акции для указанного автомобиля
     * @param car автомобиль
     * @return коллекция акций для указанного автомобиля
     */
    Collection<Promotions> findByCar(Cars car);

    /**
     * Добавляет автомобиль к акции
     * @param promotionId ID акции
     * @param carId ID автомобиля
     */
    void addCarToPromotion(Long promotionId, Long carId);

    /**
     * Удаляет автомобиль из акции
     * @param promotionId ID акции
     * @param carId ID автомобиля
     */
    void removeCarFromPromotion(Long promotionId, Long carId);

    /**
     * Находит предстоящие акции относительно текущей даты
     * @param currentDate текущая дата
     * @return коллекция предстоящих акций
     */
    Collection<Promotions> findUpcomingPromotions(LocalDate currentDate);

    /**
     * Находит истекшие акции, которые все еще активны
     * @param currentDate текущая дата
     * @return коллекция истекших акций
     */
    Collection<Promotions> findExpiredActivePromotions(LocalDate currentDate);

    /**
     * Деактивирует истекшие акции
     * @param currentDate текущая дата
     * @return количество деактивированных акций
     */
    int deactivateExpiredPromotions(LocalDate currentDate);
}