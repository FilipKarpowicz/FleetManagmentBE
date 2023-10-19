package main.ErrandData;

import main.Errand.Errand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ErrandDataService {
    private final ErrandDataRepository repository;

    public ErrandDataService(ErrandDataRepository repository) {
        this.repository = repository;
    }

    public Optional<ErrandData> getByErrandId(Long errandId){
        return repository.findById(errandId);
    }

    public void generateNewDataRecord(Errand errand){
        ErrandData errandData = new ErrandData();
        errandData.setErrand(errand);
        errandData.setErrandStatus(ErrandStatus.WAITING);
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
