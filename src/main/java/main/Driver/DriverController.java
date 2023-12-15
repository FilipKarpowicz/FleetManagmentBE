package main.Driver;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "driver")
public class DriverController {

    private final DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @DeleteMapping(path = {"delete"})
    public ResponseEntity<Object> deleteDriverById(@RequestParam("drvId") Long drvId) {
        return driverService.deleteDriverById(drvId);
    }

    @PostMapping(path = "add")
    public ResponseEntity<Object> registerNewDriver(@RequestBody Driver driver) {
        return driverService.addNewDriver(driver);
    }


    @PutMapping(path = "modify")
    public ResponseEntity<Object> updateDriver(
            @RequestParam Long drvId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) LocalDate birthdate,
            @RequestParam(required = false) Long pesel,
            @RequestParam(required = false) String drvLicNo,
            @RequestParam(required = false) Integer overallDrvRating) {
        return driverService.updateDriver(drvId, firstName, lastName, birthdate, pesel, drvLicNo, overallDrvRating);
    }

    @GetMapping(path = "searchDrivers")
    public ResponseEntity<Object> findDrivers(
            @RequestParam Integer batch,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Long pesel,
            @RequestParam(required = false) String drvLicNo,
            @RequestParam(required = false) String moreOrLess,
            @RequestParam(required = false) Integer overallDrvRating) {
        return driverService.findDrivers(firstName,lastName,pesel,drvLicNo,overallDrvRating,moreOrLess,batch);
    }

    @GetMapping(path = "getByName")
    public ResponseEntity<Object> findDriversNames(
            @RequestParam(required = false) String name
    ){
        return driverService.getDriversByName(name);
    }


    @GetMapping(path="getById")
    public ResponseEntity<Object> getById(@RequestParam(name = "drvId") Long drvId){
        return driverService.getById(drvId);
    }

}