package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Cars car;

    @Column(name = "order_date")
    @CreationTimestamp
    private LocalDateTime orderDate;

    @Column(name = "test_drive_required")
    private Boolean testDriveRequired = false;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.processing;

    public enum Status {
       processing, awaiting_delivery, in_stock, test_drive, completed, cancelled;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
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