package main.Errand;

import main.Car.Car;
import main.Car.CarService;
import main.Driver.Driver;
import main.Driver.DriverService;
import main.ErrandData.ErrandData;
import main.ErrandData.ErrandDataRepository;
import main.ErrandData.ErrandDataService;
import main.ErrandData.ErrandStatus;
import main.Location.Location;
import main.Location.LocationService;
import org.h2.util.json.JSONArray;
import org.h2.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ErrandService {
    private final ErrandRepository repository;
    private CarService carService;
    private DriverService driverService;
    private LocationService locationService;

    private ErrandDataRepository errandDataRepository;

    @Autowired
    public ErrandService(ErrandRepository repository, CarService carService, DriverService driverService, LocationService locationService, ErrandDataRepository errandDataRepository) {
        this.repository = repository;
        this.carService = carService;
        this.driverService = driverService;
        this.locationService = locationService;
        this.errandDataRepository = errandDataRepository;
    }

    public ResponseEntity<Object> addNewErrand(Errand errand) {
        if (!isDrvIdValid(errand.getDrvId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Driver with that ID does not exist");
        if (!isCarIdValid(errand.getCarId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Car with that ID does not exist");
        System.out.println(errand.getPlannedRoute());
        repository.save(errand);
        generateNewDataRecord(errand);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("status", "SUCCESS");
        response.put("message", "Errand added!");
        response.put("plannedRoute", errand.getErrandId());
        return new ResponseEntity<Object>(response, HttpStatus.OK);


    }

    List<Errand> getByDrvId(Long drvId){
        return repository.findByDrvId(drvId);
    }

    List<Errand> getByCarId(Long carId){
        return repository.findByCarId(carId);
    }

    public Optional<Errand> getByErrandId(Long errandId){
        return repository.findByErrandId(errandId);
    }

    private Boolean isRouteValid(String newRoute){
        List<String> stringRoute = new ArrayList<String>(Arrays.asList(newRoute.split("-")));
        Boolean routeValid = true;
        for(String locationIdString : stringRoute){
            Long locationId = Long.parseLong(locationIdString);
            Optional<Location> checkedLocation = locationService.getByLocationId(locationId);
            routeValid = checkedLocation.isPresent();
        }
        return routeValid;
    }

    private Boolean isDrvIdValid(Long drvId){
        Optional<Driver> checkedDriver = driverService.getDriverById(drvId);
        return checkedDriver.isPresent();
    }

    private Boolean isCarIdValid(Long carId){
        Optional<Car> checkedCar = carService.getCarById(carId);
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
            if(isRouteValid(newRoute))  manipulatedErrand.setPlannedRoute(newRoute);
            else throw new ResponseStatusException(HttpStatus.CONFLICT, "Route is invalid");
        }
    }

    private void generateNewDataRecord(Errand errand){
        ErrandData errandData = new ErrandData();
        errandData.setErrand(errand);
        errandData.setErrandStatus(ErrandStatus.WAITING);
        errandDataRepository.save(errandData);
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
            Car car = carService.getCarById(errand.getCarId()).orElse(null);
            Driver driver = driverService.getDriverById(errand.getDrvId()).orElse(null);

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
                singleErrandFields.put("realAddressList", locationService.getListOfRealAddresses(errand.getPlannedRouteAsList()));
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

    public ResponseEntity<Object> deleteErrandById(Long errandId) {
        Map<String, Object> response = new HashMap<String, Object>();
        if (!getByErrandId(errandId).isPresent()) throw new IllegalStateException("Errand with that id does not exist");
        else {
            //dodac usuwanie lokalizacji plannedRoute i allLocations
            repository.deleteById(errandId);
            response.put("status", "SUCCESS");
            response.put("message", "Errand deleted!");
            return new ResponseEntity<Object>(response, HttpStatus.OK);
            //ErrandDataService.deleteDataById(errandId);   //usuwanie jest niepotrzebne. Jak usuwa sie obiekt klasy Errand, to ErrandData od razu jest usuwane
        }
    }
}
