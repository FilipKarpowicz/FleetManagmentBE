package main.Driver;

import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT s FROM Driver s WHERE s.pesel=?1")
    Optional<Driver> findDriverByPesel(Long pesel);

    @Query("SELECT s FROM Driver s WHERE (?1 IS NULL OR cast(s.pesel as string ) LIKE %?1)" +
            "AND (?2 IS NULL OR s.firstName LIKE %?2%) AND (?3 IS NULL OR s.lastName LIKE %?3%)" +
            "AND (?4 IS NULL OR s.drvLicNo LIKE %?4%)")
    Page<Driver>findDriversByAll(Long pesel,String firstName, String lastName,String drvLicNo, Pageable pageable);

    @Query("SELECT s FROM Driver s WHERE (?1 IS NULL OR cast(s.pesel as string ) LIKE %?1)" +
            "AND (?2 IS NULL OR s.firstName LIKE %?2%) AND (?3 IS NULL OR s.lastName LIKE %?3%)" +
            "AND (?4 IS NULL OR s.drvLicNo LIKE %?4%) AND (?5 IS NULL OR s.overallDrvRating < ?5)")
    Page<Driver> findDriversLess(Long pesel, String firstName, String lastName, String drvLicNo, Integer overallDrvRating, Pageable pageable);

    @Query("SELECT s FROM Driver s WHERE (?1 IS NULL OR cast(s.pesel as string ) LIKE %?1)" +
            "AND (?2 IS NULL OR s.firstName LIKE %?2%) AND (?3 IS NULL OR s.lastName LIKE %?3%)" +
            "AND (?4 IS NULL OR s.drvLicNo LIKE %?4%) AND (?5 IS NULL OR s.overallDrvRating > ?5)")
    Page<Driver>findDriversMore(Long pesel, String firstName, String lastName, String drvLicNo, Integer overallDrvRating, Pageable pageable);

}
