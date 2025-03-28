package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.CarsDAO;
import ru.msu.cmc.webprak.models.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Repository
public class CarsDAOImpl extends CommonDAOImpl<Cars, Long> implements CarsDAO {

    public CarsDAOImpl() {
        super(Cars.class);
    }

    @Override
    public Cars findByRegistrationNumber(String regNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars WHERE registrationNumber = :regNumber", Cars.class);
            query.setParameter("regNumber", regNumber);
            return query.uniqueResult();
        }
    }

    @Override
    public Collection<Cars> findByBrand(String brand) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars WHERE brand = :brand", Cars.class);
            query.setParameter("brand", brand);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Cars> findByStatus(Cars.Status status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars WHERE status = :status", Cars.class);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Cars> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars WHERE price BETWEEN :minPrice AND :maxPrice", Cars.class);
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Cars> findByManufacturer(String manufacturer) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars WHERE manufacturer = :manufacturer", Cars.class);
            query.setParameter("manufacturer", manufacturer);
            return query.getResultList();
        }
    }

    @Override
    public Collection<Cars> findByPromotion(Promotions promotion) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery(
                    "SELECT c FROM Cars c JOIN c.promotions p WHERE p = :promotion",
                    Cars.class
            );
            query.setParameter("promotion", promotion);
            return query.getResultList();
        }
    }

    @Override
    public Cars addPromotionToCar(Long carId, Long promotionId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Cars car = session.get(Cars.class, carId);
            Promotions promotion = session.get(Promotions.class, promotionId);

            if (car != null && promotion != null) {
                car.addPromotion(promotion);
                session.merge(car);
                session.getTransaction().commit();
                return car;
            }

            session.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public Cars removePromotionFromCar(Long carId, Long promotionId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Cars car = session.get(Cars.class, carId);
            Promotions promotion = session.get(Promotions.class, promotionId);

            if (car != null && promotion != null) {
                car.removePromotion(promotion);
                session.merge(car);
                session.getTransaction().commit();
                return car;
            }

            session.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public TechnicalSpecs getTechnicalSpecs(Long carId) {
        if (carId == null) return null;
        try (Session session = sessionFactory.openSession()) {
            Cars car = session.get(Cars.class, carId);
            if (car != null) {
                return car.getTechnicalSpecs();
            }
            return null;
        }
    }

    @Override
    public ConsumerSpecs getConsumerSpecs(Long carId) {
        if (carId == null) return null;
        try (Session session = sessionFactory.openSession()) {
            Cars car = session.get(Cars.class, carId);
            if (car != null) {
                return car.getConsumerSpecs();
            }
            return null;
        }
    }

    @Override
    public DynamicSpecs getDynamicSpecs(Long carId) {
        if (carId == null) return null;
        try (Session session = sessionFactory.openSession()) {
            Cars car = session.get(Cars.class, carId);
            if (car != null) {
                return car.getDynamicSpecs();
            }
            return null;
        }
    }

    @Override
    public TechnicalSpecs updateTechnicalSpecs(Long carId, TechnicalSpecs specs) {
        if (carId == null || specs == null) return null;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Cars car = session.get(Cars.class, carId);
            if (car != null) {
                specs.setCar(car);
                specs.setId(carId);
                TechnicalSpecs updatedSpecs = session.merge(specs);
                session.getTransaction().commit();
                return updatedSpecs;
            }
            session.getTransaction().rollback();
            return null;
        }
    }


    @Override
    public Cars updatePrice(Long carId, BigDecimal newPrice) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Cars car = session.get(Cars.class, carId);
            if (car != null) {
                car.setPrice(newPrice);
                session.merge(car);
                session.getTransaction().commit();
                return car;
            }
            session.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public Cars updateStatus(Long carId, Cars.Status newStatus) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Cars car = session.get(Cars.class, carId);
            if (car != null && newStatus != null) {
                car.setStatus(newStatus);
                session.merge(car);
                session.getTransaction().commit();
                return car;
            }
            session.getTransaction().rollback();
            return null;
        }
    }



    @Override
    public Collection<Cars> findFullyEquippedCars() {
        try (Session session = sessionFactory.openSession()) {
            Query<Cars> query = session.createQuery(
                    "SELECT DISTINCT c FROM Cars c JOIN c.consumerSpecs cs " +
                            "WHERE cs.hasAirConditioning = true AND cs.hasMultimedia = true AND cs.hasGps = true",
                    Cars.class
            );
            return query.getResultList();
        }
    }
    @Override
    public Collection<Cars> findWithAdvancedSearch(String brand, BigDecimal minPrice, BigDecimal maxPrice,
                                                   Cars.Status status, Boolean hasAC, String fuelType, String color) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT DISTINCT c FROM Cars c");
            List<String> conditions = new ArrayList<>();

            // Добавляем необходимые JOIN в зависимости от параметров поиска
            boolean needConsumerSpecs = hasAC != null || color != null;
            boolean needTechnicalSpecs = fuelType != null;

            if (needConsumerSpecs) {
                queryBuilder.append(" JOIN c.consumerSpecs cs");
            }
            if (needTechnicalSpecs) {
                queryBuilder.append(" JOIN c.technicalSpecs ts");
            }

            // Добавляем условия для Cars
            if (brand != null && !brand.isEmpty()) {
                conditions.add("c.brand = :brand");
            }
            if (minPrice != null) {
                conditions.add("c.price >= :minPrice");
            }
            if (maxPrice != null) {
                conditions.add("c.price <= :maxPrice");
            }
            if (status != null) {
                conditions.add("c.status = :status");
            }

            // Добавляем условия для ConsumerSpecs
            if (hasAC != null) {
                conditions.add("cs.hasAirConditioning = :hasAC");
            }
            if (color != null && !color.isEmpty()) {
                conditions.add("cs.color = :color");
            }

            // Добавляем условия для TechnicalSpecs
            if (fuelType != null && !fuelType.isEmpty()) {
                conditions.add("ts.fuelType = :fuelType");
            }

            // Формируем WHERE часть запроса
            if (!conditions.isEmpty()) {
                queryBuilder.append(" WHERE ");
                queryBuilder.append(String.join(" AND ", conditions));
            }

            Query<Cars> query = session.createQuery(queryBuilder.toString(), Cars.class);

            // Устанавливаем параметры
            if (brand != null && !brand.isEmpty()) {
                query.setParameter("brand", brand);
            }
            if (minPrice != null) {
                query.setParameter("minPrice", minPrice);
            }
            if (maxPrice != null) {
                query.setParameter("maxPrice", maxPrice);
            }
            if (status != null) {
                query.setParameter("status", status);
            }
            if (hasAC != null) {
                query.setParameter("hasAC", hasAC);
            }
            if (color != null && !color.isEmpty()) {
                query.setParameter("color", color);
            }
            if (fuelType != null && !fuelType.isEmpty()) {
                try {
                    TechnicalSpecs.FuelType fuel = TechnicalSpecs.FuelType.valueOf(fuelType.toUpperCase());
                    query.setParameter("fuelType", fuel);
                } catch (IllegalArgumentException e) {
                    // Если невалидный тип топлива, возвращаем пустой результат
                    return new ArrayList<>();
                }
            }

            return query.getResultList();
        }
    }

    @Override
    public Map<String, Long> getCarCountByBrand() {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                    "SELECT c.brand, COUNT(c) FROM Cars c GROUP BY c.brand ORDER BY COUNT(c) DESC",
                    Object[].class
            );

            List<Object[]> results = query.getResultList();
            Map<String, Long> brandCountMap = new HashMap<>();

            for (Object[] result : results) {
                String brand = (String) result[0];
                Long count = (Long) result[1];
                brandCountMap.put(brand, count);
            }

            return brandCountMap;
        }
    }
}