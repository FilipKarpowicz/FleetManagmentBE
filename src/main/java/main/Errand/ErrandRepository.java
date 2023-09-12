package main.Errand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ErrandRepository extends JpaRepository<Errand, Long> {
    @Query("FROM Errand as errand WHERE errand.drvId=?1")
    List<Errand> findByDrvId(Long drvId);

    @Query("FROM Errand as errand WHERE errand.carId=?1")
    List<Errand> findByCarId(Long carId);

    @Query("FROM Errand as errand WHERE errand.errandId=?1")
    Optional<Errand> findByErrandId(Long errandId);

//    @Query("FROM Errand as errand WHERE errand.")
//    List<Errand> searchErrands(String namePart, String lastNamePart, String makePart, String modelPart);
}
