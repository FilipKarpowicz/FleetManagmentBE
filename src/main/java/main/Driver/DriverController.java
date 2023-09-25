package main.Driver;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DriverController {

    private final DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @DeleteMapping(path = {"api/v1/driverById/{drvId}"})
    public void deleteDriverById(@PathVariable("drvId") Long drvId) {
        driverService.deleteDriverById(drvId);
    }

    @PostMapping(path = "api/v1/addDriver")
    public void registerNewDriver(@RequestBody Driver driver) {
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
    ) {
        driverService.updateDriver(drvId, firstName, lastName, birthdate, pesel, drvLicNo, carId, overallDrvRating);
    }

    @GetMapping(path = "drivers")
    public ResponseEntity<String> findDrivers(
            @RequestParam Integer batch,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Long pesel,
            @RequestParam(required = false) String drvLicNo,
            @RequestParam(required = false) Long carId,
            @RequestParam(required = false) String MoreOrLess,
            @RequestParam(required = false) Integer overallDrvRating) {
        JSONObject response =  driverService.findDrivers(firstName,lastName,pesel,drvLicNo,carId,overallDrvRating,MoreOrLess,batch);
        return ResponseEntity.ok(response.toString());
    }




}