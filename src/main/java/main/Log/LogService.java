package main.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LogService {

    private final LogRepository repository;

    @Autowired
    public LogService(LogRepository repository) {
        this.repository = repository;
    }

    public List<Log> getAll(){
        return repository.sortByTimestamp();
    }

    public Optional<Log> getLatestFromDevId(String devId){
        return Optional.ofNullable(repository.showLatestFromDevId(devId).get(0));
    }

    //pomyslec co zrobic z tym ze tu jest instancja klasy Log, moze po prostu destroy na koncu?
    public void deleteLogsOlderThan(Integer days){
        List<Log> allLogs = repository.sortByTimestamp();
        if (allLogs != null) {
            for (Log allLog : allLogs) {
                if (allLog.getTimestamp().isBefore(LocalDate.now().minusDays(days))) {
                    repository.deleteLogByTimestamp(allLog.getTimestamp());
                }
            }
        }
    }

    //dodac logike, co jesli dwa logi maja ten sam timestamp
    public void addNewLog(Log log){
        repository.save(log);
    }
}
