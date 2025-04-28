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

    /**
     * Находит заявки на выкуп автомобилей определенного года выпуска
     * @param year год выпуска
     * @return коллекция заявок на выкуп автомобилей указанного года
     */
    Collection<Buybacks> findByCarYear(int year);

    /**
     * Находит заявки на выкуп, созданные в указанный период
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return коллекция заявок на выкуп в указанном диапазоне дат
     */
    Collection<Buybacks> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Находит ожидающие заявки на выкуп, отсортированные по дате создания
     * @return коллекция ожидающих заявок на выкуп
     */
    Collection<Buybacks> findPendingBuybacks();

    /**
     * Обновляет статус заявки на выкуп и устанавливает оценочную стоимость
     * @param buybackId ID заявки на выкуп
     * @param newStatus новый статус
     * @param estimatedPrice оценочная стоимость (может быть null для статуса REJECTED)
     * @return обновленная заявка на выкуп или null, если заявка не найдена
     */
    Buybacks updateBuybackStatus(Long buybackId, Buybacks.Status newStatus, BigDecimal estimatedPrice);

    /**
     * Находит заявки на выкуп с пробегом больше указанного
     * @param minMileage минимальный пробег
     * @return коллекция заявок на выкуп с подходящим пробегом
     */
    Collection<Buybacks> findByMileageGreaterThan(int minMileage);

    /**
     * Создает новую заявку на выкуп
     * @param userId ID пользователя
     * @param carBrand марка автомобиля
     * @param carYear год выпуска
     * @param mileage пробег
     * @param photos JSON-строка с фотографиями
     * @return созданная заявка на выкуп
     */
    Buybacks createBuyback(Long userId, String carBrand, int carYear, int mileage, String photos);

    /**
     * Находит количество заявок на выкуп по марке и статусу
     * @param brand марка автомобиля
     * @param status статус заявки
     * @return количество заявок на выкуп
     */
    Long countByCarBrandAndStatus(String brand, Buybacks.Status status);
}