package main.Location;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "location")
public class LocationController {
    private final LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    @GetMapping(path = "GetBy/LocationId")
    Optional<Location> getByLocationId(@RequestParam(name = "locationId") Long locationId){
        return service.getByLocationId(locationId);
    }

    @GetMapping(path = "GetAll")
    List<Location> getAll(@RequestParam(name = "batchNumber") Integer batchNumber){
        return service.findAll(batchNumber);
    }

    @PutMapping(path = "Update/{LocationId}")
    void updateLocation(@PathVariable(name = "LocationId") Long locationId,
                        @RequestParam(name = "ArrivalTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") LocalDateTime arrivalTime,
                        @RequestParam(name = "RealAddress", required = false) String realAddress){
        service.updateLocation(locationId, arrivalTime, realAddress);
    }

    @DeleteMapping(path = "Delete/{LocationId}")
    void deleteLocation(@PathVariable(name = "LocationId") Long locationId){
        service.deleteLocationById(locationId);
    }

    /*
    UZYWANE ENDPOINTY vvvv
     */

    @PostMapping(path = "add")
    public ResponseEntity<Object> addNewLocation(@RequestBody Location location){
        return service.addNewLocation(location);
    }

    @GetMapping(path = "getErrandLocations")
    ResponseEntity<Object> getLocationList(@RequestParam(name = "route") String route){
        return service.findLocationList(route);
    }
}
