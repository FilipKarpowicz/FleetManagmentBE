package main.Car;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import main.CarData.CarData;

import java.time.LocalDate;

@Entity
@Table
public class Car {
    @Id
    @SequenceGenerator(
            name = "car_sequence",
            sequenceName = "car_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "car_sequence"
    )
    private Long carId;
    private String make;
    private String model;
    private String vin;
    private String plateNo;
    private String type;
    private String comment;
    private LocalDate serviceDate;
    private Long serviceMileage;
    private Double battNominalCapacity;   //Ah

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private CarData carData;

    public Car() {

    }

    @Override
    public String toString() {
        return "Car{" +
                "carId=" + carId +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", vin='" + vin + '\'' +
                ", plateNo='" + plateNo + '\'' +
                ", type='" + type + '\'' +
                ", comment='" + comment + '\'' +
                ", serviceDate=" + serviceDate +
                ", serviceMileage=" + serviceMileage +
                '}';
    }

    public Car(String make, String model, String vin, String plateNo, String type, String comment, LocalDate serviceDate, Long serviceMileage, Double battNominalCapacity) {
        this.make = make;
        this.model = model;
        this.vin = vin;
        this.plateNo = plateNo;
        this.type = type;
        this.comment = comment;
        this.serviceDate = serviceDate;
        this.serviceMileage = serviceMileage;
        this.battNominalCapacity = battNominalCapacity;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public Long getServiceMileage() {
        return serviceMileage;
    }

    public void setServiceMileage(Long serviceMileage) {
        this.serviceMileage = serviceMileage;
    }

    public void setCarData(CarData carData) {
        this.carData = carData;
    }

    public Double getBattNominalCapacity() {
        return battNominalCapacity;
    }

    public void setBattNominalCapacity(Double battNominalCapacity) {
        this.battNominalCapacity = battNominalCapacity;
    }
}