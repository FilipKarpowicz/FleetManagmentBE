package main.Log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, LocalDate> {

    @Query("FROM Log as log ORDER BY log.timestamp DESC")
    public List<Log> sortByTimestamp();

    @Query("FROM Log as log WHERE log.devId = ?1 ORDER BY log.timestamp DESC")
    public List<Log> showLatestFromDevId(String devId);

    @Query("DELETE FROM Log as log WHERE log.timestamp = ?1")
    public void deleteLogByTimestamp(LocalDate timestamp);

}
