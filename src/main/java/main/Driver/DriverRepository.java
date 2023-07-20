package main.Driver;

import main.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
