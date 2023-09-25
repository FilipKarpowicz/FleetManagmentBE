package main.Driver;

import org.apache.catalina.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class DriverService {

    public static DriverRepository driverRepository;

    private List<Driver> calculateBatch(Integer batchNumber, List<Driver> table) {
        int from = 15 * (batchNumber - 1);
        int to = 15 * batchNumber;
        if (table.size() > from + to && table.size() > from) {
            table = table.subList(from, to);
        } else if (table.size() > from) {
            table = table.subList(from, table.size());
        } else {
            throw new IllegalStateException("batch is empty");
        }
        return table;
    }


    @Autowired
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public static Optional<Driver> getDriverById(Long drvId) {
        return driverRepository.findById(drvId);
    }


    public void deleteDriverById(Long drvId) {
        driverRepository.deleteById(drvId);
    }

    public void addNewDriver(Driver driver) {
        Optional<Driver> driverByPeselOptional = driverRepository.findDriverByPesel(driver.getPesel());
        if (driverByPeselOptional.isPresent()) {
            throw new IllegalStateException("Driver with this pesel already exist");
        } else {
            driverRepository.save(driver);
        }
    }

    @Transactional
    public void updateDriver(Long drvId, String firstName, String lastName, LocalDate birthdate, Long pesel, String drvLicNo, Integer overallDrvRating) {
        Driver driverById = driverRepository.findById(drvId).orElseThrow(
                () -> new IllegalStateException("Driver with that id does not exist")
        );

        if (firstName != null && !Objects.equals(firstName, driverById.getFirstName())) {
            driverById.setFirstName(firstName);
        }

        if (lastName != null && !Objects.equals(lastName, driverById.getLastName())) {
            driverById.setLastName(lastName);
        }

        if (birthdate != null && !Objects.equals(birthdate, driverById.getBirthdate())) {
            driverById.setBirthdate(birthdate);
        }

        if (pesel != null && !Objects.equals(pesel, driverById.getPesel())) {
            driverById.setPesel(pesel);
        }

        if (drvLicNo != null && !Objects.equals(drvLicNo, driverById.getDrvLicNo())) {
            driverById.setDrvLicNo(drvLicNo);
        }

        if (overallDrvRating != null && !Objects.equals(overallDrvRating, driverById.getOverallDrvRating())) {
            driverById.setOverallDrvRating(overallDrvRating);
        }


    }


    public JSONObject findDrivers(String firstName, String lastName, Long pesel, String drvLicNo, Integer overallDrvRating, String LessOrMore, Integer batch) {
        JSONObject response = new JSONObject();
        Pageable pages = PageRequest.of(batch - 1,10, Sort.by("drvId"));
        if (overallDrvRating != null && LessOrMore != null) {
            switch (LessOrMore) {
                case "More" -> {
                    List<Driver> data = driverRepository.findDriversMore(pesel, firstName, lastName, drvLicNo, overallDrvRating, pages);
                    response.put("status","SUCCESS");
                    response.put("data", data);
                }
                case "Less" -> {
                    List<Driver> data = driverRepository.findDriversLess(pesel, firstName, lastName, drvLicNo, overallDrvRating, pages);
                    response.put("status","SUCCESS");
                    response.put("data", data);
                }
                default -> {
                    response.put("status", "ERROR");
                    response.put("message", "Invalid overallDrvRating operator");
                }
            }
        }else {
            List<Driver> data = driverRepository.findDriversByAll(pesel, firstName, lastName, drvLicNo, pages);
            response.put("status","SUCCESS");
            response.put("data", data);
        }

        return response;
    }




}
