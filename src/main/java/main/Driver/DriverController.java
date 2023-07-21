package main.Driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class DriverController {

    private final DriverService driverService;
    @Autowired
    public DriverController(DriverService driverService){this.driverService = driverService;}

    @GetMapping(path = "api/v1/sortDriversBy/{column}/{sortingType}")
    public List<Driver> sortDriversByRating(@PathVariable("sortingType") String sortingType,@PathVariable("column") String column,@RequestParam Integer batchNumber){
        return driverService.get15DriversSorted(sortingType,column,batchNumber);
    }

    @GetMapping(path = "api/v1/findDriversByValue/{column}")
    public List<Driver> findDriversByValue(@PathVariable("column") String column, @RequestParam Long value,@RequestParam Integer batchNumber){
        return driverService.findDriverByValue(column, value, batchNumber);
    }

    @GetMapping(path = "api/v1/findDriversByBirthdate")
    public List<Driver> findDriversByBirthdate(@RequestParam Integer batchNumber,@RequestParam(required = false) Integer day,@RequestParam(required = false) Integer month,@RequestParam(required = false) Integer year){
        return driverService.findDriversByBirthdate(batchNumber,day,month,year);
    }

    @GetMapping(path = "api/v1/findDriversByPattern/{column}")
    public List<Driver> findDriversByPattern(@PathVariable("column") String column, @RequestParam String pattern, @RequestParam Integer batchNumber){
        return driverService.findDriverByPattern(column,pattern,batchNumber);
    }

    @GetMapping(path = "api/v1/batchDrivers/{batchNumber}")
    public List<Driver> getBatchOfDrivers(@PathVariable("batchNumber") Integer batchNumber){
        System.out.println("RUN");
        return driverService.calculateBatch(batchNumber);
    }

    @GetMapping(path = "api/v1/driverById/{drvId}")
    public Optional<Driver> getDriverById(@PathVariable("drvId") Long drvId){
        return driverService.getDriverById(drvId);
    }

    @DeleteMapping(path = {"api/v1/driverById/{drvId}"})
    public void deleteDriverById(@PathVariable("drvId") Long drvId){
        driverService.deleteDriverById(drvId);
    }

    @PostMapping(path = "api/v1/addDriver")
    public void reqisterNewDriver(@RequestBody Driver driver){
        driverService.addNewDriver(driver);
    }


    @PutMapping(path = "api/v1/upadteDriver/{drvId}")
    public void updateDriver(
            @PathVariable("drvId") Long drvId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) LocalDate birthdate,
            @RequestParam(required = false) Long pesel,
            @RequestParam(required = false) String drvLicNo,
            @RequestParam(required = false) Long carId,
            @RequestParam(required = false) Integer overallDrvRating
    ){
        driverService.updateDriver(drvId,firstName,lastName,birthdate,pesel,drvLicNo,carId,overallDrvRating);
    }


}