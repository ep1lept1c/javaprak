package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.UsersDAO;
import ru.msu.cmc.webprak.models.Users;
import ru.msu.cmc.webprak.models.Orders;
import ru.msu.cmc.webprak.models.TestDrives;
import ru.msu.cmc.webprak.models.Buybacks;

import java.util.Collection;
import java.util.Collections;

@Repository
public class UsersDAOImpl extends CommonDAOImpl<Users, Long> implements UsersDAO {

    public UsersDAOImpl() {
        super(Users.class);
    }

    @Override
    public Users findByEmail(String email) {
        Session session = sessionFactory.openSession();
            Query<Users> query = session.createQuery("FROM Users WHERE email = :email", Users.class);
            query.setParameter("email", email);
            return query.uniqueResult();

    }

    @Override
    public Collection<Users> findByRole(Users.Role role) {
        Session session = sessionFactory.openSession();
         Query<Users> query = session.createQuery("FROM Users WHERE role = :role", Users.class);
            query.setParameter("role", role);
            return query.getResultList();
    }

    @Override
    public Collection<Users> findByFullNameContaining(String name) {
        Session session = sessionFactory.openSession();
            Query<Users> query = session.createQuery("FROM Users WHERE lower(fullName) LIKE :name", Users.class);
            query.setParameter("name", "%" + name.toLowerCase() + "%");
            return query.getResultList();

    }

    @Override
    public Collection<Orders> getOrdersByUser(Long userId) {
        Session session = sessionFactory.openSession();
         Users user = getById(userId);
            if (user != null) {
                Query<Orders> query = session.createQuery(
                        "FROM Orders WHERE user = :user", Orders.class);
                query.setParameter("user", user);
                return query.getResultList();
            }
            return Collections.emptyList();

    }

    @Override
    public Collection<TestDrives> getTestDrivesByUser(Long userId) {
        Session session = sessionFactory.openSession();
            Users user = getById(userId);
            if (user != null) {
                Query<TestDrives> query = session.createQuery(
                        "FROM TestDrives WHERE user = :user", TestDrives.class);
                query.setParameter("user", user);
                return query.getResultList();
            }
            return Collections.emptyList();

    }

    @Override
    public Collection<Buybacks> getBuybacksByUser(Long userId) {
        Session session = sessionFactory.openSession();
            Users user = getById(userId);
            if (user != null) {
                Query<Buybacks> query = session.createQuery(
                        "FROM Buybacks WHERE user = :user", Buybacks.class);
                query.setParameter("user", user);
                return query.getResultList();
            }
            return Collections.emptyList();

    }
}