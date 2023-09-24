package main.CarData;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "CarData")
public class CarDataController {
    public static CarDataService service;

    public CarDataController(CarDataService service) {
        this.service = service;
    }

    @PutMapping(path = "Update/{carId}")
    void editCarData(@PathVariable(name = "carId") Long carId,
                     @RequestParam(required = true) Double overallMileage){
        service.editCarData(carId, overallMileage);
    }

    @GetMapping(path = "Get/{carId}")
    public Optional<CarData> getCarDataById(@PathVariable(name = "carId") Long carId){
        return service.getByCarId(carId);
    }

}
