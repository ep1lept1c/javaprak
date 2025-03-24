package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "consumer_specs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerSpecs implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "car_id")
    private Cars car;

    private String interiorMaterial = "fabric";

    @Column(nullable = false, length = 100)
    private String color;

    private boolean hasAirConditioning = false;
    private boolean hasMultimedia = false;
    private boolean hasGps = false;

    @Override
    public Long getId() {
        return carId;
    }

    @Override
    public void setId(Long id) {
        this.carId = id;
    }
}
