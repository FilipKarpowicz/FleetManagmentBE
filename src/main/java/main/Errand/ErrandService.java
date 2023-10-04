package main.Errand;

import main.Car.Car;
import main.Car.CarService;
import main.Driver.Driver;
import main.Driver.DriverService;
import main.ErrandData.ErrandData;
import main.ErrandData.ErrandDataService;
import main.ErrandData.ErrandStatus;
import main.Location.Location;
import main.Location.LocationService;
import org.h2.util.json.JSONArray;
import org.h2.util.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static main.ErrandData.ErrandDataService.getCompletedPointsByErrandId;
import static main.Location.LocationService.getListOfRealAddresses;

@Service
public class ErrandService {
    private final ErrandRepository repository;

    public ErrandService(ErrandRepository repository) {
        this.repository = repository;
    }

    public void addNewErrand(Errand errand){
        if(!isDrvIdValid(errand.getDrvId())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Driver with that ID does not exist");
        else if(!isCarIdValid(errand.getCarId())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Car with that ID does not exist");
        else{
            repository.save(errand);
            ErrandDataService.generateNewDataRecord(errand);
        }
    }

    List<Errand> getByDrvId(Long drvId){
        return repository.findByDrvId(drvId);
    }

    List<Errand> getByCarId(Long carId){
        return repository.findByCarId(carId);
    }

    Optional<Errand> getByErrandId(Long errandId){
        return repository.findByErrandId(errandId);
    }

    private Boolean isRouteValid(String newRoute){
        List<String> stringRoute = new ArrayList<String>(Arrays.asList(newRoute.split("-")));
        Boolean routeValid = true;
        for(String locationIdString : stringRoute){
            Long locationId = Long.parseLong(locationIdString);
            Optional<Location> checkedLocation = LocationService.getByLocationId(locationId);
            routeValid = checkedLocation.isPresent();
        }
        return routeValid;
    }

    private Boolean isDrvIdValid(Long drvId){
        Optional<Driver> checkedDriver = DriverService.getDriverById(drvId);
        return checkedDriver.isPresent();
    }

    private Boolean isCarIdValid(Long carId){
        Optional<Car> checkedCar = CarService.getCarById(carId);
        return checkedCar.isPresent();
    }

    @Transactional
    public void editErrand(Long errandId, Long carId, Long drvId, String newRoute){
        Errand manipulatedErrand = getByErrandId(errandId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.CONFLICT, "Errand with that id does not exist")
        );
        if(drvId != null){
            if(isDrvIdValid(drvId)) manipulatedErrand.setDrvId(drvId);
            else throw new ResponseStatusException(HttpStatus.CONFLICT, "Driver ID is invalid");
        }
        if(newRoute != null) {
            if(isRouteValid(newRoute))  manipulatedErrand.setPlannedRouteAsString(newRoute);
            else throw new ResponseStatusException(HttpStatus.CONFLICT, "Route is invalid");
        }
    }

    public List<Errand> getAll() {
        return repository.findAll();
    }

    public ResponseEntity<Object> searchErrands(String firstNamePart, String lastNamePart, String makePart, String modelPart, Integer batchNumber){
        List<Errand> allErrands = repository.findAll();
        List<Errand> matchedErrands = new ArrayList<Errand>();
        List<Errand> responseErrandList = new ArrayList<Errand>();

        if(firstNamePart == null)   firstNamePart = "";
        if(lastNamePart == null)    lastNamePart = "";
        if(makePart == null)    makePart = "";
        if(modelPart == null)   modelPart = "";

        for(Errand errand : allErrands){
            Car car = CarService.getCarById(errand.getCarId()).orElse(null);
            Driver driver = DriverService.getDriverById(errand.getDrvId()).orElse(null);

            if(car != null && driver != null) {
                Boolean errandMatchesSearch = false;
                errandMatchesSearch = driver.getFirstName().contains(firstNamePart) && driver.getLastName().contains(lastNamePart) &&
                        car.getMake().contains(makePart) && car.getModel().contains(modelPart);

                if (errandMatchesSearch) matchedErrands.add(errand);
            }
        }

        if(matchedErrands.isEmpty())    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found");
        else {
            Integer startIndex = batchNumber * 10 - 10;
            Integer endIndex = batchNumber * 10;
            if (matchedErrands.size() > startIndex + endIndex && matchedErrands.size() > startIndex) {
                responseErrandList = matchedErrands.subList(startIndex, endIndex);
            } else if (matchedErrands.size() > startIndex) {
                responseErrandList = matchedErrands.subList(startIndex, matchedErrands.size());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch is empty");
            }
        }

        Integer numberOfBatches = (Integer) (matchedErrands.size()/10) + 1;

        Map<String, Object> response = new HashMap<String, Object>();
        List<Object> listOfErrands = new ArrayList<Object>();
        try{
            for(Errand errand : responseErrandList){
                Map<String, Object> singleErrandFields = new HashMap<String, Object>();
                singleErrandFields.put("errand", errand);
                singleErrandFields.put("realAddressList", getListOfRealAddresses(errand.getPlannedRouteAsList()));
                singleErrandFields.put("completedPoints", getCompletedPointsByErrandId(errand.getErrandId()));
                listOfErrands.add(singleErrandFields);
            }
            response.put("errands", listOfErrands);
            response.put("size", numberOfBatches);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error");
        }
    }

    public void deleteErrandById(Long errandId) {
        if (!getByErrandId(errandId).isPresent()) throw new IllegalStateException("Errand with that id does not exist");
        else {
            repository.deleteById(errandId);
            //ErrandDataService.deleteDataById(errandId);   //usuwanie jest niepotrzebne. Jak usuwa sie obiekt klasy Errand, to ErrandData od razu jest usuwane
        }
    }
}
