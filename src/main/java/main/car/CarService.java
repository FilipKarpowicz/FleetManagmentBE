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

    }
}
