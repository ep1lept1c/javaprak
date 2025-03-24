package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Promotions;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

public interface PromotionsDAO extends CommonDAO<Promotions, Long> {

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
}