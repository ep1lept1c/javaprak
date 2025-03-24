package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.BuybacksDAO;
import ru.msu.cmc.webprak.models.Buybacks;
import ru.msu.cmc.webprak.models.Users;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public class BuybacksDAOImpl extends CommonDAOImpl<Buybacks, Long> implements BuybacksDAO {

    public BuybacksDAOImpl() {
        super(Buybacks.class);
    }

    @Override
    public Collection<Buybacks> findByUser(Users user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Buybacks> query = session.createQuery("FROM Buybacks WHERE user = :user", Buybacks.class);
            query.setParameter("user", user);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Buybacks> findByStatus(Buybacks.Status status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Buybacks> query = session.createQuery("FROM Buybacks WHERE status = :status", Buybacks.class);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Buybacks> findByCarBrand(String brand) {
        try (Session session = sessionFactory.openSession()) {
            Query<Buybacks> query = session.createQuery(
                    "FROM Buybacks WHERE carBrand = :brand",
                    Buybacks.class
            );
            query.setParameter("brand", brand);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Buybacks> findByEstimatedPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        try (Session session = sessionFactory.openSession()) {
            Query<Buybacks> query = session.createQuery(
                    "FROM Buybacks WHERE estimatedPrice BETWEEN :minPrice AND :maxPrice",
                    Buybacks.class
            );
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Buybacks> findByCarYear(int year) {
        try (Session session = sessionFactory.openSession()) {
            Query<Buybacks> query = session.createQuery(
                    "FROM Buybacks WHERE carYear = :year",
                    Buybacks.class
            );
            query.setParameter("year", year);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Buybacks> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Buybacks> query = session.createQuery(
                    "FROM Buybacks WHERE createdAt BETWEEN :startDate AND :endDate",
                    Buybacks.class
            );
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        }
    }
}