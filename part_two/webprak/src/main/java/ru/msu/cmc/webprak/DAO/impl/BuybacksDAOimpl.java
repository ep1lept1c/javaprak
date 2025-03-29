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
        Session session = sessionFactory.openSession();
        Query<Buybacks> query = session.createQuery("FROM Buybacks WHERE user = :user", Buybacks.class);
            query.setParameter("user", user);
            return query.getResultList();

    }

    @Override
    public Collection<Buybacks> findByStatus(Buybacks.Status status) {
        Session session = sessionFactory.openSession();
        Query<Buybacks> query = session.createQuery("FROM Buybacks WHERE status = :status", Buybacks.class);
            query.setParameter("status", status);
            return query.getResultList();

    }

    @Override
    public Collection<Buybacks> findByCarBrand(String brand) {
        Session session = sessionFactory.openSession();
        Query<Buybacks> query = session.createQuery(
                    "FROM Buybacks WHERE carBrand = :brand",
                    Buybacks.class
            );
            query.setParameter("brand", brand);
            return query.getResultList();

    }

    @Override
    public Collection<Buybacks> findByEstimatedPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        Session session = sessionFactory.openSession();
        Query<Buybacks> query = session.createQuery(
                    "FROM Buybacks WHERE estimatedPrice BETWEEN :minPrice AND :maxPrice",
                    Buybacks.class
            );
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);
            return query.getResultList();

    }

    @Override
    public Collection<Buybacks> findByCarYear(int year) {
        Session session = sessionFactory.openSession();
        Query<Buybacks> query = session.createQuery(
                    "FROM Buybacks WHERE carYear = :year",
                    Buybacks.class
            );
            query.setParameter("year", year);
            return query.getResultList();

    }

    @Override
    public Collection<Buybacks> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        Session session = sessionFactory.openSession();
        Query<Buybacks> query = session.createQuery(
                    "FROM Buybacks WHERE createdAt >= :startDate AND createdAt < :endDate",
                    Buybacks.class
            );
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();

    }

    @Override
    public Collection<Buybacks> findPendingBuybacks() {
        Session session = sessionFactory.openSession();
        Query<Buybacks> query = session.createQuery(
                    "FROM Buybacks WHERE status = :status ORDER BY createdAt",
                    Buybacks.class
            );
            query.setParameter("status", Buybacks.Status.PENDING);
            return query.getResultList();

    }

    @Override
    public Buybacks updateBuybackStatus(Long buybackId, Buybacks.Status newStatus, BigDecimal estimatedPrice) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

            Buybacks buyback = getById(buybackId);
            if (buyback != null) {
                buyback.setStatus(newStatus);

                // Устанавливаем оценочную стоимость только если она предоставлена и статус ACCEPTED
                if (estimatedPrice != null && newStatus == Buybacks.Status.ACCEPTED) {
                    buyback.setEstimatedPrice(estimatedPrice);
                }

                session.merge(buyback);
                session.getTransaction().commit();
                return buyback;
            }

            session.getTransaction().rollback();
            return null;

    }

    @Override
    public Collection<Buybacks> findByMileageGreaterThan(int minMileage) {
        Session session = sessionFactory.openSession();
        Query<Buybacks> query = session.createQuery(
                    "FROM Buybacks WHERE mileage > :minMileage",
                    Buybacks.class
            );
            query.setParameter("minMileage", minMileage);
            return query.getResultList();

    }

    @Override
    public Buybacks createBuyback(Long userId, String carBrand, int carYear, int mileage, String photos) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

            Users user = session.get(Users.class, userId);
            if (user != null) {
                Buybacks buyback = new Buybacks();
                buyback.setUser(user);
                buyback.setCarBrand(carBrand);
                buyback.setCarYear(carYear);
                buyback.setMileage(mileage);
                buyback.setPhotos(photos);
                buyback.setStatus(Buybacks.Status.PENDING);
                buyback.setCreatedAt(LocalDateTime.now());

                session.persist(buyback);
                session.getTransaction().commit();
                return buyback;
            }

            session.getTransaction().rollback();
            return null;

    }

    @Override
    public Long countByCarBrandAndStatus(String brand, Buybacks.Status status) {
        Session session = sessionFactory.openSession();
        Query<Long> query = session.createQuery(
                    "SELECT COUNT(b) FROM Buybacks b WHERE b.carBrand = :brand AND b.status = :status",
                    Long.class
            );
            query.setParameter("brand", brand);
            query.setParameter("status", status);
            return query.uniqueResult();

    }
}