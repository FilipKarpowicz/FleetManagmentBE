package main.CarData;

import main.car.Car;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarDataService {
    public static CarDataRepository repository;

    public CarDataService(CarDataRepository repository) {
        this.repository = repository;
    }

    Optional <CarData> getByCarId(Long carId){
        return repository.findById(carId);
    }

    public static void createNewCarDataRecord(Car car){
        CarData carData = new CarData(car);
        car.setCarData(carData);
        repository.save(carData);
    }

    @Transactional
    public void editCarData(Long carId, Double overallMileage){
        CarData manipulatedData = getByCarId(carId).orElseThrow(
                () -> new IllegalStateException("CarData with that id does not exist")
        );
        manipulatedData.setOverallMileage(overallMileage);
    }

    public static void deleteCarDataRecord(Long carId){
        repository.deleteById(carId);
    }
}
