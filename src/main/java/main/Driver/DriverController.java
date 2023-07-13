package main.Driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/driver")
public class DriverController {

    private final DriverService driverService;
    @Autowired
    public DriverController(DriverService driverService){this.driverService = driverService;}

    @GetMapping
    public List<Driver> getDrivers(){
        return driverService.getDrivers();
    }

    @DeleteMapping(path = {"{drvId}"})
    public void deleteDriverById(@PathVariable("drvId") Long drvId){
        driverService.deleteDriverById(drvId);
    }

    @PostMapping
    public void reqisterNewDriver(@RequestBody Driver driver){
        driverService.addNewDriver(driver);
    }


    @PutMapping
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