package main.Location;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Table
public class Location {
    @Id
    @SequenceGenerator(
            name = "location_sequence",
            sequenceName = "location_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "location_sequence"
    )
    private Long locationId;
    @Column(columnDefinition = "geography")
    private Point point;
    private String realAddress;
    private LocalDateTime arrivalTime;   //UTC

    public Location(Point point, String realAddress, LocalDateTime arrivalTime) {
        this.point = point;
        this.realAddress = realAddress;
        this.arrivalTime = arrivalTime;
    }

    public Location() {
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getRealAddress() {
        return realAddress;
    }

    public void setRealAddress(String realAddress) {
        this.realAddress = realAddress;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationId=" + locationId +
                ", point=" + point +
                ", realAddress='" + realAddress + '\'' +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}
