package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.CarsDAO;
import ru.msu.cmc.webprak.models.Cars;

import java.math.BigDecimal;
import java.util.Collection;

@Repository
public class CarsDAOImpl extends CommonDAOImpl<Cars, Long> implements CarsDAO {

    public CarsDAOImpl() {
        super(Cars.class);
    }

    @Override
    public Cars findByRegistrationNumber(String regNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars WHERE registrationNumber = :regNumber", Cars.class);
            query.setParameter("regNumber", regNumber);
            return query.uniqueResult();
        }
    }

    @Override
    public Collection<Cars> findByBrand(String brand) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars WHERE brand = :brand", Cars.class);
            query.setParameter("brand", brand);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Cars> findByStatus(Cars.Status status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars WHERE status = :status", Cars.class);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Cars> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars WHERE price BETWEEN :minPrice AND :maxPrice", Cars.class);
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Cars> findByManufacturer(String manufacturer) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars WHERE manufacturer = :manufacturer", Cars.class);
            query.setParameter("manufacturer", manufacturer);
            return query.getResultList();
        }
    }
}