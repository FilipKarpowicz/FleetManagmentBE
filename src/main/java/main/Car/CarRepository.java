package main.Car;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT s FROM Car s WHERE s.plateNo = ?1")
    Optional<Car> findCarByPlate(String plate);

    @Query("SELECT s FROM Car s WHERE s.vin LIKE %?1%")
    Optional<Car> findCarByVin(String vin);

    @Query("SELECT s FROM Car s WHERE s.make LIKE %?1%")
    List<Car> findCarsByMake(String pattern);

    @Query("SELECT s FROM Car s WHERE s.model LIKE %?1%")
    List<Car> findCarsByModel(String pattern);

    @Query("SELECT s FROM Car s WHERE s.vin LIKE %?1%")
    List<Car> findCarsByVin(String pattern);

    @Query("SELECT s FROM Car s WHERE s.plateNo LIKE %?1%")
    List<Car> findCarsByPlateNo(String pattern);

    @Query("SELECT s FROM Car s WHERE s.type LIKE %?1%")
    List<Car> findCarsByType(String pattern);

    @Query("SELECT s FROM Car s WHERE s.comment LIKE %?1%")
    List<Car> findCarsByComment(String pattern);

    @Query("SELECT s FROM Car s WHERE s.serviceDate = ?1")
    List<Car> findCarsByServiceDate(LocalDate date);

    @Query("SELECT s FROM Car s WHERE MONTH(s.serviceDate) = ?1 AND YEAR(s.serviceDate) = ?2")
    List<Car> findCarsByMonthYear(Integer month, Integer year);

    @Query("SELECT s FROM Car s WHERE DAY(s.serviceDate) = ?1 AND YEAR(s.serviceDate) = ?2")
    List<Car> findCarsByDayYear(Integer day, Integer year);

    @Query("SELECT s FROM Car s WHERE YEAR(s.serviceDate) = ?1")
    List<Car> findCarsByYear(Integer year);

    @Query("SELECT s FROM Car s WHERE DAY(s.serviceDate) = ?1 AND MONTH(s.serviceDate) = ?2")
    List<Car> findCarsByMonthDay(Integer day, Integer month);

    @Query("SELECT s FROM Car s WHERE MONTH(s.serviceDate) = ?1")
    List<Car> findCarsByMonth(Integer month);

    @Query("SELECT s FROM Car s WHERE DAY(s.serviceDate) = ?1")
    List<Car> findCarsByDay(Integer day);

    @Query("SELECT s FROM Car s WHERE cast(s.serviceMileage as string ) LIKE ?1%")
    List<Car> findCarsByServiceMileage(Long value);
}
