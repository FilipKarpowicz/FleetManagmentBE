package main.car;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }


    public List<Car> getCars() {
        return carRepository.findAll();
    }

    public void addNewCar(Car car) {
        Optional<Car> carOptional = carRepository.findCarByBrand(car.getBrand());
        if (carOptional.isPresent()){
            throw new IllegalStateException("Brand Taken");
        }
        carRepository.save(car);
    }

    public void deleteCar(Long carId) {
        boolean exists = carRepository.existsById(carId);
        if (!exists) {
            throw new IllegalStateException("Car with id" + carId + "does not exists");
        }
        carRepository.deleteById(carId);
    }

    @Transactional
    public void updateCar(Long carId, String brand, String model, String comment, String reviewTo) {
        Car car = carRepository.findById(carId).orElseThrow(
                () -> new IllegalStateException("Car with id" + carId + "does not exists"));

        System.out.println(car.toString());
        if(brand != null & !Objects.equals(car.getBrand(),brand)){
            car.setBrand(brand);
        }

        if(model != null & !Objects.equals(car.getModel(),model)){
            car.setModel(model);
        }
        if(comment != null & !Objects.equals(car.getComment(),comment)){
            car.setComment(comment);
        }
        if(reviewTo != null & !Objects.equals(car.getReview_to(),reviewTo)){
            car.setReview_to(LocalDate.parse(reviewTo));
        }
    }
}
