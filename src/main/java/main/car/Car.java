package main.car;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

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
    private Long id;
    private String  brand;
    private String model;
    private String comment;
    private LocalDate review_to;
    @Transient
    private Integer days_to_review;

    public Car(Long id, String brand,
               String model, String comment,
               LocalDate review_to) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.comment = comment;
        this.review_to = review_to;
    }

    public Car(String brand,
               String model, String comment,
               LocalDate review_to) {
        this.brand = brand;
        this.model = model;
        this.comment = comment;
        this.review_to = review_to;
    }

    public Car() {

    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getReview_to() {
        return review_to;
    }

    public void setReview_to(LocalDate review_to) {
        this.review_to = review_to;
    }

    public Integer getDays_to_review() {
        return Math.toIntExact(ChronoUnit.DAYS.between(LocalDate.now(), this.review_to));
    }

    public void setDays_to_review(Integer days_to_review) {
        this.days_to_review = days_to_review;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", comment='" + comment + '\'' +
                ", serwis_to=" + review_to + '\'' +
                ", days_to_review='" + days_to_review +
                '}';
    }
}
