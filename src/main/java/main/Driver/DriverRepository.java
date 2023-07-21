package main.Driver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT s FROM Driver s WHERE s.carId = ?1")
    Optional<Driver> findDriverByCarId(Long carId);

    @Query("SELECT s FROM Driver s WHERE s.drvId BETWEEN ?1 AND ?2 ORDER BY s.drvId ASC")
    List<Driver> find15Drivers(Long from, Long to);

    @Query("SELECT s From Driver s WHERE s.pesel = ?1")
    Optional<Driver> findDriverByPesel(Long pesel);

    @Query("SELECT s From Driver s WHERE CAST(s.pesel AS string ) LIKE ?1%")
    List<Driver> findDriversByPesel(String pesel);

    @Query("SELECT s FROM Driver s WHERE s.drvId BETWEEN ?1 AND ?2 AND s.firstName LIKE %?3% ")
    List<Driver> driversLikeByFirstName(String firstName );

    @Query("SELECT s FROM Driver s WHERE s.drvId BETWEEN ?1 AND ?2 AND s.lastName LIKE %?3% ")
    List<Driver> driversLikeByLastName(String lastName );

    @Query("SELECT s FROM Driver s WHERE s.drvId BETWEEN ?1 AND ?2 AND s.drvLicNo LIKE %?3% ")
    List<Driver> driversLikeByDrvLicNo(String drvLicNo );

    @Query("SELECT s FROM Driver s WHERE s.drvId BETWEEN ?1 AND ?2 AND s.overallDrvRating = ?3 ")
    Optional<Driver> findDriverByOverallDrvRating(Long from, Long to, Long value);

    @Query("SELECT s FROM Driver s WHERE s.birthdate = ?1 ")
    List<Driver> findDriverByBirthDate(LocalDate date);

    @Query("SELECT s FROM Driver s WHERE MONTH(s.birthdate)=?1 AND YEAR(s.birthdate)=?2 ")
    List<Driver> findDriverByMonthYear(Integer month, Integer year);

    @Query("SELECT s FROM Driver s WHERE DAY(s.birthdate)=?1 AND YEAR(s.birthdate)=?2 ")
    List<Driver> findDriverByDayYear(Integer day, Integer year);

    @Query("SELECT s FROM Driver s WHERE YEAR(s.birthdate)=?1 ")
    List<Driver> findDriverByYear(Integer year);

    @Query("SELECT s FROM Driver s WHERE DAY(s.birthdate)=?1 AND MONTH(s.birthdate)=?2 ")
    List<Driver> findDriverByMonthDay(Integer day, Integer month);

    @Query("SELECT s FROM Driver s WHERE MONTH(s.birthdate)=?1 ")
    List<Driver> findDriverByMonth(Integer month);

    @Query("SELECT s FROM Driver s WHERE DAY(s.birthdate)=?1 ")
    List<Driver> findDriverByDay(Integer day);

    @Query("SELECT s FROM Driver s WHERE cast(s.drvId as string ) LIKE ?1% ")
    List<Driver> findDriversByDrvId(String value);

    @Query("SELECT s FROM Driver s WHERE cast(s.carId as string ) LIKE ?1% ")
    List<Driver> findDriversByCarId(String string);

    @Query("SELECT s FROM Driver s WHERE cast(s.overallDrvRating as string ) LIKE ?1% ")
    List<Driver> findDriversByOverallDrvRating(String string);
}
