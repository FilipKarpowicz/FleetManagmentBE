package main.ErrandData;

import main.Car.Car;
import main.Car.CarService;
import main.CarData.CarData;
import main.CarData.CarDataRepository;
import main.CarData.CarDataService;
import main.Errand.Errand;
import main.Errand.ErrandService;
import main.Location.Location;
import main.Location.LocationService;
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

    private LocationService locationService;

    private CarDataRepository carDataRepository;

    public ErrandDataService(ErrandDataRepository repository, ErrandService errandService, CarService carService, CarDataRepository carDataRepository,LocationService locationService) {
        this.repository = repository;
        this.errandService = errandService;
        this.carService = carService;
        this.carDataRepository = carDataRepository;
        this.locationService = locationService;
    }

    public ResponseEntity<Object> getCalculatedDataByErrandId(String errandId) {
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<>();
        Optional<ErrandData> maybeErrandData = repository.findById(errandId);

        Double avgEnergyConsumption = null;
        String errandDrivingTimeString = null;
        Double errandMileage = null;
        Double avgSpeed = null;

        if (maybeErrandData.isEmpty()) {
            response.put("message", "Brak danych w bazie dla id zlecenia " + errandId);
            response.put("status", "record-not-found-0006");
            data.put("errandMileage", null);
            data.put("errandDrivingTime", null);
            data.put("errandStartedTimestamp", null);
            data.put("avgSpeed", null);
            data.put("avgEnergyConsumption", null);
            data.put("errandStatus", null);
            response.put("data", null);
        } else {
            ErrandData errandData = maybeErrandData.get();
            if (errandData.getErrandLastMileage() != null && errandData.getErrandStartedMileage() != null) {
                errandMileage = errandData.getErrandLastMileage() - errandData.getErrandStartedMileage();
                if (errandMileage <= 0) errandMileage = (double) 0;
                if (errandData.getErrandLastTimestamp() != null && errandData.getErrandStartedTimestamp() != null) {
                    if (ChronoUnit.SECONDS.between(errandData.getErrandStartedTimestamp(), errandData.getErrandLastTimestamp()) != 0) {
                        avgSpeed = errandMileage / ((double) ChronoUnit.SECONDS.between(errandData.getErrandStartedTimestamp(), errandData.getErrandLastTimestamp()) / 3600);   //km/h
                    }
                }
            }
            errandDrivingTimeString = caluclateErrandDrivingTime(errandId);
            avgEnergyConsumption = calculateErrandAvgEnergyConsumption(errandId);

            data.put("errandMileage", roundPlaces(errandMileage, 2));
            data.put("errandDrivingTime", errandDrivingTimeString);
            data.put("errandStartedTimestamp", errandData.getErrandStartedTimestamp());
            data.put("avgSpeed", roundPlaces(avgSpeed, 2));
            data.put("avgEnergyConsumption", roundPlaces(avgEnergyConsumption, 2));
            data.put("errandStatus", errandData.getErrandStatus());
            response.put("message", "Dane przekazane poprawnie");
            response.put("status", "success");
            response.put("data", data);

            if (errandData.getErrandStatus() == ErrandStatus.WAITING) {
                response.put("message", "Zlecenie ma status OCZEKUJĄCE, nie można policzyć danych");
                response.put("status", "success");
            }
            else if (errandData.getErrandLastMileage() == null || errandData.getErrandLastBatteryEnergy() == null || errandData.getErrandLastTimestamp() == null) {
                response.put("message", "Brak aktualnych danych zlecenia, nie można policzyć danych");
                response.put("status", "errand-last-conditions-unknown");
            }
        }
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    private Object roundPlaces(Double value, int places) {
        if (value == null) return null;
        else return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public Double calculateErrandAvgEnergyConsumption(String errandId) {
        ErrandData errandData = getByErrandId(errandId).get();
        Double errandMileage = null;
        if (errandData.getErrandStartedMileage() != null && errandData.getErrandLastMileage() != null) {
            errandMileage = errandData.getErrandLastMileage() - errandData.getErrandStartedMileage();
        }
        if (errandData.getErrandStartedBatteryEnergy() != null && errandData.getErrandLastBatteryEnergy() != null && errandMileage > 0) {
            return (errandData.getErrandStartedBatteryEnergy() - errandData.getErrandLastBatteryEnergy()) / errandMileage;  //Wh/km
        } else return null;
    }

    public String caluclateErrandDrivingTime(String errandId) {
        ErrandData errandData = getByErrandId(errandId).get();
        if (errandData.getErrandLastTimestamp() != null && errandData.getErrandStartedTimestamp() != null) {
            Double hours = (double) ChronoUnit.HOURS.between(errandData.getErrandStartedTimestamp(), errandData.getErrandLastTimestamp());
            Double minutes = (double) ChronoUnit.MINUTES.between(errandData.getErrandStartedTimestamp(), errandData.getErrandLastTimestamp()) - hours * 60;
            Double seconds = (double) ChronoUnit.SECONDS.between(errandData.getErrandStartedTimestamp(), errandData.getErrandLastTimestamp()) - hours * 3600 - minutes * 60;
            return String.format("%02.0f", hours) + ":" + String.format("%02.0f", minutes) + ":" + String.format("%02.0f", seconds);
        } else return null;
    }

    private Optional<ErrandData> getByErrandId(String errandId) {
        return repository.findById(errandId);
    }

    @Transactional
    public ResponseEntity<Object> changeErrandStatus(String errandId, ErrandStatus newStatus) {
        Map<String, Object> response = new HashMap<String, Object>();
        Optional<ErrandData> maybeManipulatedRecord = getByErrandId(errandId);
        Optional<Errand> maybeErrand = errandService.getByErrandId(errandId);
        if (maybeManipulatedRecord.isPresent() && maybeErrand.isPresent()) {
            ErrandData manipulatedRecord = maybeManipulatedRecord.get();
            Errand errand = maybeErrand.get();
            if (newStatus == ErrandStatus.IN_PROGRESS) {
                if(getActiveErrandDataForDrvId(errand.getDrvId()) == null && getActiveErrandDataForCarId(errand.getCarId()) == null) {
                    Optional<Car> maybeCar = carService.getCarById(errand.getCarId());
                    Optional<CarData> maybeCarData = carDataRepository.findById(errand.getCarId());
                    if (maybeCar.isPresent() && maybeCarData.isPresent()) {
                        CarData carData = maybeCarData.get();
                        Car car = maybeCar.get();
                        if (carData.getBattSoh() != null && car.getBattNominalCapacity() != null && carData.getOverallMileage() != null && carData.getBattSoc() != null && carData.getBattVoltage() != null) {
                            manipulatedRecord.setErrandStartedTimestamp(LocalDateTime.now());
                            manipulatedRecord.setErrandStartedMileage(carData.getOverallMileage());
                            manipulatedRecord.setErrandStartedBatteryEnergy(car.getBattNominalCapacity() * ((double) carData.getBattSoh() / 100) * ((double) carData.getBattSoc() / 100) * carData.getBattVoltage());
                            manipulatedRecord.setErrandStatus(newStatus);
                            response.put("status", "success");
                            response.put("message", "Status zlecenia nr " + errandId + " został zmieniony na W TRAKCIE");
                        } else {
                            response.put("status", "data-not-found-0020");
                            response.put("message", "Brak wystarczających danych dla pojazdu o numerze ID " + errand.getCarId() + " aby obliczyć parametry początkowe zlecenia. Nie udało się rozpocząć zlecenia");
                        }
                    } else {
                        response.put("status", "data-not-found-0015");
                        response.put("message", "Dane pojazdu o numerze ID " + errand.getCarId() + " nie istnieją w bazie danych. Nie udało się rozpocząć zlecenia");
                    }
                }
                else{
                    if(getActiveErrandDataForCarId(errand.getCarId()) != null){
                        response.put("status", "conflict-0010");
                        response.put("message", "Nie można rozpocząć tego zlecenia, ponieważ pojazd o numerze ID " + errand.getCarId() + " uczestniczy już w innym, aktywnym zleceniu (" + getActiveErrandDataForCarId(errand.getCarId()) + ")");
                    }
                    else if(getActiveErrandDataForDrvId(errand.getDrvId()) != null){
                        response.put("status", "conflict-0011");
                        response.put("message", "Nie można rozpocząć tego zlecenia, ponieważ kierowca o numerze ID " + errand.getDrvId() + " uczestniczy już w innym, aktywnym zleceniu (" + getActiveErrandDataForDrvId(errand.getDrvId()) + ")");
                    }
                }
            } else if (newStatus == ErrandStatus.FINISHED) {
                Double errandAvgEnergyConsumption = calculateErrandAvgEnergyConsumption(errandId);
                response.put("status", "success");
                response.put("message", "Status zlecenia nr " + errandId + " został zmieniony na ZAKOŃCZONE");
                if(errandAvgEnergyConsumption != null) {
                    if(setCarAvgConsumption(errand.getCarId(), errandAvgEnergyConsumption) != "success") {
                        response.put("status", "data-not-found-0016");
                        response.put("message", "Pojazd o numerze ID " + errand.getCarId() + ", przypisany do tego zlecenia, nie istnieje w bazie danych. Nie udało się zakończyć zlecenia");
                    }
                    else manipulatedRecord.setErrandStatus(newStatus);
                }
                else manipulatedRecord.setErrandStatus(newStatus);
            }
        } else {
            response.put("status", "record-not-found-0014");
            response.put("message", "Zlecenie o numerze ID " + errandId + " nie istnieje w bazie danych. Nie udało się zakończyć zlecenia");
        }
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @Transactional
    public String setCarAvgConsumption(Long carId, Double lastErrandAvgEnergyConsumption) {
        Optional<CarData> maybeManipulatedData = carDataRepository.findById(carId);
        if(maybeManipulatedData.isEmpty()) return "failed";
        else {
            CarData manipulatedData = maybeManipulatedData.get();
            manipulatedData.setLastErrandAvgEnergyConsumption(lastErrandAvgEnergyConsumption);
            return "success";
        }
    }

    public List<ErrandData> getAll(Integer batchNumber) {
        List<ErrandData> allData = repository.findAll();

        Integer startIndex = batchNumber * 15 - 15;
        Integer endIndex = batchNumber * 15;

        if (allData.size() > startIndex + endIndex && allData.size() > startIndex) {
            return allData.subList(startIndex, endIndex);
        } else if (allData.size() > startIndex) {
            return allData.subList(startIndex, allData.size());
        } else {
            throw new IllegalStateException("Batch is empty");
        }
    }

    public String getActiveErrandDataForCarId(Long carId) {
        Optional<ErrandData> activeErrandData = null;
        List<Errand> carErrandList = errandService.getByCarId(carId);

        for (Errand errand : carErrandList) {
            Optional<ErrandData> errandData = getByErrandId(errand.getErrandId());
            if (errandData.isPresent()) {
                if (errandData.get().getErrandStatus() == ErrandStatus.IN_PROGRESS) activeErrandData = errandData;
            }
        }

        if (activeErrandData != null) return activeErrandData.get().getId();
        else return null;
    }

    public String getActiveErrandDataForDrvId(Long drvId) {
        Optional<ErrandData> activeErrandData = null;
        List<Errand> driverErrandList = errandService.getByDrvId(drvId);

        for (Errand errand : driverErrandList) {
            Optional<ErrandData> errandData = getByErrandId(errand.getErrandId());
            if (errandData.isPresent()) {
                if (errandData.get().getErrandStatus() == ErrandStatus.IN_PROGRESS) activeErrandData = errandData;
            }
        }

        if (activeErrandData != null) return activeErrandData.get().getId();
        else return null;
    }


    public ResponseEntity<Object> findRoute(String errandId) {
        Optional<ErrandData> potentialErrand = repository.findById(errandId);
        if (potentialErrand.isPresent()) {
            if(potentialErrand.get().getAllLocations() != null) {
                ResponseEntity<Object> route = locationService.findLocationList(potentialErrand.get().getAllLocations());
                return route;
            }
            else{
                Map<String, Object> response = new HashMap<String, Object>();
                response.put("status", "record-not-found-0009");
                response.put("message", "Brak lokalizacji dla zlecenia o numerze ID " + errandId);
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
        } else {
            Map<String, Object> response = new HashMap<String, Object>();
            response.put("status", "record-not-found-0008");
            response.put("message", "Brak danych dla zlecenia o numerze ID " + errandId);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }
}
