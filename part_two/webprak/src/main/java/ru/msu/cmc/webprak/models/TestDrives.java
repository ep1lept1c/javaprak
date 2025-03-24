package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_drives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestDrives implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testDriveId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Cars car;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private String notes;

    public enum Status {
        PENDING, COMPLETED, CANCELLED
    }

    @Override
    public Long getId() {
        return testDriveId;
    }

    @Override
    public void setId(Long id) {
        this.testDriveId = id;
    }
}
