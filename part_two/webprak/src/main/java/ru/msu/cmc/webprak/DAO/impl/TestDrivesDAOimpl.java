package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.TestDrivesDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.TestDrives;
import ru.msu.cmc.webprak.models.Users;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public class TestDrivesDAOImpl extends CommonDAOImpl<TestDrives, Long> implements TestDrivesDAO {

    public TestDrivesDAOImpl() {
        super(TestDrives.class);
    }

    @Override
    public Collection<TestDrives> findByUser(Users user) {
        try (Session session = sessionFactory.openSession()) {
            Query<TestDrives> query = session.createQuery("FROM TestDrives WHERE user = :user", TestDrives.class);
            query.setParameter("user", user);
            return query.getResultList();
        }
    }

    @Override
    public Collection<TestDrives> findByCar(Cars car) {
        try (Session session = sessionFactory.openSession()) {
            Query<TestDrives> query = session.createQuery("FROM TestDrives WHERE car = :car", TestDrives.class);
            query.setParameter("car", car);
            return query.getResultList();
        }
    }

    @Override
    public Collection<TestDrives> findByStatus(TestDrives.Status status) {
        try (Session session = sessionFactory.openSession()) {
            Query<TestDrives> query = session.createQuery("FROM TestDrives WHERE status = :status", TestDrives.class);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    @Override
    public Collection<TestDrives> findByScheduledTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        try (Session session = sessionFactory.openSession()) {
            Query<TestDrives> query = session.createQuery(
                    "FROM TestDrives WHERE scheduledTime BETWEEN :startTime AND :endTime",
                    TestDrives.class
            );
            query.setParameter("startTime", startTime);
            query.setParameter("endTime", endTime);
            return query.getResultList();
        }
    }

    @Override
    public boolean isCarAvailableForTestDrive(Cars car, LocalDateTime time) {
        try (Session session = sessionFactory.openSession()) {
            // Проверяем, нет ли тест-драйвов для этого автомобиля в выбранное время (±1 час)
            LocalDateTime startTime = time.minusHours(1);
            LocalDateTime endTime = time.plusHours(1);

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(td) FROM TestDrives td WHERE td.car = :car AND td.status != :canceledStatus " +
                            "AND td.scheduledTime BETWEEN :startTime AND :endTime",
                    Long.class
            );
            query.setParameter("car", car);
            query.setParameter("canceledStatus", TestDrives.Status.CANCELLED);
            query.setParameter("startTime", startTime);
            query.setParameter("endTime", endTime);

            Long count = query.uniqueResult();
            return count == 0;
        }
    }
}