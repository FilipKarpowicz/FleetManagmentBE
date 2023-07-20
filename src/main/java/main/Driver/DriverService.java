package main.Driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    @Autowired
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }


    public Optional<Driver> getDriverById(Long drvId){
        return driverRepository.findById(drvId);
    }

    public List<Driver> get15Drivers(Integer BatchNumber){
        Long from  = (long) (15 * (BatchNumber - 1));
        Long to = (long) (15 * BatchNumber);
        return driverRepository.find15Drivers(from, to);

    }

    public void deleteDriverById(Long drvId){
        driverRepository.deleteById(drvId);
    }

    public void addNewDriver(Driver driver){
        Optional <Driver> driverOptional = driverRepository.findDriverByPesel(driver.getPesel());
        if (driverOptional.isPresent()){
            throw new IllegalStateException("Driver with this pesel already exist");
        }
        else {
            driverRepository.save(driver);
        }
    }

    @Transactional
    public void updateDriver(Long drvId, String firstName, String lastName, LocalDate birthdate, Long pesel, String drvLicNo, Long carId, Integer overallDrvRating) {
        Driver driverById = driverRepository.findById(drvId).orElseThrow(
                () ->new IllegalStateException("Driver with that id does not exist")
        );
        Optional <Driver> driverOptional = driverRepository.findDriverByCarId(carId);
        if(driverOptional.isPresent()){
            throw new IllegalStateException("Car ID you trying to set is already taken");
        }

        if(firstName != null && !Objects.equals(firstName, driverById.getFirstName())){
            driverById.setFirstName(firstName);
        }

        if(lastName != null && !Objects.equals(lastName,driverById.getLastName())){
            driverById.setLastName(lastName);
        }

        if(birthdate != null && !Objects.equals(birthdate,driverById.getBirthdate())){
            driverById.setBirthdate(birthdate);
        }

        if(pesel != null && !Objects.equals(pesel,driverById.getPesel())){
            driverById.setPesel(pesel);
        }

        if(drvLicNo != null && !Objects.equals(drvLicNo,driverById.getDrvLicNo())){
            driverById.setDrvLicNo(drvLicNo);
        }

        if(carId != null && !Objects.equals(carId,driverById.getCarId())){
            driverById.setCarId(carId);
        }

        if(overallDrvRating != null && !Objects.equals(overallDrvRating,driverById.getOverallDrvRating())){
            driverById.setOverallDrvRating(overallDrvRating);
        }


    }
}
