package main.Location;

import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;

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
    public void updateLocation(Long locationId, Date arrivalTime){
        Location manipulatedLocation = getByLocationId(locationId).orElseThrow(
                () -> new IllegalStateException("Location with that id does not exist")
        );
        if(arrivalTime!=null){
            manipulatedLocation.setArrivalTime(arrivalTime);
        }
    }
}
