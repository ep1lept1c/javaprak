package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "dynamic_specs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DynamicSpecs implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "car_id")
    private Cars car;

    private int mileage = 0;

    @Column(nullable = false)
    private LocalDate lastService;

    private int testDriveCount = 0;

    @Override
    public Long getId() {
        return carId;
    }

    @Override
    public void setId(Long id) {
        this.carId = id;
    }
}
