package main.Car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "**")
@RequestMapping(path = "car")
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

    /*
    UZYWANE ENDPOINTY vvvv
     */

    @GetMapping(path = "getById")
    ResponseEntity<Object> getCarById(@RequestParam(name = "carId") Long carId){
        return carService.getCarResponse(carId);
    }

    @PostMapping(path = "add")
    public ResponseEntity<Object> registerNewCar(@RequestBody Car car){
        return carService.addNewCar(car);
    }

    @DeleteMapping(path = "delete")
    public ResponseEntity<Object> deleteCar(@RequestParam("carId") Long carId){
        return carService.deleteCar(carId);
    }

    @PutMapping(path = "modify")
    public ResponseEntity<Object> updateCar(
            @RequestParam(required = true, name = "carId") Long carId,
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String vin,
            @RequestParam(required = false) String plateNo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) LocalDate serviceDate,
            @RequestParam(required = false) Long serviceMileage,
            @RequestParam(required = false) Double battNominalCapacity,
            @RequestParam(required = false) String devId){
            return carService.updateCar(carId,make,model,vin,plateNo,type,comment,serviceDate,serviceMileage, battNominalCapacity, devId);
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
                                                @RequestParam(required = false) Double carMileageThreshold,
                                                @RequestParam(required = false) String carMileageLessOrMore,
                                                @RequestParam(required = false) Integer battSocThreshold,
                                                @RequestParam(required = false) String battSocLessOrMore,
                                                @RequestParam(required = true) Integer batch){
        return carService.searchCars(makePart, modelPart, vinPart, plateNumberPart, typePart, serviceMileageLowerThreshold, serviceMileageUpperThreshold, serviceDateLowerThreshold, serviceDateUpperThreshold, carMileageThreshold, carMileageLessOrMore, battSocThreshold, battSocLessOrMore, batch);
    }

    @GetMapping(path = "getByName")
    public ResponseEntity<Object> carsByNames(
            @RequestParam(required = false) String name
    ){
        return carService.carsByName(name);
    }

}
