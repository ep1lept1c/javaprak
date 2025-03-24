package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.OrdersDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.Orders;
import ru.msu.cmc.webprak.models.Users;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public class OrdersDAOImpl extends CommonDAOImpl<Orders, Long> implements OrdersDAO {

    public OrdersDAOImpl() {
        super(Orders.class);
    }

    @Override
    public Collection<Orders> findByUser(Users user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Orders> query = session.createQuery("FROM Orders WHERE user = :user", Orders.class);
            query.setParameter("user", user);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Orders> findByCar(Cars car) {
        try (Session session = sessionFactory.openSession()) {
            Query<Orders> query = session.createQuery("FROM Orders WHERE car = :car", Orders.class);
            query.setParameter("car", car);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Orders> findByStatus(Orders.Status status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Orders> query = session.createQuery("FROM Orders WHERE status = :status", Orders.class);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Orders> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Orders> query = session.createQuery("FROM Orders WHERE orderDate BETWEEN :startDate AND :endDate", Orders.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Orders> findByTestDriveRequired(boolean required) {
        try (Session session = sessionFactory.openSession()) {
            Query<Orders> query = session.createQuery("FROM Orders WHERE testDriveRequired = :required", Orders.class);
            query.setParameter("required", required);
            return query.getResultList();
        }
    }
}