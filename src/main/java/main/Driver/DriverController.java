package main.Driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/driver")
public class DriverController {

    private final DriverService driverService;
    @Autowired
    public DriverController(DriverService driverService){this.driverService = driverService;}

    @GetMapping
    public List<Driver> getDrivers(){
        return DriverService.getDrivers();
    }

}