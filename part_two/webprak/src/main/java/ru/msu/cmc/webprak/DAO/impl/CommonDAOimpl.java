package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.CommonDAO;
import ru.msu.cmc.webprak.models.BaseEntity;

import java.io.Serializable;
import java.util.Collection;

@Repository
public abstract class CommonDAOImpl<T extends BaseEntity<ID>, ID extends Serializable> implements CommonDAO<T, ID> {
    protected SessionFactory sessionFactory;
    protected Class<T> persistentClass;

    public CommonDAOImpl(Class<T> entityClass) {
        this.persistentClass = entityClass;
    }

    @Autowired
    public void setSessionFactory(LocalSessionFactoryBean sessionFactory) {
        this.sessionFactory = sessionFactory.getObject();
    }

    @Override
    public T getById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(persistentClass, id);
        }
    }

    @Override
    public Collection<T> getAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(persistentClass);
            criteriaQuery.from(persistentClass);
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public void save(T entity) {
        try (Session session = sessionFactory.openSession()) {
            if (entity.getId() != null) {
                entity.setId(null);
            }
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public void saveCollection(Collection<T> entities) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            for (T entity : entities) {
                if (entity.getId() != null) {
                    entity.setId(null);
                }
                // Заменяем saveOrUpdate на persist для Hibernate 6
                session.persist(entity);
            }
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            // Заменяем update на merge для Hibernate 6
            session.merge(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            if (!session.contains(entity)) {
                entity = session.merge(entity);
            }
            session.remove(entity); // Заменяем delete на remove
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            T entity = getById(id);
            if (entity != null) {
                session.beginTransaction();
                session.remove(entity); // Заменяем delete на remove
                session.getTransaction().commit();
            }
        }
    }
}