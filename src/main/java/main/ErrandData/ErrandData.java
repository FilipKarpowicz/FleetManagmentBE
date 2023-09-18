package main.ErrandData;

import jakarta.persistence.*;
import main.Errand.Errand;

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
    private String allLocations;    //"16-32-24-8-45"

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "errand_id")
    private Errand errand;

    public ErrandData(Long id, ErrandStatus errandStatus, LocalDateTime errandStartedTimestamp, LocalDateTime errandFinishedTimestamp, LocalDateTime errandDrivingTime, Double errandMileage, Double avgEnergyConsumption, Double avgSpeed, Integer completedPoints, String allLocations, Errand errand) {
        this.id = id;
        this.errandStatus = errandStatus;
        this.errandStartedTimestamp = errandStartedTimestamp;
        this.errandFinishedTimestamp = errandFinishedTimestamp;
        this.errandDrivingTime = errandDrivingTime;
        this.errandMileage = errandMileage;
        this.avgEnergyConsumption = avgEnergyConsumption;
        this.avgSpeed = avgSpeed;
        this.completedPoints = completedPoints;
        this.allLocations = allLocations;
        this.errand = errand;
    }

    public ErrandData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErrandStatus getErrandStatus() {
        return errandStatus;
    }

    public void setErrandStatus(ErrandStatus errandStatus) {
        this.errandStatus = errandStatus;
    }

    public LocalDateTime getErrandStartedTimestamp() {
        return errandStartedTimestamp;
    }

    public void setErrandStartedTimestamp(LocalDateTime errandStartedTimestamp) {
        this.errandStartedTimestamp = errandStartedTimestamp;
    }

    public LocalDateTime getErrandFinishedTimestamp() {
        return errandFinishedTimestamp;
    }

    public void setErrandFinishedTimestamp(LocalDateTime errandFinishedTimestamp) {
        this.errandFinishedTimestamp = errandFinishedTimestamp;
    }

    public LocalDateTime getErrandDrivingTime() {
        return errandDrivingTime;
    }

    public void setErrandDrivingTime(LocalDateTime errandDrivingTime) {
        this.errandDrivingTime = errandDrivingTime;
    }

    public Double getErrandMileage() {
        return errandMileage;
    }

    public void setErrandMileage(Double errandMileage) {
        this.errandMileage = errandMileage;
    }

    public Double getAvgEnergyConsumption() {
        return avgEnergyConsumption;
    }

    public void setAvgEnergyConsumption(Double avgEnergyConsumption) {
        this.avgEnergyConsumption = avgEnergyConsumption;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Integer getCompletedPoints() {
        return completedPoints;
    }

    public void setCompletedPoints(Integer completedPoints) {
        this.completedPoints = completedPoints;
    }

    public String getAllLocations() {
        return allLocations;
    }

    public void setAllLocations(String allLocations) {
        this.allLocations = allLocations;
    }

    public Errand getErrand() {
        return errand;
    }

    public void setErrand(Errand errand) {
        this.errand = errand;
    }
}
