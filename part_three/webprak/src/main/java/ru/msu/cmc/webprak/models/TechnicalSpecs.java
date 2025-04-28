package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "technicalspecs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalSpecs implements BaseEntity<Long> {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "car_id")
    private Cars car;

    @Column(name = "engine_volume", nullable = false, precision = 4, scale = 2)
    private BigDecimal engineVolume;

    @Column(name = "power", nullable = false)
    private Integer power;

    @Column(name = "fuel_consumption", precision = 5, scale = 2)
    private BigDecimal fuelConsumption;

    @Column(name = "doors")
    private Integer doors = 4;

    @Column(name = "seats")
    private Integer seats = 5;

    @Column(name = "automatic_transmission")
    private Boolean automaticTransmission = false;

    @Column(name = "fuel_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column(name = "cruise_control")
    private Boolean cruiseControl = false;

    public enum FuelType {
        petrol, diesel, electric;

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
}