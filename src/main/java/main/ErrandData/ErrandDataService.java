package main.ErrandData;

import main.Car.Car;
import main.Car.CarService;
import main.CarData.CarData;
import main.CarData.CarDataRepository;
import main.CarData.CarDataService;
import main.Errand.Errand;
import main.Errand.ErrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ErrandDataService {
    private final ErrandDataRepository repository;
    private ErrandService errandService;
    private CarService carService;

    private CarDataRepository carDataRepository;

    public ErrandDataService(ErrandDataRepository repository, ErrandService errandService, CarService carService, CarDataRepository carDataRepository) {
        this.repository = repository;
        this.errandService = errandService;
        this.carService = carService;
        this.carDataRepository = carDataRepository;
    }

    public ResponseEntity<Object> getCalculatedDataByErrandId(Long errandId){
        Map<String, Object> response = new HashMap<String, Object>();
        Optional<ErrandData> maybeErrandData = repository.findById(errandId);

        Double avgEnergyConsumption = null;
        String errandDrivingTimeString = null;
        Double errandMileage = null;
        Double avgSpeed = null;

        if(maybeErrandData.isEmpty()){
            response.put("message", "No data available for this errand ID");
            response.put("status", "DATA NOT AVAILABLE!");
        }
        else{
            ErrandData errandData = maybeErrandData.get();
            if(errandData.getErrandLastMileage() != null && errandData.getErrandStartedMileage() != null){
                errandMileage = errandData.getErrandLastMileage() - errandData.getErrandStartedMileage();
                if(errandMileage <= 0) errandMileage = (double) 0;
                if(errandData.getErrandLastTimestamp() != null && errandData.getErrandStartedTimestamp() != null){
                    if(ChronoUnit.SECONDS.between(errandData.getErrandStartedTimestamp(), errandData.getErrandLastTimestamp()) != 0) {
                        avgSpeed = errandMileage / ((double) ChronoUnit.SECONDS.between(errandData.getErrandStartedTimestamp(), errandData.getErrandLastTimestamp()) / 3600);   //km/h
                    }
                }
            }
            errandDrivingTimeString = caluclateErrandDrivingTime(errandId);
            avgEnergyConsumption = calculateErrandAvgEnergyConsumption(errandId);

            response.put("errandMileage", roundPlaces(errandMileage, 2));
            response.put("errandDrivingTime", errandDrivingTimeString);
            response.put("avgSpeed", roundPlaces(avgSpeed, 2));
            response.put("avgEnergyConsumption", roundPlaces(avgEnergyConsumption, 2));
            response.put("message", "Data calculated successfully");
            response.put("status", "SUCCESS!");
            response.put("errandStatus", errandData.getErrandStatus());

            if(errandData.getErrandStatus()==ErrandStatus.WAITING){
                response.put("message", "Errand status is waiting");
                response.put("status", "DATA NOT CALCULATED!");
            }
            if(errandData.getErrandStartedMileage() == null || errandData.getErrandStartedBatteryEnergy() == null || errandData.getErrandStartedTimestamp() == null){
                response.put("message", "Errand initial conditions are null");
                response.put("status", "DATA NOT FULLY CALCULATED!");
            }
            if(errandData.getErrandLastMileage() == null || errandData.getErrandLastBatteryEnergy() == null || errandData.getErrandLastTimestamp() == null){
                response.put("message", "Errand last conditions are null");
                response.put("status", "DATA NOT FULLY CALCULATED!");
            }
        }
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    private Object roundPlaces(Double value, int places){
        if(value == null) return null;
        else return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public Double calculateErrandAvgEnergyConsumption(Long errandId){
        ErrandData errandData = getByErrandId(errandId).orElseThrow(
                () -> new IllegalStateException("errand data with that id does not exist")
        );
        Double errandMileage = null;
        if(errandData.getErrandStartedMileage() != null && errandData.getErrandLastMileage() != null){
            errandMileage = errandData.getErrandLastMileage() - errandData.getErrandStartedMileage();
        }
        if(errandData.getErrandStartedBatteryEnergy() != null && errandData.getErrandLastBatteryEnergy() != null && errandMileage > 0){
            return (errandData.getErrandStartedBatteryEnergy() - errandData.getErrandLastBatteryEnergy())/errandMileage;  //Wh/km
        }
        else return null;
    }

    public String caluclateErrandDrivingTime(Long errandId){
        ErrandData errandData = getByErrandId(errandId).orElseThrow(
                () -> new IllegalStateException("errand data with that id does not exist")
        );
        if(errandData.getErrandLastTimestamp() != null && errandData.getErrandStartedTimestamp() != null){
            Double hours = (double) ChronoUnit.HOURS.between(errandData.getErrandStartedTimestamp(), errandData.getErrandLastTimestamp());
            Double minutes = (double) ChronoUnit.MINUTES.between(errandData.getErrandStartedTimestamp(), errandData.getErrandLastTimestamp()) - hours*60;
            Double seconds = (double) ChronoUnit.SECONDS.between(errandData.getErrandStartedTimestamp(), errandData.getErrandLastTimestamp()) - hours*3600 - minutes*60;
            return String.format("%02.0f", hours) + ":" + String.format("%02.0f", minutes) + ":" + String.format("%02.0f", seconds);
        }
        else return null;
    }

    private Optional<ErrandData> getByErrandId(Long errandId){
        return repository.findById(errandId);
    }

    @Transactional
    public ResponseEntity<Object> changeErrandStatus(Long errandId, ErrandStatus newStatus){
        Map<String, Object> response = new HashMap<String, Object>();
        Optional<ErrandData> maybeManipulatedRecord = getByErrandId(errandId);
        Optional<Errand> maybeErrand = errandService.getByErrandId(errandId);
        if(maybeManipulatedRecord.isPresent() && maybeErrand.isPresent()){
            ErrandData manipulatedRecord = maybeManipulatedRecord.get();
            Errand errand = maybeErrand.get();
            if(newStatus == ErrandStatus.IN_PROGRESS) {
                Optional<Car> maybeCar = carService.getCarById(errand.getCarId());
                Optional<CarData> maybeCarData = carDataRepository.findById(errand.getCarId());
                if (maybeCar.isPresent() && maybeCarData.isPresent()) {
                    CarData carData = maybeCarData.get();
                    Car car = maybeCar.get();
                    manipulatedRecord.setErrandStartedTimestamp(LocalDateTime.now());
                    manipulatedRecord.setErrandStartedMileage(carData.getOverallMileage());
                    manipulatedRecord.setErrandStartedBatteryEnergy(car.getBattNominalCapacity()*(carData.getBattSoh()/100)*(carData.getBattSoc()/100)*carData.getBattVoltage());
                    response.put("status","SUCCESS");
                    response.put("message","status changed to IN_PROGRESS");
                }
                else{
                    response.put("status","ERROR");
                    response.put("message","Car data for that errand is corrupted");
                }
            }
            else if(newStatus == ErrandStatus.FINISHED) {
                Double errandAvgEnergyConsumption = calculateErrandAvgEnergyConsumption(errandId);
                if(errandAvgEnergyConsumption!=null) editCarData(errand.getCarId(), errandAvgEnergyConsumption);
                response.put("status","SUCCESS");
                response.put("message","status changed to IN_PROGRESS");
            }
            manipulatedRecord.setErrandStatus(newStatus);
        }
        else{
            response.put("status","ERROR");
            response.put("message","Errand data with that ID does not exist");
        }
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @Transactional
    public void editCarData(Long carId, Double lastErrandAvgEnergyConsumption) {
        CarData manipulatedData = carDataRepository.findById(carId).orElseThrow(
                () -> new IllegalStateException("Car data for that id does not exist")
        );
        if(lastErrandAvgEnergyConsumption!=null) manipulatedData.setLastErrandAvgEnergyConsumption(lastErrandAvgEnergyConsumption);
        else throw new IllegalStateException("Energy consumption needs to be provided in order to update car data");
    }

    public List<ErrandData> getAll(Integer batchNumber){
        List<ErrandData> allData = repository.findAll();

        Integer startIndex = batchNumber*15 - 15;
        Integer endIndex = batchNumber*15;

        if(allData.size() > startIndex + endIndex && allData.size() > startIndex){
            return allData.subList(startIndex, endIndex);
        } else if (allData.size() > startIndex) {
            return allData.subList(startIndex, allData.size());
        } else {
            throw new IllegalStateException("Batch is empty");
        }
    }

    public Long getActiveErrandDataForCarId(Long carId){
        Optional<ErrandData> activeErrandData = null;
        List<Errand> carErrandList = errandService.getByCarId(carId);

        for(Errand errand:carErrandList){
            Optional<ErrandData> errandData = getByErrandId(errand.getErrandId());
            if(errandData.isPresent()) {
                if (errandData.get().getErrandStatus() == ErrandStatus.IN_PROGRESS) activeErrandData = errandData;
            }
        }

        if(activeErrandData != null) return activeErrandData.get().getId();
        else return null;
    }
}
