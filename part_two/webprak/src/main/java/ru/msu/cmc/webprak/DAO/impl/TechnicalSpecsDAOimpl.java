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
        Session session = sessionFactory.openSession();
            Query<TechnicalSpecs> query = session.createQuery("FROM TechnicalSpecs WHERE car = :car", TechnicalSpecs.class);
            query.setParameter("car", car);
            return query.uniqueResult();

    }

    @Override
    public Collection<TechnicalSpecs> findByFuelType(TechnicalSpecs.FuelType fuelType) {
        Session session = sessionFactory.openSession();
        Query<TechnicalSpecs> query = session.createQuery("FROM TechnicalSpecs WHERE fuelType = :fuelType", TechnicalSpecs.class);
            query.setParameter("fuelType", fuelType);
            return query.getResultList();

    }

    @Override
    public Collection<TechnicalSpecs> findByPowerBetween(int minPower, int maxPower) {
        Session session = sessionFactory.openSession();
        Query<TechnicalSpecs> query = session.createQuery(
                    "FROM TechnicalSpecs WHERE power BETWEEN :minPower AND :maxPower",
                    TechnicalSpecs.class
            );
            query.setParameter("minPower", minPower);
            query.setParameter("maxPower", maxPower);
            return query.getResultList();

    }

    @Override
    public Collection<TechnicalSpecs> findByAutomaticTransmission(boolean automatic) {
        Session session = sessionFactory.openSession();
        Query<TechnicalSpecs> query = session.createQuery(
                    "FROM TechnicalSpecs WHERE automaticTransmission = :automatic",
                    TechnicalSpecs.class
            );
            query.setParameter("automatic", automatic);
            return query.getResultList();

    }

    @Override
    public Collection<TechnicalSpecs> findByEngineVolumeBetween(BigDecimal minVolume, BigDecimal maxVolume) {
        Session session = sessionFactory.openSession();
        Query<TechnicalSpecs> query = session.createQuery(
                    "FROM TechnicalSpecs WHERE engineVolume BETWEEN :minVolume AND :maxVolume",
                    TechnicalSpecs.class
            );
            query.setParameter("minVolume", minVolume);
            query.setParameter("maxVolume", maxVolume);
            return query.getResultList();

    }

    @Override
    public Collection<TechnicalSpecs> findByDoorsAndSeats(int doors, int seats) {
        Session session = sessionFactory.openSession();
        Query<TechnicalSpecs> query = session.createQuery(
                    "FROM TechnicalSpecs WHERE doors = :doors AND seats = :seats",
                    TechnicalSpecs.class
            );
            query.setParameter("doors", doors);
            query.setParameter("seats", seats);
            return query.getResultList();

    }

    @Override
    public Collection<TechnicalSpecs> findByCruiseControl(boolean hasCruiseControl) {
        Session session = sessionFactory.openSession();
        Query<TechnicalSpecs> query = session.createQuery(
                    "FROM TechnicalSpecs WHERE cruiseControl = :hasCruiseControl",
                    TechnicalSpecs.class
            );
            query.setParameter("hasCruiseControl", hasCruiseControl);
            return query.getResultList();

    }

    @Override
    public Collection<TechnicalSpecs> findByFuelConsumptionLessThan(BigDecimal maxConsumption) {
        Session session = sessionFactory.openSession();
        Query<TechnicalSpecs> query = session.createQuery(
                    "FROM TechnicalSpecs WHERE fuelConsumption IS NOT NULL AND fuelConsumption < :maxConsumption",
                    TechnicalSpecs.class
            );
            query.setParameter("maxConsumption", maxConsumption);
            return query.getResultList();

    }
}