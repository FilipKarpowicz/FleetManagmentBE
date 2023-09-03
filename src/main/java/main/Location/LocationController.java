package main.Location;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/Location")
public class LocationController {
    public final LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    @GetMapping(path = "GetBy/LocationId")
    Optional<Location> getByLocationId(@RequestParam(name = "locationId") Long locationId){
        return service.getByLocationId(locationId);
    }

    @GetMapping(path = "GetAll")
    List<Location> getAll(){
        return service.findAll();
    }

    @PostMapping(path = "Add")
    void addNewLocation(@RequestBody Location location){
        service.addNewLocation(location);
    }

    @PutMapping(path = "Update/{LocationId}")
    void updateLocation(@PathVariable(name = "LocationId") Long locationId,
                        @RequestParam(name = "arrivalTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") Date arrivalTime){
        service.updateLocation(locationId, arrivalTime);
    }

    //zwrot wszystkich lokalizacji z id erranda
}
