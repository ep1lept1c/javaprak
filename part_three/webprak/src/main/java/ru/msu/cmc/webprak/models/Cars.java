package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cars implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long carId;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "registration_number", nullable = false, unique = true, length = 50)
    private String registrationNumber;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private Status status = Status.available;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    private TechnicalSpecs technicalSpecs;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    private ConsumerSpecs consumerSpecs;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    private DynamicSpecs dynamicSpecs;

    @OneToMany(mappedBy = "car")
    private Set<Orders> orders;

    @OneToMany(mappedBy = "car")
    private Set<TestDrives> testDrives;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "carspromotions",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private Set<Promotions> promotions = new HashSet<>();

    public enum Status {
        available, reserved, sold;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Override
    public Long getId() {
        return carId;
    }

    @Override
    public void setId(Long id) {
        this.carId = id;
    }

    // Вспомогательные методы для работы с коллекцией promotions
    public void addPromotion(Promotions promotion) {
        this.promotions.add(promotion);
        promotion.getCars().add(this);
    }

    public void removePromotion(Promotions promotion) {
        this.promotions.remove(promotion);
        promotion.getCars().remove(this);
    }
}