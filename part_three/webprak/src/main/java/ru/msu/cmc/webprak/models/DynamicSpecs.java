package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "dynamicspecs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DynamicSpecs implements BaseEntity<Long> {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "car_id")
    private Cars car;

    @Column(name = "mileage")
    private Integer mileage = 0;

    @Column(name = "last_service", nullable = false)
    private LocalDate lastService;

    @Column(name = "test_drive_count")
    private Integer testDriveCount = 0;

    @Override
    public Long getId() {
        return carId;
    }

    @Override
    public void setId(Long id) {
        this.carId = id;
    }
}