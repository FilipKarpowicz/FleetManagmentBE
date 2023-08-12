package main.ErrandData;

import org.springframework.stereotype.Service;

@Service
public class ErrandDataService {
    private final ErrandDataRepository repository;

    public ErrandDataService(ErrandDataRepository repository) {
        this.repository = repository;
    }
}
