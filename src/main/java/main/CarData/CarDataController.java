package main.CarData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "carData")
public class CarDataController {
    private final CarDataService service;

    @Autowired
    public CarDataController(CarDataService service) {
        this.service = service;
    }

    @GetMapping(path = "getById")
    public ResponseEntity<Object> getCarDataById(@RequestParam(name = "carId") Long carId){
        return service.getResponse(carId);
    }

}
