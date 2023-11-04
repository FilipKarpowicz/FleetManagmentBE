package main.Location;

import io.swagger.models.auth.In;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.sql.Timestamp;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

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

    public ResponseEntity<Object> addNewLocation(Location location) {
        Location newLocation = new Location();
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        newLocation = repository.save(location);

        if(newLocation.getLocationId() == null){
            response.put("status", "unknown-0003");
            response.put("message", "Nie udało się dodać punktu do trasy zlecenia");
        }
        else {
            data.put("pointId", location.getLocationId());
            response.put("status", "success");
            response.put("message", "Punkt został poprawnie dodany");
        }
        response.put("data", data);
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    List<Location> findAll(Integer batchNumber){
        List<Location> allLocations = repository.findAll();

        Integer startIndex = batchNumber*15 - 15;
        Integer endIndex = batchNumber*15;

        if(allLocations.size() > endIndex){
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

    public ResponseEntity<Object> findLocationList(String route) {
        Map<String, Object> response = new HashMap<String, Object>();
        String[] list = route.split("-");
        List<Location> data = new ArrayList<Location>();
        for (int i = 0; i < list.length; i++) {
            Long id = Long.parseLong(list[i]);
            Optional<Location> temp = repository.findById(id);
            if(temp.isPresent()){
                data.add(temp.get());
            }else{
                response.put("status", "record-not-found-0009");
                response.put("message", "Lokalizacja o numerze ID " + id + " nie istnieje w bazie danych");
                response.put("locationId", list[i]);
                return new ResponseEntity<Object>(response, HttpStatus.OK);
            }
        }
        response.put("status", "success");
        response.put("data", data);
        response.put("message", "Dane przekazane poprawnie");
        return new ResponseEntity<Object>(response,HttpStatus.OK);
    }
}
