package main.car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping(path = "batchCars/{batchNumber}")
    public List<Car> getCarsBatch(@PathVariable("batchNumber") Integer batchNumber){
        return carService.getCarsBatch(batchNumber);
    }

    @GetMapping(path = "sortCarsBy/{column}/{sortingType}/")
    public List<Car> sortCarsBy(@PathVariable("column") String column,@PathVariable("sortingType") String sortingType,@RequestParam Integer batchNumber){
        return carService.getBatchCarsSorted(sortingType,column,batchNumber);
    }

    @GetMapping(path = "findCarsByServiceMileage")
    public List<Car> findCarsByServiceMileage(@RequestParam Long value, @RequestParam Integer batchNumnber){
        return carService.findCarByServiceMileage(value, batchNumnber);
    }

    @GetMapping(path = "findCarsByPattern/{column}")
    List<Car> findCarsByPattern(@PathVariable("column") String column,@RequestParam String pattern, @RequestParam Integer batchNumber){
        return carService.findCarsByPattern(column, pattern, batchNumber);
    }

    @GetMapping(path = "findCarsByServiceDate")
    List<Car> findCarsByServiceDate(@RequestParam Integer batchNumber,@RequestParam(required = false) Integer day,@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year){
        return carService.findCarsByServiceDate(batchNumber, day, month, year);
    }

    @GetMapping
    public List<Car> getCars() {
        return carService.getCars();
    }

    @PostMapping
    public void registerNewCar(@RequestBody Car car){
        carService.addNewCar(car);
    }

    @DeleteMapping(path = "{carId}")
    public void deleteCar(@PathVariable("carId") Long carId){
         carService.deleteCar(carId);
    }

    @PutMapping(path = "{carId}")
    public void updateCar(
            @PathVariable("carId") Long carId,
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

}
