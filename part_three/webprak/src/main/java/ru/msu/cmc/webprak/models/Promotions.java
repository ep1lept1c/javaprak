package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotions implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "discount", precision = 5, scale = 2)
    private BigDecimal discount;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "promotions",cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Cars> cars = new HashSet<>();

    @Override
    public Long getId() {
        return promotionId;
    }

    @Override
    public void setId(Long id) {
        this.promotionId = id;
    }

    // Вспомогательные методы для работы с коллекцией cars
    public void addCar(Cars car) {
        this.cars.add(car);
        car.getPromotions().add(this);
    }

    public void removeCar(Cars car) {
        this.cars.remove(car);
        car.getPromotions().remove(this);
    }
}