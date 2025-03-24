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
    public void incrementTestDriveCount(Cars car) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            DynamicSpecs specs = findByCar(car);
            if (specs != null) {
                specs.setTestDriveCount(specs.getTestDriveCount() + 1);
                session.update(specs);
            }

            session.getTransaction().commit();
        }
    }
}