package main.Log;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table
public class Log {
    @Id
    private LocalDate timestamp;
    private String devId;
    private Double longitude;
    private Double latitude;
    private Double altitude;
    private Integer battPer;
    private Float mileage;
    private Integer deliveryAddressId;

    public Log(LocalDate timestamp, String devId, Double longitude, Double latitude, Double altitude, Integer battPer, Float mileage, Integer deliveryAddressId) {
        this.timestamp = timestamp;
        this.devId = devId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.battPer = battPer;
        this.mileage = mileage;
        this.deliveryAddressId = deliveryAddressId;
    }

    @Override
    public String toString() {
        return "Log{" +
                "timestamp=" + timestamp +
                ", devId='" + devId + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", battPer=" + battPer +
                ", mileage=" + mileage +
                ", deliveryAddressId=" + deliveryAddressId +
                '}';
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public String getDevId() {
        return devId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public Integer getBattPer() {
        return battPer;
    }

    public Float getMileage() {
        return mileage;
    }

    public Integer getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public void setBattPer(Integer battPer) {
        this.battPer = battPer;
    }

    public void setMileage(Float mileage) {
        this.mileage = mileage;
    }

    public void setDeliveryAddressId(Integer deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }
}
