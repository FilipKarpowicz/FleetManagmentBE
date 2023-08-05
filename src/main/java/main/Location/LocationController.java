package main.Location;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/location")
public class LocationController {
    public final LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    @GetMapping(path = "getBy/locationId")
    Optional<Location> getByLocationId(@RequestParam(name = "locationId") Long locationId){
        return service.getByLocationId(locationId);
    }

    @GetMapping(path = "getAll")
    List<Location> getAll(){
        return service.findAll();
    }

    @PostMapping(path = "add")
    void addNewLocation(@RequestBody Location location){
        service.addNewLocation(location);
    }
}
