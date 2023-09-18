package main.Location;

import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.sql.Timestamp;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Service
public class LocationService {
    public static LocationRepository repository;

    //WGS-84 SRID
    private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public static Optional<Location> getByLocationId(Long locationId){
        return repository.findById(locationId);
    }

    void addNewLocation(Location location){
        repository.save(location);
    }

    List<Location> findAll(){
        return repository.findAll();
    }

    @Transactional
    public void updateLocation(Long locationId, Date arrivalTime, String street, String buildingNumber, String city, Long latitude, Long longitude){
        Location manipulatedLocation = getByLocationId(locationId).orElseThrow(
                () -> new IllegalStateException("Location with that id does not exist")
        );
        if(arrivalTime!=null){
            manipulatedLocation.setArrivalTime(arrivalTime);
        }
//        if(manipulatedLocation.getRealAddress() != null){
//            String oldStreet = Arrays.asList(manipulatedLocation.getRealAddress().split("-")).get(0).toString();
//            String oldBuildingNumber = Arrays.asList(manipulatedLocation.getRealAddress().split("-")).get(1).toString();
//            String oldCity = Arrays.asList(manipulatedLocation.getRealAddress().split("-")).get(2).toString();
//        }
    }

    public static List<String> getListOfRealAddresses(List<Long> plannedRoute){ //1-2-3-4
        List<String> listOfRealAddresses = new ArrayList<String>();

        for (Long locationId : plannedRoute){
            Location location = getByLocationId(locationId).orElse(null);
            if(location != null)    listOfRealAddresses.add(location.getRealAddress());
        }

        return listOfRealAddresses;
    }
}
