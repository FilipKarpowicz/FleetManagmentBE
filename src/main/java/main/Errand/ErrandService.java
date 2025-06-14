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
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<>();

        if (!isDrvIdValid(errand.getDrvId())) {
            response.put("status", "data-not-found-0016");
            response.put("message", "Kierowca o numerze ID " + errand.getDrvId() + " nie istnieje w bazie danych. Zlecenie nie zostało utworzone");
        }
        else if (!isCarIdValid(errand.getCarId())) {
            response.put("status", "data-not-found-0017");
            response.put("message", "Pojazd o numerze ID " + errand.getCarId() + " nie istnieje w bazie danych. Zlecenie nie zostało utworzone");
        }
        else if (!isRouteValid(errand.getPlannedRoute())) {
            response.put("status", "data-not-found-0019");
            response.put("message", "Conajmniej jeden z podanych punktów trasy zlecenia nie istnieje w bazie danych");
        }
        else {
            Errand newErrand = repository.save(errand);
            generateNewDataRecord(errand);
            response.put("status", "success");
            response.put("message", "Nowe zlecenie o numerze " + newErrand.getErrandId() + " zostało utworzone");
            data.put("plannedRoute", errand.getErrandId());
            response.put("data", data);
        }
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    public List<Errand> getByDrvId(Long drvId){
        return repository.findByDrvId(drvId);
    }

    public List<Errand> getByCarId(Long carId){
        return repository.findByCarId(carId);
    }

    public Optional<Errand> getByErrandId(String errandId){
        return repository.findByErrandId(errandId);
    }

    private Boolean isRouteValid(String newRoute){
        List<String> stringRoute = new ArrayList<String>(Arrays.asList(newRoute.split("-")));
        Boolean routeValid = true;
        for(String locationIdString : stringRoute){
            Long locationId = Long.parseLong(locationIdString);
            Optional<Location> checkedLocation = locationService.getByLocationId(locationId);
            routeValid = checkedLocation.isPresent();
            if(!routeValid) break;
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
    public void editErrand(String errandId, Long carId, Long drvId, String newRoute){
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
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<>();

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

        if(matchedErrands.isEmpty()){
            response.put("status", "success");
            response.put("message", null);
            response.put("data", null);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        else {
            Integer startIndex = batchNumber * 15 - 15;
            Integer endIndex = batchNumber * 15;
            if (matchedErrands.size() > endIndex) {
                responseErrandList = matchedErrands.subList(startIndex, endIndex);
            } else if (matchedErrands.size() > startIndex) {
                responseErrandList = matchedErrands.subList(startIndex, matchedErrands.size());
            } else {
                response.put("status", "empty-0001");
                response.put("message", "Pakiet danych pusty");
                response.put("data", null);
                return new ResponseEntity<Object>(response, HttpStatus.OK);
            }
        }

        Integer numberOfBatches = (Integer) ((matchedErrands.size() -1 )/15) + 1;

        List<Object> listOfErrands = new ArrayList<Object>();
        try{
            for(Errand errand : responseErrandList){
                Map<String, Object> singleErrandFields = new HashMap<String, Object>();
                singleErrandFields.put("errand", errand);
                singleErrandFields.put("realAddressList", locationService.getListOfRealAddresses(errand.getPlannedRouteAsList()));
                singleErrandFields.put("errandStatus", errandDataRepository.findById(errand.getErrandId()).get().getErrandStatus());
                listOfErrands.add(singleErrandFields);
            }
            response.put("status", "success");
            response.put("message", null);
            data.put("errands", listOfErrands);
            data.put("size", numberOfBatches);
            response.put("data", data);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.put("status", "unknown-0003");
            response.put("message", "Błąd wewnętrzny serwera");
            response.put("data", null);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> deleteErrandById(String errandId) {
        Map<String, Object> response = new HashMap<String, Object>();
        if (!getByErrandId(errandId).isPresent()){
            response.put("status", "data-not-found-0018");
            response.put("message", "Zlecenie o numerze ID " + errandId + " nie istnieje w bazie danych");
        }
        else {
            deletePlannedRouteLocations(errandId);
            deleteAllLocations(errandId);
            repository.deleteById(errandId);
            response.put("status", "success");
            response.put("message", "Zlecenie o numerze " + errandId + " zostało usunięte");
            //ErrandDataService.deleteDataById(errandId);   //usuwanie jest niepotrzebne. Jak usuwa sie obiekt klasy Errand, to ErrandData od razu jest usuwane
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public void deletePlannedRouteLocations(String errandId){
        List<Long> listOfLocations = getByErrandId(errandId).get().getPlannedRouteAsList();
        for(Long id : listOfLocations){
            if(locationService.getByLocationId(id).isPresent()) locationService.deleteLocationById(id);
        }
    }

    public void deleteAllLocations(String errandId){
        List<Long> listOfLocations = errandDataRepository.findById(errandId).get().getAllLocationsAsList();
        for(Long id : listOfLocations){
            if(locationService.getByLocationId(id).isPresent()) locationService.deleteLocationById(id);
        }
    }
}
