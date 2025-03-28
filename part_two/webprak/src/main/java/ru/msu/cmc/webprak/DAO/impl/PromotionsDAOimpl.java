package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.PromotionsDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.Promotions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

@Repository
public class PromotionsDAOImpl extends CommonDAOImpl<Promotions, Long> implements PromotionsDAO {

    public PromotionsDAOImpl() {
        super(Promotions.class);
    }

    @Override
    public Collection<Promotions> findByIsActiveTrue() {
        try (Session session = sessionFactory.openSession()) {
            Query<Promotions> query = session.createQuery("FROM Promotions WHERE isActive = true", Promotions.class);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Promotions> findActiveOnDate(LocalDate date) {
        try (Session session = sessionFactory.openSession()) {
            Query<Promotions> query = session.createQuery(
                    "FROM Promotions WHERE isActive = true AND startDate <= :date AND endDate >= :date",
                    Promotions.class
            );
            query.setParameter("date", date);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Promotions> findByDiscountGreaterThanEqual(BigDecimal minDiscount) {
        try (Session session = sessionFactory.openSession()) {
            Query<Promotions> query = session.createQuery(
                    "FROM Promotions WHERE discount >= :minDiscount",
                    Promotions.class
            );
            query.setParameter("minDiscount", minDiscount);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Promotions> findByTitleContaining(String title) {
        try (Session session = sessionFactory.openSession()) {
            Query<Promotions> query = session.createQuery(
                    "FROM Promotions WHERE lower(title) LIKE :title",
                    Promotions.class
            );
            query.setParameter("title", "%" + title.toLowerCase() + "%");
            return query.getResultList();
        }
    }

    @Override
    public Collection<Promotions> findByCar(Cars car) {
        try (Session session = sessionFactory.openSession()) {
            Query<Promotions> query = session.createQuery(
                    "SELECT p FROM Promotions p JOIN p.cars c WHERE c = :car",
                    Promotions.class
            );
            query.setParameter("car", car);
            return query.getResultList();
        }
    }

    @Override
    public void addCarToPromotion(Long promotionId, Long carId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Promotions promotion = session.get(Promotions.class, promotionId);
            Cars car = session.get(Cars.class, carId);

            if (promotion != null && car != null) {
                promotion.addCar(car);
                session.merge(promotion);
            }

            session.getTransaction().commit();
        }
    }

    @Override
    public void removeCarFromPromotion(Long promotionId, Long carId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Promotions promotion = session.get(Promotions.class, promotionId);
            Cars car = session.get(Cars.class, carId);

            if (promotion != null && car != null) {
                promotion.removeCar(car);
                session.merge(promotion);
                session.merge(car);
            }

            session.getTransaction().commit();
        }
    }

    @Override
    public Collection<Promotions> findUpcomingPromotions(LocalDate currentDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Promotions> query = session.createQuery(
                    "FROM Promotions WHERE startDate > :currentDate AND isActive = true ORDER BY startDate",
                    Promotions.class
            );
            query.setParameter("currentDate", currentDate);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Promotions> findExpiredActivePromotions(LocalDate currentDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Promotions> query = session.createQuery(
                    "FROM Promotions WHERE endDate < :currentDate AND isActive = true ORDER BY endDate",
                    Promotions.class
            );
            query.setParameter("currentDate", currentDate);
            return query.getResultList();
        }
    }

    @Override
    public int deactivateExpiredPromotions(LocalDate currentDate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            org.hibernate.query.MutationQuery query = session.createMutationQuery(
                    "UPDATE Promotions p SET p.isActive = false WHERE p.endDate < :currentDate AND p.isActive = true"
            );
            query.setParameter("currentDate", currentDate);
            int updatedCount = query.executeUpdate();

            session.getTransaction().commit();
            return updatedCount;
        }
    }
}