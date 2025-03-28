package ru.msu.cmc.webprak.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "consumerspecs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerSpecs implements BaseEntity<Long> {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "car_id")
    private Cars car;

    @Column(name = "interior_material")
    private String interiorMaterial = "fabric";

    @Column(name = "color", nullable = false, length = 100)
    private String color;

    @Column(name = "has_air_conditioning")
    private Boolean hasAirConditioning = false;

    @Column(name = "has_multimedia")
    private Boolean hasMultimedia = false;

    @Column(name = "has_gps")
    private Boolean hasGps = false;

    @Override
    public Long getId() {
        return carId;
    }

    @Override
    public void setId(Long id) {
        this.carId = id;
    }
}