package main.CarData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import main.Car.Car;

import java.time.LocalDateTime;

@Entity
@Table
//@JsonIgnoreProperties(value = "car")
public class CarData {
    @Id
    @Column(name = "car_id")
    private Long id;
    private Double overallMileage;
    private Integer battSoc;
    private Integer battSoh;
    private Double battVoltage;
    private LocalDateTime lastUpdate;   //UTC
    private Long lastLocation;  //locationId
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "car_id")
    private Car car;

    public CarData(Double overallMileage, Integer battSoc, Integer battSoh, Double battVoltage, LocalDateTime lastUpdate, Long lastLocation, Car car){
        this.overallMileage = overallMileage;
        this.battSoc = battSoc;
        this.battSoh = battSoh;
        this.battVoltage = battVoltage;
        this.lastUpdate = lastUpdate;
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

    public Double getBattVoltage() {
        return battVoltage;
    }

    public void setBattVoltage(Double battVoltage) {
        this.battVoltage = battVoltage;
    }
}
