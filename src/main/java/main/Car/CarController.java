package main.Car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "**")
@RequestMapping(path = "car")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping(path = "BatchCars/{batchNumber}")
    public List<Car> getCarsBatch(@PathVariable("batchNumber") Integer batchNumber){
        return carService.getCarsBatch(batchNumber);
    }

    @GetMapping(path = "SortCarsBy/{column}/{sortingType}/")
    public List<Car> sortCarsBy(@PathVariable("column") String column,@PathVariable("sortingType") String sortingType,@RequestParam Integer batchNumber){
        return carService.getBatchCarsSorted(sortingType,column,batchNumber);
    }

    @GetMapping(path = "FindCarsByServiceMileage")
    public List<Car> findCarsByServiceMileage(@RequestParam Long value, @RequestParam Integer batchNumnber){
        return carService.findCarByServiceMileage(value, batchNumnber);
    }

    @GetMapping(path = "FindCarsByPattern/{column}")
    List<Car> findCarsByPattern(@PathVariable("column") String column,@RequestParam String pattern, @RequestParam Integer batchNumber){
        return carService.findCarsByPattern(column, pattern, batchNumber);
    }

    @GetMapping(path = "FindCarsByServiceDate")
    List<Car> findCarsByServiceDate(@RequestParam Integer batchNumber,@RequestParam(required = false) Integer day,@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year){
        return carService.findCarsByServiceDate(batchNumber, day, month, year);
    }


    @GetMapping
    public List<Car> getCars() {
        return carService.getCars();
    }

    @PostMapping(path = "add")
    public void registerNewCar(@RequestBody Car car){
        carService.addNewCar(car);
    }

    @DeleteMapping(path = "delete")
    public void deleteCar(@RequestParam("carId") Long carId){
        carService.deleteCar(carId);
    }

    @PutMapping(path = "update")
    public void updateCar(
            @RequestParam(required = true, name = "carId") Long carId,
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String vin,
            @RequestParam(required = false) String plateNo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) LocalDate serviceDate,
            @RequestParam(required = false) Long serviceMileage){
            carService.updateCar(carId,make,model,vin,plateNo,type,comment,serviceDate,serviceMileage);
    }

    @GetMapping(path = "searchCars")
    public ResponseEntity<Object> searchCars(@RequestParam(required = false) String makePart,
                                                @RequestParam(required = false) String modelPart,
                                                @RequestParam(required = false) String vinPart,
                                                @RequestParam(required = false) String plateNumberPart,
                                                @RequestParam(required = false) String typePart,
                                                @RequestParam(required = false) Long serviceMileageLowerThreshold,
                                                @RequestParam(required = false) Long serviceMileageUpperThreshold,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate serviceDateLowerThreshold,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate serviceDateUpperThreshold,
                                                @RequestParam(required = true) Integer batch){
        return carService.searchCars(makePart, modelPart, vinPart, plateNumberPart, typePart, serviceMileageLowerThreshold, serviceMileageUpperThreshold, serviceDateLowerThreshold, serviceDateUpperThreshold, batch);
    }
}
