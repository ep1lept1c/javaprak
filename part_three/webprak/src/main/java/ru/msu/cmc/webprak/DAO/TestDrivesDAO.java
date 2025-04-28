package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.TestDrives;
import ru.msu.cmc.webprak.models.Users;
import ru.msu.cmc.webprak.models.Cars;
import java.time.LocalDateTime;
import java.util.Collection;

public interface TestDrivesDAO extends CommonDAO<TestDrives, Long> {
    /**
     * Находит тест-драйвы по пользователю
     * @param user пользователь
     * @return коллекция тест-драйвов указанного пользователя
     */
    Collection<TestDrives> findByUser(Users user);

    /**
     * Находит тест-драйвы по автомобилю
     * @param car автомобиль
     * @return коллекция тест-драйвов для указанного автомобиля
     */
    Collection<TestDrives> findByCar(Cars car);

    /**
     * Находит тест-драйвы по статусу
     * @param status статус тест-драйва
     * @return коллекция тест-драйвов с указанным статусом
     */
    Collection<TestDrives> findByStatus(TestDrives.Status status);

    /**
     * Находит тест-драйвы, запланированные на указанный период
     * @param startTime начальное время
     * @param endTime конечное время
     * @return коллекция тест-драйвов в указанном диапазоне времени
     */
    Collection<TestDrives> findByScheduledTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Проверяет доступность автомобиля для тест-драйва в указанное время
     * @param car автомобиль
     * @param time время для проверки
     * @return true если автомобиль доступен, иначе false
     */
    boolean isCarAvailableForTestDrive(Cars car, LocalDateTime time);

    /**
     * Находит предстоящие тест-драйвы
     * @param currentTime текущее время
     * @return коллекция предстоящих тест-драйвов
     */
    Collection<TestDrives> findUpcomingTestDrives(LocalDateTime currentTime);

    /**
     * Обновляет статус тест-драйва и увеличивает счетчик тест-драйвов для автомобиля, если статус COMPLETED
     * @param testDriveId ID тест-драйва
     * @param newStatus новый статус
     * @return обновленный тест-драйв или null, если тест-драйв не найден
     */
    TestDrives updateTestDriveStatus(Long testDriveId, TestDrives.Status newStatus);

    /**
     * Находит тест-драйвы по пользователю и статусу
     * @param user пользователь
     * @param status статус тест-драйва
     * @return коллекция тест-драйвов указанного пользователя с указанным статусом
     */
    Collection<TestDrives> findByUserAndStatus(Users user, TestDrives.Status status);
}