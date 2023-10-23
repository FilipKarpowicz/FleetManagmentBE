package main.ErrandData;

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

    @GetMapping(path = "Get/{errandId}")
    public ResponseEntity<Object> getErrandDataById(@PathVariable(name = "errandId") String errandId){
        return service.getCalculatedDataByErrandId(errandId);
    }

    @PutMapping(path = "ChangeStatus/{errandId}")
    public void changeErrandStatus(@PathVariable(name = "errandId") String errandId,
                                   @RequestParam(name = "newStatus") ErrandStatus newStatus){
        service.changeErrandStatus(errandId, newStatus);
    }

    @GetMapping(path = "GetAll")
    public List<ErrandData> getAll(@RequestParam(name = "batchNumber") Integer batchNumber){
        return service.getAll(batchNumber);
    }
}
