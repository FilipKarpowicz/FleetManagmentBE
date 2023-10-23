package main.CarData;

import main.Car.Car;
import main.Car.CarRepository;
import main.Car.CarService;
import main.ErrandData.ErrandDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.DoublePredicate;

@Service
public class CarDataService {
    private final CarDataRepository repository;

    private CarRepository carRepository;
    private ErrandDataService errandDataService;

    @Autowired
    public CarDataService(CarDataRepository repository, CarRepository carRepository, ErrandDataService errandDataService) {
        this.repository = repository;
        this.carRepository = carRepository;
        this.errandDataService = errandDataService;
    }

    public Optional<CarData> getByCarId(Long carId){
        return repository.findById(carId);
    }

    public ResponseEntity<Object> getResponse(Long carId){
        Map<String, Object> response = new HashMap<String, Object>();
        CarData carData = getByCarId(carId).orElseThrow(
                () -> new IllegalStateException("car data with that id does not exist")
        );
        Car car = carRepository.findById(carId).orElseThrow(
                () -> new IllegalStateException("car with that id does not exist")
        );

        Double battEnergy = car.getBattNominalCapacity() * ((double) carData.getBattSoh()/100) * ((double) carData.getBattSoc()/100) * carData.getBattVoltage();
        String activeErrandId = errandDataService.getActiveErrandDataForCarId(carId);
        Double remainingRange = null;
        if(activeErrandId != null) {
            remainingRange = battEnergy / errandDataService.calculateErrandAvgEnergyConsumption(activeErrandId);
        }
        else{
            remainingRange = battEnergy / carData.getLastErrandAvgEnergyConsumption();
        }

        response.put("overallMileage", roundPlaces(carData.getOverallMileage(), 2));
        response.put("battSoc", carData.getBattSoc());
        response.put("battSoh", carData.getBattSoh());
        response.put("battVoltage", carData.getBattVoltage());
        response.put("lastUpdate", carData.getLastUpdate());
        response.put("lastLocationId", carData.getLastLocation());
        response.put("remainingRange", roundPlaces(remainingRange, 2));

        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    private Object roundPlaces(Double value, int places){
        if(value == null) return null;
        else return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    @Transactional
    public void editCarData(Long carId, Double lastErrandAvgEnergyConsumption) {
        CarData manipulatedData = getByCarId(carId).orElseThrow(
                () -> new IllegalStateException("Car data for that id does not exist")
        );
        if(lastErrandAvgEnergyConsumption!=null) manipulatedData.setLastErrandAvgEnergyConsumption(lastErrandAvgEnergyConsumption);
        else throw new IllegalStateException("Energy consumption needs to be provided in order to update car data");
    }
}
