package main.ErrandData;

import main.Errand.DatePrefixedIdSequenceGenerator;
import main.Errand.Errand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "ErrandData")
public class ErrandDataController {
    private final ErrandDataService service;

    public ErrandDataController(ErrandDataService service) {
        this.service = service;
    }

    @GetMapping(path = "Get")
    public ResponseEntity<Object> getErrandDataById(@RequestParam(name = "errandId") String errandId){
        return service.getCalculatedDataByErrandId(errandId);
    }

    @PutMapping(path = "ChangeStatus/{errandId}")
    public ResponseEntity<Object> changeErrandStatus(@PathVariable(name = "errandId") Long errandId,
                                   @RequestParam(name = "newStatus") ErrandStatus newStatus){
        return service.changeErrandStatus(errandId, newStatus);
    }

    @GetMapping(path = "GetAll")
    public List<ErrandData> getAll(@RequestParam(name = "batchNumber") Integer batchNumber){
        return service.getAll(batchNumber);
    }
}
