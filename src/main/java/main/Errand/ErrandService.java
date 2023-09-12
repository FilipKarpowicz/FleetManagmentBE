package main.Errand;

import main.Car.Car;
import main.Car.CarService;
import main.Driver.Driver;
import main.Driver.DriverService;
import main.Location.Location;
import main.Location.LocationService;
import org.h2.util.json.JSONArray;
import org.h2.util.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ErrandService {
    private final ErrandRepository repository;

    public ErrandService(ErrandRepository repository) {
        this.repository = repository;
    }

    public void addNewErrand(Errand errand){
        repository.save(errand);
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

    @Transactional
    public void editErrand(Long errandId, Long carId, Long drvId, String newRoute){
        Errand manipulatedErrand = getByErrandId(errandId).orElseThrow(
                () -> new IllegalStateException("Errand with that id does not exist")
        );
        if(drvId != null && isDrvIdValid(drvId)){
            manipulatedErrand.setDrvId(drvId);  //dac exception jesli jest invalid
        }
        if(newRoute != null && isRouteValid(newRoute)) {
            manipulatedErrand.setPlannedRouteAsString(newRoute);    //dac exception jesli jest invalid
        }
    }

    public List<Errand> getAll() {
        return repository.findAll();
    }

    public List<Errand> searchErrands(String firstNamePart, String lastNamePart, String makePart, String modelPart, Integer batchNumber){
        List<Errand> allErrands = repository.findAll();
        List<Errand> matchedErrands = new ArrayList<>();

        if(firstNamePart == null)   firstNamePart = "";
        if(lastNamePart == null)    lastNamePart = "";
        if(makePart == null)    makePart = "";
        if(modelPart == null)   modelPart = "";

        for(Errand errand : allErrands){
            Car car = CarService.getCarById(errand.getCarId()).get();
            Driver driver = DriverService.getDriverById(errand.getDrvId()).get();

            Boolean errandMatchesSearch = false;
            errandMatchesSearch = driver.getFirstName().contains(firstNamePart) && driver.getLastName().contains(lastNamePart) &&
                    car.getMake().contains(makePart) && car.getModel().contains(modelPart);

            if (errandMatchesSearch)    matchedErrands.add(errand);
        }

        if(matchedErrands.isEmpty())    throw new IllegalStateException("no results found");

        Integer startIndex = batchNumber*15 - 15;
        Integer endIndex = batchNumber*15;
        if(matchedErrands.size() > startIndex + endIndex && matchedErrands.size() > startIndex){
            return matchedErrands.subList(startIndex, endIndex);
        } else if (matchedErrands.size() > startIndex) {
            return matchedErrands.subList(startIndex, matchedErrands.size());
        } else {
            throw new IllegalStateException("batch is empty");
        }
    }

//    public JSONArray displayMatchedErrands(String firstNamePart, String lastNamePart, String makePart, String modelPart, Integer batchNumber){
//        List<Errand> matchedErrands = this.searchErrands(firstNamePart, lastNamePart, makePart, modelPart, batchNumber);
//        for(Errand errand : matchedErrands){
//            JSONObject errandJson = errand.createJsonObject();
//        }
//    }
    //dodac sklejanie, tak zeby do kazdego wyszukanego erranda dodac liste jego realAdressow i completed points
}
