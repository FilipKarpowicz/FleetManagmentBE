package main.ErrandData;

import main.Errand.Errand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        if(!getByErrandId(errandId).isPresent()) throw new IllegalStateException("Errand data with that ID does not exist");
        else repository.deleteById(errandId);
    }

    public static void generateNewDataRecord(Errand errand){
        ErrandData errandData = new ErrandData();
        errandData.setErrand(errand);
        repository.save(errandData);
    }

    @Transactional
    public void changeErrandStatus(Long errandId, ErrandStatus newStatus){
        Optional<ErrandData> manipulatedRecord = getByErrandId(errandId);
        if(manipulatedRecord.isPresent()){
            manipulatedRecord.get().setErrandStatus(newStatus);
        }
        else throw new IllegalStateException("Errand data with that ID does not exist");
    }

    public List<ErrandData> getAll(Integer batchNumber){
        List<ErrandData> allData = repository.findAll();

        Integer startIndex = batchNumber*15 - 15;
        Integer endIndex = batchNumber*15;

        if(allData.size() > startIndex + endIndex && allData.size() > startIndex){
            return allData.subList(startIndex, endIndex);
        } else if (allData.size() > startIndex) {
            return allData.subList(startIndex, allData.size());
        } else {
            throw new IllegalStateException("Batch is empty");
        }
    }
}
