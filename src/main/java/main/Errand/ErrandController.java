package main.Errand;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "Errand")
public class ErrandController {
    private final ErrandService service;

    public ErrandController(ErrandService service) {
        this.service = service;
    }

    @PostMapping(path = "Add")
    public void addNewErrand(@RequestBody Errand errand){
        service.addNewErrand(errand);
    }

    @GetMapping(path = "GetBy/DrvId")
    List<Errand> getByDrvId(@RequestParam(name = "drvId") Long drvId){
        return service.getByDrvId(drvId);
    }

    @GetMapping(path = "GetBy/CarId")
    List<Errand> getByCarId(@RequestParam(name = "carId") Long carId){
        return service.getByCarId(carId);
    }

    @PutMapping(path = "EditErrand/{errandId}")
    public void editErrand(@PathVariable("errandId") Long errandId,
                                @RequestParam(required = false) Long carId,
                                @RequestParam(required = false) Long drvId,
                                @RequestParam(required = false) String newRoute){   //newRoute=1-2-5-12
        service.editErrand(errandId, carId, drvId, newRoute);
    }
}
