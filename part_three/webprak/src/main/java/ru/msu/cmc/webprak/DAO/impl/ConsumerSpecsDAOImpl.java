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

    @Override
    public Collection<ConsumerSpecs> findByHasMultimedia(boolean hasMultimedia) {
        try (Session session = sessionFactory.openSession()) {
            Query<ConsumerSpecs> query = session.createQuery(
                    "FROM ConsumerSpecs WHERE hasMultimedia = :hasMultimedia",
                    ConsumerSpecs.class
            );
            query.setParameter("hasMultimedia", hasMultimedia);
            return query.getResultList();
        }
    }

    @Override
    public Collection<ConsumerSpecs> findByHasGps(boolean hasGps) {
        try (Session session = sessionFactory.openSession()) {
            Query<ConsumerSpecs> query = session.createQuery(
                    "FROM ConsumerSpecs WHERE hasGps = :hasGps",
                    ConsumerSpecs.class
            );
            query.setParameter("hasGps", hasGps);
            return query.getResultList();
        }
    }

    @Override
    public Collection<ConsumerSpecs> findByMultipleFeatures(boolean hasAC, boolean hasMultimedia, boolean hasGps) {
        try (Session session = sessionFactory.openSession()) {
            Query<ConsumerSpecs> query = session.createQuery(
                    "FROM ConsumerSpecs WHERE hasAirConditioning = :hasAC AND hasMultimedia = :hasMultimedia AND hasGps = :hasGps",
                    ConsumerSpecs.class
            );
            query.setParameter("hasAC", hasAC);
            query.setParameter("hasMultimedia", hasMultimedia);
            query.setParameter("hasGps", hasGps);
            return query.getResultList();
        }
    }

    @Override
    public ConsumerSpecs updateConsumerSpecs(Long carId, String color, String interiorMaterial,
                                             boolean hasAC, boolean hasMultimedia, boolean hasGps) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            ConsumerSpecs specs = getById(carId);
            if (specs != null) {
                specs.setColor(color);
                specs.setInteriorMaterial(interiorMaterial);
                specs.setHasAirConditioning(hasAC);
                specs.setHasMultimedia(hasMultimedia);
                specs.setHasGps(hasGps);

                session.merge(specs);
                session.getTransaction().commit();
                return specs;
            }

            session.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public Collection<String> findPopularColors(int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<String> query = session.createQuery(
                    "SELECT cs.color FROM ConsumerSpecs cs " +
                            "GROUP BY cs.color " +
                            "ORDER BY COUNT(cs) DESC",
                    String.class
            );
            query.setMaxResults(limit);
            return query.getResultList();
        }
    }
}