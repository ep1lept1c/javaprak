package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.TechnicalSpecsDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.TechnicalSpecs;

import java.math.BigDecimal;
import java.util.Collection;

@Repository
public class TechnicalSpecsDAOImpl extends CommonDAOImpl<TechnicalSpecs, Long> implements TechnicalSpecsDAO {

    public TechnicalSpecsDAOImpl() {
        super(TechnicalSpecs.class);
    }

    @Override
    public TechnicalSpecs findByCar(Cars car) {
        try (Session session = sessionFactory.openSession()) {
            Query<TechnicalSpecs> query = session.createQuery("FROM TechnicalSpecs WHERE car = :car", TechnicalSpecs.class);
            query.setParameter("car", car);
            return query.uniqueResult();
        }
    }

    @Override
    public Collection<TechnicalSpecs> findByFuelType(TechnicalSpecs.FuelType fuelType) {
        try (Session session = sessionFactory.openSession()) {
            Query<TechnicalSpecs> query = session.createQuery("FROM TechnicalSpecs WHERE fuelType = :fuelType", TechnicalSpecs.class);
            query.setParameter("fuelType", fuelType);
            return query.getResultList();
        }
    }

    @Override
    public Collection<TechnicalSpecs> findByPowerBetween(int minPower, int maxPower) {
        try (Session session = sessionFactory.openSession()) {
            Query<TechnicalSpecs> query = session.createQuery(
                    "FROM TechnicalSpecs WHERE power BETWEEN :minPower AND :maxPower",
                    TechnicalSpecs.class
            );
            query.setParameter("minPower", minPower);
            query.setParameter("maxPower", maxPower);
            return query.getResultList();
        }
    }

    @Override
    public Collection<TechnicalSpecs> findByAutomaticTransmission(boolean automatic) {
        try (Session session = sessionFactory.openSession()) {
            Query<TechnicalSpecs> query = session.createQuery(
                    "FROM TechnicalSpecs WHERE automaticTransmission = :automatic",
                    TechnicalSpecs.class
            );
            query.setParameter("automatic", automatic);
            return query.getResultList();
        }
    }
}