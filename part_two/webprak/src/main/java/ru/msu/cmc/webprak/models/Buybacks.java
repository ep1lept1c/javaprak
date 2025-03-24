package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;

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
    private Long buybackId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 255)
    private String carBrand;

    @Column(nullable = false)
    private int carYear;

    @Column(nullable = false)
    private int mileage;

    @Column(columnDefinition = "jsonb")
    private String photos;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private BigDecimal estimatedPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        PENDING, ACCEPTED, REJECTED
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
