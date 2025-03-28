package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Users;
import ru.msu.cmc.webprak.models.Orders;
import ru.msu.cmc.webprak.models.TestDrives;
import ru.msu.cmc.webprak.models.Buybacks;
import java.util.Collection;

public interface UsersDAO extends CommonDAO<Users, Long> {
    /**
     * Находит пользователя по email
     * @param email email пользователя
     * @return пользователь с указанным email или null, если не найден
     */
    Users findByEmail(String email);

    /**
     * Находит пользователей по роли
     * @param role роль пользователя
     * @return коллекция пользователей с указанной ролью
     */
    Collection<Users> findByRole(Users.Role role);

    /**
     * Находит пользователей по части ФИО
     * @param name часть ФИО для поиска
     * @return коллекция пользователей, чьи ФИО содержат указанную строку
     */
    Collection<Users> findByFullNameContaining(String name);

    /**
     * Получает все заказы пользователя
     * @param userId ID пользователя
     * @return коллекция заказов пользователя
     */
    Collection<Orders> getOrdersByUser(Long userId);

    /**
     * Получает все тест-драйвы пользователя
     * @param userId ID пользователя
     * @return коллекция тест-драйвов пользователя
     */
    Collection<TestDrives> getTestDrivesByUser(Long userId);

    /**
     * Получает все заявки на выкуп от пользователя
     * @param userId ID пользователя
     * @return коллекция заявок на выкуп от пользователя
     */
    Collection<Buybacks> getBuybacksByUser(Long userId);
}