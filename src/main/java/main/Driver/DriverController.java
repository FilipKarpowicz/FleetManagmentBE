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
public class DriverController {

    private final DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @DeleteMapping(path = {"driver/delete"})
    public ResponseEntity<String> deleteDriverById(@RequestParam("drvId") Long drvId) {
        JSONObject response = driverService.deleteDriverById(drvId);
        if (response.get("status") == "SUCCESS"){
            return ResponseEntity.ok(response.toString());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.toString());
        }
    }

    @PostMapping(path = "driver/add")
    public ResponseEntity<String> registerNewDriver(@RequestBody Driver driver) {
        JSONObject response = driverService.addNewDriver(driver);
        if (response.get("status") == "SUCCESS"){
            return ResponseEntity.ok(response.toString());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.toString());
        }
    }


    @PutMapping(path = "driver/modify")
    public ResponseEntity<String> updateDriver(
            @RequestParam Long drvId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) LocalDate birthdate,
            @RequestParam(required = false) Long pesel,
            @RequestParam(required = false) String drvLicNo,
            @RequestParam(required = false) Integer overallDrvRating
    ) {
        JSONObject response = driverService.updateDriver(drvId, firstName, lastName, birthdate, pesel, drvLicNo, overallDrvRating);
        if (response.get("status") == "SUCCESS"){
            return ResponseEntity.ok(response.toString());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.toString());
        }
    }

    @GetMapping(path = "drivers")
    public ResponseEntity<String> findDrivers(
            @RequestParam Integer batch,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Long pesel,
            @RequestParam(required = false) String drvLicNo,
            @RequestParam(required = false) String moreOrLess,
            @RequestParam(required = false) Integer overallDrvRating) {
        JSONObject response =  driverService.findDrivers(firstName,lastName,pesel,drvLicNo,overallDrvRating,moreOrLess,batch);
        if (response.get("status") == "SUCCESS"){
            return ResponseEntity.ok(response.toString());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.toString());
        }

    }




}