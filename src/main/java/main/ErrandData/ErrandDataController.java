package main.ErrandData;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "ErrandData")
public class ErrandDataController {
    private final ErrandDataService service;

    public ErrandDataController(ErrandDataService service) {
        this.service = service;
    }
}
