package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "testdrives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestDrives implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_drive_id")
    private Long testDriveId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Cars car;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private Status status = Status.pending;

    @Column(name = "notes")
    private String notes;

    public enum Status {
       pending, completed, cancelled;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
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