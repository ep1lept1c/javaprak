package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.PromotionsDAO;
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
}