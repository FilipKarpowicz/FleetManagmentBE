package main.Driver;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table
public class Driver {

    @Id
    @SequenceGenerator(
            name = "driver_sequence",
            sequenceName = "driver_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "driver_sequence"
    )
    private Long drvId;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private Long pesel;
    private String drvLicNo;
    private Integer overallDrvRating;

    private Long carId;


    public Driver() {

    }

    @Override
    public String toString() {
        return "Driver{" +
                "drvId=" + drvId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthdate=" + birthdate +
                ", pesel=" + pesel +
                ", drvLicNo='" + drvLicNo + '\'' +
                ", carId=" + carId +
                ", overallDrvRating=" + overallDrvRating +
                '}';
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Long getPesel() {
        return pesel;
    }

    public void setPesel(Long pesel) {
        this.pesel = pesel;
    }

    public String getDrvLicNo() {
        return drvLicNo;
    }

    public void setDrvLicNo(String drvLicNo) {
        this.drvLicNo = drvLicNo;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Integer getOverallDrvRating() {
        return overallDrvRating;
    }

    public void setOverallDrvRating(Integer overallDrvRating) {
        this.overallDrvRating = overallDrvRating;
    }

    public Long getDrvId() {
        return drvId;
    }

    public void setDrvId(Long drvId) {
        this.drvId = drvId;
    }
}
