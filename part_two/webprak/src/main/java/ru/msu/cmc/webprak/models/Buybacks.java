package ru.msu.cmc.webprak.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "buybacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Buybacks implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyback_id")
    private Long buybackId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "car_brand", nullable = false)
    private String carBrand;

    @Column(name = "car_year", nullable = false)
    private Integer carYear;

    @Column(name = "mileage", nullable = false)
    private Integer mileage;

    @Column(name = "photos", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String photos;

    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(name = "estimated_price", precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "created_at", updatable = true)
    private LocalDateTime createdAt;

    public enum Status {
        PENDING, ACCEPTED, REJECTED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Override
    public Long getId() {
        return buybackId;
    }

    @Override
    public void setId(Long id) {
        this.buybackId = id;
    }
}