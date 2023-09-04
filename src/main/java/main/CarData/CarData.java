package main.CarData;

import jakarta.persistence.*;
import main.Car.Car;

import java.time.LocalDateTime;

@Entity
@Table
public class CarData {
    @Id
    @Column(name = "car_id")
    private Long id;
    private Double avgEnergyConsumption;
    private Double overallMileage;
    private Integer battSoc;
    private Integer battSoh;
    private LocalDateTime lastUpdate;
    private Integer remainingRange;
    private Long lastLocation;  //locationId
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "car_id")
    private Car car;

    public CarData(Double overallMileage, Integer battSoc, Integer battSoh, LocalDateTime lastUpdate, Integer remainingRange, Long lastLocation, Car car){
        this.overallMileage = overallMileage;
        this.battSoc = battSoc;
        this.battSoh = battSoh;
        this.lastUpdate = lastUpdate;
        this.remainingRange = remainingRange;
        this.lastLocation = lastLocation;
        this.car = car;
    }

    public CarData(Car car) {
        this.car = car;
    }

    public CarData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAvgEnergyConsumption() {
        return avgEnergyConsumption;
    }

    public void setAvgEnergyConsumption(Double avgEnergyConsumption) {
        this.avgEnergyConsumption = avgEnergyConsumption;
    }

    public Double getOverallMileage() {
        return overallMileage;
    }

    public void setOverallMileage(Double overallMileage) {
        this.overallMileage = overallMileage;
    }

    public Integer getBattSoc() {
        return battSoc;
    }

    public void setBattSoc(Integer battSoc) {
        this.battSoc = battSoc;
    }

    public Integer getBattSoh() {
        return battSoh;
    }

    public void setBattSoh(Integer battSoh) {
        this.battSoh = battSoh;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getRemainingRange() {
        return remainingRange;
    }

    public void setRemainingRange(Integer remainingRange) {
        this.remainingRange = remainingRange;
    }

    public Long getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Long lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
