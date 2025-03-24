package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.ConsumerSpecsDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.ConsumerSpecs;

import java.util.Collection;

@Repository
public class ConsumerSpecsDAOImpl extends CommonDAOImpl<ConsumerSpecs, Long> implements ConsumerSpecsDAO {

    public ConsumerSpecsDAOImpl() {
        super(ConsumerSpecs.class);
    }

    @Override
    public ConsumerSpecs findByCar(Cars car) {
        try (Session session = sessionFactory.openSession()) {
            Query<ConsumerSpecs> query = session.createQuery("FROM ConsumerSpecs WHERE car = :car", ConsumerSpecs.class);
            query.setParameter("car", car);
            return query.uniqueResult();
        }
    }

    @Override
    public Collection<ConsumerSpecs> findByColor(String color) {
        try (Session session = sessionFactory.openSession()) {
            Query<ConsumerSpecs> query = session.createQuery(
                    "FROM ConsumerSpecs WHERE color = :color",
                    ConsumerSpecs.class
            );
            query.setParameter("color", color);
            return query.getResultList();
        }
    }

    @Override
    public Collection<ConsumerSpecs> findByInteriorMaterial(String material) {
        try (Session session = sessionFactory.openSession()) {
            Query<ConsumerSpecs> query = session.createQuery(
                    "FROM ConsumerSpecs WHERE interiorMaterial = :material",
                    ConsumerSpecs.class
            );
            query.setParameter("material", material);
            return query.getResultList();
        }
    }

    @Override
    public Collection<ConsumerSpecs> findByHasAirConditioning(boolean hasAC) {
        try (Session session = sessionFactory.openSession()) {
            Query<ConsumerSpecs> query = session.createQuery(
                    "FROM ConsumerSpecs WHERE hasAirConditioning = :hasAC",
                    ConsumerSpecs.class
            );
            query.setParameter("hasAC", hasAC);
            return query.getResultList();
        }
    }
}