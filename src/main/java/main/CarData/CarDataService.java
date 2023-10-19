package main.CarData;

import main.Car.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CarDataService {
    private final CarDataRepository repository;

    @Autowired
    public CarDataService(CarDataRepository repository) {
        this.repository = repository;
    }

    public Optional <CarData> getByCarId(Long carId){
        return repository.findById(carId);
    }

    public void createNewCarDataRecord(Car car){
        CarData carData = new CarData(car);
        car.setCarData(carData);
        repository.save(carData);
    }
}
