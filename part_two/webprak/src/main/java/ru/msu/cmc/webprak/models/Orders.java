package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Cars car;

    @Column(nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    private boolean testDriveRequired;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PROCESSING;

    public enum Status {
        PROCESSING, AWAITING_DELIVERY, IN_STOCK, TEST_DRIVE, COMPLETED, CANCELLED
    }

    @Override
    public Long getId() {
        return orderId;
    }

    @Override
    public void setId(Long id) {
        this.orderId = id;
    }
}
