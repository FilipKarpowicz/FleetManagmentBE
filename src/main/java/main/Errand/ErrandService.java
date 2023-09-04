package main.Errand;

import main.Driver.Driver;
import main.Driver.DriverService;
import main.Location.Location;
import main.Location.LocationService;
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
}
