package main.Location;

import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {
    private final LocationRepository repository;

    //WGS-84 SRID
    private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public Optional<Location> getByLocationId(Long locationId){
        return repository.findById(locationId);
    }

    public Long addNewLocation(Location location){
        Location savedLocation = repository.save(location);
        return savedLocation.getLocationId();
    }

    List<Location> findAll(Integer batchNumber){
        List<Location> allLocations = repository.findAll();

        Integer startIndex = batchNumber*15 - 15;
        Integer endIndex = batchNumber*15;

        if(allLocations.size() > startIndex + endIndex && allLocations.size() > startIndex){
            return allLocations.subList(startIndex, endIndex);
        } else if (allLocations.size() > startIndex) {
            return allLocations.subList(startIndex, allLocations.size());
        } else {
            throw new IllegalStateException("Batch is empty");
        }
    }

    @Transactional
    public void updateLocation(Long locationId, LocalDateTime arrivalTime, String realAddress){
        Location manipulatedLocation = getByLocationId(locationId).orElseThrow(
                () -> new IllegalStateException("Location with that id does not exist")
        );
        if(arrivalTime!=null){
            manipulatedLocation.setArrivalTime(arrivalTime);
        }
        if(realAddress!=null){
            manipulatedLocation.setRealAddress(realAddress);
        }
    }

    public void deleteLocationById(Long locationId){
        if(getByLocationId(locationId).isPresent()){
            repository.deleteById(locationId);
        }
        else throw new IllegalStateException("Location with that id does not exist");
    }

    public List<String> getListOfRealAddresses(List<Long> plannedRoute){ //list of locationId
        List<String> listOfRealAddresses = new ArrayList<String>();

        for (Long locationId : plannedRoute){
            Location location = getByLocationId(locationId).orElse(null);
            if(location != null)    listOfRealAddresses.add(location.getRealAddress());
        }

        return listOfRealAddresses;
    }
}
