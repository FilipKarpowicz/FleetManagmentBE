package main.ErrandData;

import main.Errand.DatePrefixedIdSequenceGenerator;
import main.Errand.Errand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "errandData")
public class ErrandDataController {
    private final ErrandDataService service;

    public ErrandDataController(ErrandDataService service) {
        this.service = service;
    }

    @GetMapping(path = "GetAll")
    public List<ErrandData> getAll(@RequestParam(name = "batchNumber") Integer batchNumber){
        return service.getAll(batchNumber);
    }

    /*
    UZYWANE ENDPOINTY vvvv
     */

    @GetMapping(path = "getById")
    public ResponseEntity<Object> getErrandDataById(@RequestParam(name = "errandId") String errandId){
        return service.getCalculatedDataByErrandId(errandId);
    }

    @PutMapping(path = "changeStatus/{errandId}")
    public ResponseEntity<Object> changeErrandStatus(@PathVariable(name = "errandId") String errandId,
                                   @RequestParam(name = "newStatus") ErrandStatus newStatus){
        return service.changeErrandStatus(errandId, newStatus);
    }

    @GetMapping(path = "getRoute")
    public ResponseEntity<Object> findRoute(@RequestParam String errandId){
        return service.findRoute(errandId);
    }
}
