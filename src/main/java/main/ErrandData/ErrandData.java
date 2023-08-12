package main.ErrandData;

import jakarta.persistence.*;
import main.Errand.Errand;
import main.car.Car;

import java.time.LocalDateTime;

enum ErrandStatus{
    WAITING,
    IN_PROGRESS,
    FINISHED
}

@Entity
@Table
public class ErrandData {
    @Id
    @Column(name = "errand_id")
    private Long id;

    private ErrandStatus errandStatus;
    private LocalDateTime errandStartedTimestamp;
    private LocalDateTime errandFinishedTimestamp;
    private LocalDateTime errandDrivingTime;
    private Double errandMileage;
    private Double avgEnergyConsumption;
    private Double avgSpeed;
    private Integer completedPoints;
    //private List<LocalDateTime> routeTimestamps;
    private String allLocations;    //"16-32-24-8-45"

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "errand_id")
    private Errand errand;
}
