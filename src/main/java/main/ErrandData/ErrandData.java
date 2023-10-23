package main.ErrandData;

import jakarta.persistence.*;
import main.Errand.Errand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import main.ErrandData.ErrandStatus;

@Entity
@Table
public class ErrandData {
    @Id
    @Column(name = "errand_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ErrandStatus errandStatus;
    private LocalDateTime errandStartedTimestamp;   //UTC
    private LocalDateTime errandLastTimestamp;  //UTC
    private Double errandStartedMileage;
    private Double errandLastMileage;
    private Double errandStartedBatteryEnergy;
    private Double errandLastBatteryEnergy;
    private String allLocations;    //"16-32-24-8-45"

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "errand_id")
    private Errand errand;

    public ErrandData(String id, ErrandStatus errandStatus, LocalDateTime errandStartedTimestamp, LocalDateTime errandLastTimestamp, Double errandStartedMileage, Double errandLastMileage, Double errandStartedBatteryEnergy, Double errandLastBatteryEnergy, String allLocations, Errand errand) {
        this.id = id;
        this.errandStatus = errandStatus;
        this.errandStartedTimestamp = errandStartedTimestamp;
        this.errandLastTimestamp = errandLastTimestamp;
        this.errandStartedMileage = errandStartedMileage;
        this.errandLastMileage = errandLastMileage;
        this.errandStartedBatteryEnergy = errandStartedBatteryEnergy;
        this.errandLastBatteryEnergy = errandLastBatteryEnergy;
        this.allLocations = allLocations;
        this.errand = errand;
    }

    public ErrandData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public LocalDateTime getErrandLastTimestamp() {
        return errandLastTimestamp;
    }

    public void setErrandLastTimestamp(LocalDateTime errandLastTimestamp) {
        this.errandLastTimestamp = errandLastTimestamp;
    }

    public Double getErrandStartedMileage() {
        return errandStartedMileage;
    }

    public void setErrandStartedMileage(Double errandStartedMileage) {
        this.errandStartedMileage = errandStartedMileage;
    }

    public Double getErrandLastMileage() {
        return errandLastMileage;
    }

    public void setErrandLastMileage(Double errandLastMileage) {
        this.errandLastMileage = errandLastMileage;
    }

    public Double getErrandStartedBatteryEnergy() {
        return errandStartedBatteryEnergy;
    }

    public void setErrandStartedBatteryEnergy(Double errandStartedBatteryEnergy) {
        this.errandStartedBatteryEnergy = errandStartedBatteryEnergy;
    }

    public Double getErrandLastBatteryEnergy() {
        return errandLastBatteryEnergy;
    }

    public void setErrandLastBatteryEnergy(Double errandLastBatteryEnergy) {
        this.errandLastBatteryEnergy = errandLastBatteryEnergy;
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
