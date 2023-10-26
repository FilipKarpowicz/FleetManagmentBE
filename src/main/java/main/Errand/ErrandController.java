package main.Errand;

import org.h2.util.json.JSONArray;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> addNewErrand(@RequestBody Errand errand){
        System.out.println(errand.getPlannedRoute());
        return service.addNewErrand(errand);
    }

    @GetMapping(path = "GetBy/DrvId")
    List<Errand> getByDrvId(@RequestParam(name = "drvId") Long drvId){
        return service.getByDrvId(drvId);
    }

    @GetMapping(path = "GetBy/CarId")
    List<Errand> getByCarId(@RequestParam(name = "carId") Long carId){
        return service.getByCarId(carId);
    }

    @GetMapping
    List<Errand> getErrands(){return service.getAll();}


    @PutMapping(path = "EditErrand/{errandId}")
    public void editErrand(@PathVariable("errandId") String errandId,
                                @RequestParam(required = false) Long carId,
                                @RequestParam(required = false) Long drvId,
                                @RequestParam(required = false) String newRoute){   //newRoute=1-2-5-12
        service.editErrand(errandId, carId, drvId, newRoute);
    }

    @GetMapping(path = "SearchErrands")
    public ResponseEntity<Object> searchErrands(@RequestParam(required = false) String firstNamePart,
                                                @RequestParam(required = false) String lastNamePart,
                                                @RequestParam(required = false) String makePart,
                                                @RequestParam(required = false) String modelPart,
                                                @RequestParam(required = true) Integer batchNumber){
        return service.searchErrands(firstNamePart, lastNamePart, makePart, modelPart, batchNumber);
    }

    @DeleteMapping(path = "Delete/{ErrandId}")
    public ResponseEntity<Object> deleteErrand(@PathVariable(name = "ErrandId") String errandId){
        return service.deleteErrandById(errandId);
    }


}
