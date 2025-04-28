package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.BaseEntity;
import java.io.Serializable;
import java.util.Collection;

public interface CommonDAO<T extends BaseEntity<ID>, ID extends Serializable> {

    /**
     * Получает сущность по ID
     * @param id идентификатор сущности
     * @return сущность с указанным ID или null, если не найдена
     */
    T getById(ID id);

    /**
     * Получает все сущности данного типа
     * @return коллекция всех сущностей
     */
    Collection<T> getAll();

    /**
     * Сохраняет сущность в базу данных
     * @param entity сущность для сохранения
     */
    void save(T entity);

    /**
     * Сохраняет коллекцию сущностей в базу данных
     * @param entities коллекция сущностей для сохранения
     */
    void saveCollection(Collection<T> entities);

    /**
     * Обновляет сущность в базе данных
     * @param entity сущность для обновления
     */
    void update(T entity);

    /**
     * Удаляет сущность из базы данных
     * @param entity сущность для удаления
     */
    void delete(T entity);

    /**
     * Удаляет сущность по ID
     * @param id идентификатор сущности
     */
    void deleteById(ID id);
}