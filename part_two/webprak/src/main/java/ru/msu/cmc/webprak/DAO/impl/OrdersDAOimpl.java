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

    @Override
    public Collection<Orders> findRecentOrders(int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Orders> query = session.createQuery(
                    "FROM Orders ORDER BY orderDate DESC",
                    Orders.class
            );
            query.setMaxResults(limit);
            return query.getResultList();
        }
    }

    @Override
    public Orders updateOrderStatus(Long orderId, Orders.Status newStatus) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Orders order = getById(orderId);
            if (order != null) {
                // Если меняем статус на SOLD, обновляем статус автомобиля
                if (newStatus == Orders.Status.COMPLETED) {
                    Cars car = order.getCar();
                    car.setStatus(Cars.Status.SOLD);
                    session.merge(car);
                }
                // Если меняем статус на PROCESSING или AWAITING_DELIVERY, обновляем статус автомобиля на RESERVED
                else if (newStatus == Orders.Status.PROCESSING || newStatus == Orders.Status.AWAITING_DELIVERY) {
                    Cars car = order.getCar();
                    car.setStatus(Cars.Status.RESERVED);
                    session.merge(car);
                }
                // Если меняем статус на CANCELLED, возвращаем статус автомобиля на AVAILABLE
                else if (newStatus == Orders.Status.CANCELLED) {
                    Cars car = order.getCar();
                    car.setStatus(Cars.Status.AVAILABLE);
                    session.merge(car);
                }

                order.setStatus(newStatus);
                session.merge(order);
                session.getTransaction().commit();
                return order;
            }

            session.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public Collection<Orders> findByUserAndStatus(Users user, Orders.Status status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Orders> query = session.createQuery(
                    "FROM Orders WHERE user = :user AND status = :status ORDER BY orderDate DESC",
                    Orders.class
            );
            query.setParameter("user", user);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    @Override
    public Long countByStatus(Orders.Status status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(o) FROM Orders o WHERE o.status = :status",
                    Long.class
            );
            query.setParameter("status", status);
            return query.uniqueResult();
        }
    }

    @Override
    public Orders createOrder(Long userId, Long carId, boolean testDriveRequired) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Users user = session.get(Users.class, userId);
            Cars car = session.get(Cars.class, carId);

            if (user != null && car != null && car.getStatus() == Cars.Status.AVAILABLE) {
                Orders order = new Orders();
                order.setUser(user);
                order.setCar(car);
                order.setOrderDate(LocalDateTime.now());
                order.setTestDriveRequired(testDriveRequired);
                order.setStatus(Orders.Status.PROCESSING);

                // Резервируем автомобиль
                car.setStatus(Cars.Status.RESERVED);
                session.merge(car);

                session.persist(order);
                session.getTransaction().commit();
                return order;
            }

            session.getTransaction().rollback();
            return null;
        }
    }
}