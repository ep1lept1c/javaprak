package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "technical_specs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalSpecs implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "car_id")
    private Cars car;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal engineVolume;

    @Column(nullable = false)
    private int power;

    @Column(precision = 5, scale = 2)
    private BigDecimal fuelConsumption;

    private int doors = 4;
    private int seats = 5;
    private boolean automaticTransmission = false;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private boolean cruiseControl = false;

    public enum FuelType {
        PETROL, DIESEL, ELECTRIC
    }

    @Override
    public Long getId() {
        return carId;
    }

    @Override
    public void setId(Long id) {
        this.carId = id;
    }
}
