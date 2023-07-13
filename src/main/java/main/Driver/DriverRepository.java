package main.Driver;

import main.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT s FROM Driver s WHERE s.carId = ?1")
    Optional<Driver> findDriverByCarId(Long carId);
}
