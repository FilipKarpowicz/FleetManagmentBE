package main.Location;

import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {
    public final LocationRepository repository;
    //WGS-84 SRID
    private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    Optional<Location> getByLocationId(Long locationId){
        return repository.findById(locationId);
    }

    void addNewLocation(Location location){
        repository.save(location);
    }

    List<Location> findAll(){
        return repository.findAll();
    }
}
