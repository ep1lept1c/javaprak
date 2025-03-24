package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.UsersDAO;
import ru.msu.cmc.webprak.models.Users;

import java.util.Collection;

@Repository
public class UsersDAOImpl extends CommonDAOImpl<Users, Long> implements UsersDAO {

    public UsersDAOImpl() {
        super(Users.class);
    }

    @Override
    public Users findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Users> query = session.createQuery("FROM Users WHERE email = :email", Users.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }

    @Override
    public Collection<Users> findByRole(Users.Role role) {
        try (Session session = sessionFactory.openSession()) {
            Query<Users> query = session.createQuery("FROM Users WHERE role = :role", Users.class);
            query.setParameter("role", role);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Users> findByFullNameContaining(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Users> query = session.createQuery("FROM Users WHERE lower(fullName) LIKE :name", Users.class);
            query.setParameter("name", "%" + name.toLowerCase() + "%");
            return query.getResultList();
        }
    }
}