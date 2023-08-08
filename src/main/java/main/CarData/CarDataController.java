package main.CarData;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "CarData")
public class CarDataController {
    public static CarDataService service;

    public CarDataController(CarDataService service) {
        this.service = service;
    }

    @PutMapping(path = "Update/{carId}")
    void editCarData(@PathVariable(name = "carId") Long carId,
                     @RequestParam(required = false) Double overallMileage){
        service.editCarData(carId, overallMileage);
    }

}
