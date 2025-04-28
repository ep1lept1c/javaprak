package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.DynamicSpecsDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.DynamicSpecs;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public class DynamicSpecsDAOImpl extends CommonDAOImpl<DynamicSpecs, Long> implements DynamicSpecsDAO {

    public DynamicSpecsDAOImpl() {
        super(DynamicSpecs.class);
    }

    @Override
    public DynamicSpecs findByCar(Cars car) {
        try (Session session = sessionFactory.openSession()) {
            Query<DynamicSpecs> query = session.createQuery("FROM DynamicSpecs WHERE car = :car", DynamicSpecs.class);
            query.setParameter("car", car);
            return query.uniqueResult();
        }
    }

    @Override
    public Collection<DynamicSpecs> findByMileageLessThan(int maxMileage) {
        try (Session session = sessionFactory.openSession()) {
            Query<DynamicSpecs> query = session.createQuery(
                    "FROM DynamicSpecs WHERE mileage < :maxMileage",
                    DynamicSpecs.class
            );
            query.setParameter("maxMileage", maxMileage);
            return query.getResultList();
        }
    }

    @Override
    public Collection<DynamicSpecs> findByLastServiceAfter(LocalDate date) {
        try (Session session = sessionFactory.openSession()) {
            Query<DynamicSpecs> query = session.createQuery(
                    "FROM DynamicSpecs WHERE lastService >= :date",
                    DynamicSpecs.class
            );
            query.setParameter("date", date);
            return query.getResultList();
        }
    }

    @Override
    public Collection<DynamicSpecs> findByTestDriveCountGreaterThan(int count) {
        try (Session session = sessionFactory.openSession()) {
            Query<DynamicSpecs> query = session.createQuery(
                    "FROM DynamicSpecs WHERE testDriveCount > :count",
                    DynamicSpecs.class
            );
            query.setParameter("count", count);
            return query.getResultList();
        }
    }

    @Override
    public DynamicSpecs incrementTestDriveCount(Cars car) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            DynamicSpecs specs = findByCar(car);
            if (specs != null) {
                specs.setTestDriveCount(specs.getTestDriveCount() + 1);
                session.merge(specs);
                session.getTransaction().commit();
                return specs;
            }

            session.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public Collection<DynamicSpecs> findByLastServiceBetween(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<DynamicSpecs> query = session.createQuery(
                    "FROM DynamicSpecs WHERE lastService BETWEEN :startDate AND :endDate",
                    DynamicSpecs.class
            );
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        }
    }

    @Override
    public DynamicSpecs updateMileage(Long carId, int newMileage) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            DynamicSpecs specs = getById(carId);
            if (specs != null) {
                // Проверка на валидность - новый пробег должен быть больше старого
                if (newMileage >= specs.getMileage()) {
                    specs.setMileage(newMileage);
                    session.merge(specs);
                    session.getTransaction().commit();
                    return specs;
                }
            }

            session.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public DynamicSpecs updateLastServiceDate(Long carId, LocalDate serviceDate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            DynamicSpecs specs = getById(carId);
            if (specs != null) {
                specs.setLastService(serviceDate);
                session.merge(specs);
                session.getTransaction().commit();
                return specs;
            }

            session.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public Collection<DynamicSpecs> findCarsNeedingService(LocalDate currentDate) {
        try (Session session = sessionFactory.openSession()) {
            LocalDate oneYearAgo = currentDate.minusYears(1);

            Query<DynamicSpecs> query = session.createQuery(
                    "FROM DynamicSpecs ds JOIN FETCH ds.car c WHERE ds.lastService < :oneYearAgo AND c.status != :soldStatus",
                    DynamicSpecs.class
            );
            query.setParameter("oneYearAgo", oneYearAgo);
            query.setParameter("soldStatus", Cars.Status.sold);
            return query.getResultList();
        }
    }
}