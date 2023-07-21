package main.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService service;

    @Autowired
    public LogController(LogService service) {
        this.service = service;
    }

    @GetMapping("")
    public List<Log> findAll(){
        return service.getAll();
    }

    @GetMapping("/latest/{devId}")
    public Optional<Log> getLatestFromDevId(@PathVariable("devId") String devId){
        return service.getLatestFromDevId(devId);
    }

    @DeleteMapping("/delete/olderThanDays={days}")
    public void deleteOlderThan(@PathVariable("days") Integer days){
        service.deleteLogsOlderThan(days);
    }

    @PutMapping("/addNew/")
    public void addNewLog(@RequestBody Log log){
        service.addNewLog(log);
    }
}
