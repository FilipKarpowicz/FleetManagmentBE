package main.ErrandData;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ErrandDataService {
    private static ErrandDataRepository repository;

    public ErrandDataService(ErrandDataRepository repository) {
        this.repository = repository;
    }

    public static Optional<ErrandData> getByErrandId(Long errandId){
        return repository.findById(errandId);
    }

    public static Integer getCompletedPointsByErrandId(Long errandId){
        ErrandData errandData = getByErrandId(errandId).get();
        return errandData.getCompletedPoints();
    }

    public static void deleteDataById(Long errandId){
        if(getByErrandId(errandId).isPresent()){
            repository.deleteById(errandId);
        }
        else throw new IllegalStateException("ErrandData with that id does not exist");
    }

    public static void generateNewDataRecord(ErrandData errandData){
        repository.save(errandData);
    }
}
