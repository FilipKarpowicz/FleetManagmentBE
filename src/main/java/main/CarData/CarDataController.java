package main.CarData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "CarData")
public class CarDataController {
    private final CarDataService service;

    @Autowired
    public CarDataController(CarDataService service) {
        this.service = service;
    }

    @GetMapping(path = "Get/{carId}")
    public Optional<CarData> getCarDataById(@PathVariable(name = "carId") Long carId){
        return service.getByCarId(carId);
    }

}
