package main.CarData;

import main.Car.Car;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarDataService {
    public static CarDataRepository repository;

    public CarDataService(CarDataRepository repository) {
        this.repository = repository;
    }

    static Optional <CarData> getByCarId(Long carId){
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
                () -> new IllegalStateException("Car data with that ID does not exist")
        );
        if(overallMileage!=null){
            if(overallMileage>1500000) throw new IllegalStateException("Car mileage cannot be higher than 1 500 000 km");
            else manipulatedData.setOverallMileage(overallMileage);
        }
        else throw new IllegalStateException("Car mileage needs to be provided in order to update car data");
    }

    public static void deleteCarDataRecord(Long carId){
        if(getByCarId(carId).isPresent()) {
            repository.deleteById(carId);
        }
        else throw new IllegalStateException("Car data with that ID does not exist");
    }
}
