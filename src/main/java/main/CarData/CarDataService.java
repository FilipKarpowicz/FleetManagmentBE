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
        Map<String, Object> data = new HashMap<>();

        Optional<CarData> maybeCarData = getByCarId(carId);
        Optional<Car> maybeCar = carRepository.findById(carId);

        if(maybeCarData.isEmpty()){
            response.put("status", "record-not-found-0004");
            response.put("message", "Brak informacji w tabeli CarData dla ID pojazdu " + carId);
            data.put("overallMileage", null);
            data.put("battSoc", null);
            data.put("battSoh", null);
            data.put("battVoltage", null);
            data.put("lastUpdate", null);
            data.put("lastLocationId", null);
            data.put("remainingRange", null);
            response.put("data", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if(maybeCar.isEmpty()){
            response.put("status", "record-not-found-0005");
            response.put("message", "Brak informacji w tabeli Car dla ID pojazdu " + carId);
            data.put("overallMileage", null);
            data.put("battSoc", null);
            data.put("battSoh", null);
            data.put("battVoltage", null);
            data.put("lastUpdate", null);
            data.put("lastLocationId", null);
            data.put("remainingRange", null);
            response.put("data", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            Car car = maybeCar.get();
            CarData carData = maybeCarData.get();

            Double battEnergy = car.getBattNominalCapacity() * ((double) carData.getBattSoh() / 100) * ((double) carData.getBattSoc() / 100) * carData.getBattVoltage();
            String activeErrandId = errandDataService.getActiveErrandDataForCarId(carId);
            Double remainingRange = null;

            if (activeErrandId != null) {
                if (errandDataService.calculateErrandAvgEnergyConsumption(activeErrandId) != null) {
                    remainingRange = battEnergy / errandDataService.calculateErrandAvgEnergyConsumption(activeErrandId);
                }
            } else {
                if (carData.getLastErrandAvgEnergyConsumption() != null && carData.getLastErrandAvgEnergyConsumption() > 0) {
                    remainingRange = battEnergy / carData.getLastErrandAvgEnergyConsumption();
                }
            }

            data.put("overallMileage", roundPlaces(carData.getOverallMileage(), 2));
            data.put("battSoc", carData.getBattSoc());
            data.put("battSoh", carData.getBattSoh());
            data.put("battVoltage", carData.getBattVoltage());
            data.put("lastUpdate", carData.getLastUpdate());
            data.put("lastLocationId", carData.getLastLocation());
            data.put("remainingRange", roundPlaces(remainingRange, 2));
            response.put("status", "success");
            response.put("message", "Dane przekazane poprawnie");
            response.put("data", data);

            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
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
