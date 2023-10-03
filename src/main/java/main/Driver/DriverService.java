package main.Driver;

import org.apache.catalina.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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


    public JSONObject deleteDriverById(Long drvId) {
        JSONObject response = new JSONObject();
        Optional<Driver> driverById = driverRepository.findById(drvId);
        if(driverById.isPresent()){
            driverRepository.deleteById(drvId);
            response.put("status","SUCCESS");
            response.put("message","Driver deleted!");
        }else{
            response.put("status","ERROR");
            response.put("message","Driver with id " + drvId + " does not exist in database");
        }
        return response;
    }

    public JSONObject addNewDriver(Driver driver) {
        JSONObject response = new JSONObject();
        Optional<Driver> driverByPeselOptional = driverRepository.findDriverByPesel(driver.getPesel());
        if (driverByPeselOptional.isPresent()) {
            response.put("status","ERROR");
            response.put("message","Driver with this pesel already exist");
        } else {
            driverRepository.save(driver);
            response.put("status","SUCCESS");
            response.put("message","Driver deleted!");
        }
        return response;
    }

    @Transactional
    public JSONObject updateDriver(Long drvId, String firstName, String lastName, LocalDate birthdate, Long pesel, String drvLicNo, Integer overallDrvRating) {
        JSONObject res = new JSONObject();
        Optional<Driver> driverById = driverRepository.findById(drvId);
        if(driverById.isEmpty()){
            res.put("status","ERROR");
            res.put("message","Driver with id " + drvId + " does not exist in database");
        }else{
            Driver driver = driverById.get();

            if (firstName != null && !Objects.equals(firstName, driver.getFirstName())) {
                driver.setFirstName(firstName);
            }

            if (lastName != null && !Objects.equals(lastName, driver.getLastName())) {
                driver.setLastName(lastName);
            }

            if (birthdate != null && !Objects.equals(birthdate, driver.getBirthdate())) {
                driver.setBirthdate(birthdate);
            }

            if (pesel != null && !Objects.equals(pesel, driver.getPesel())) {
                driver.setPesel(pesel);
            }

            if (drvLicNo != null && !Objects.equals(drvLicNo, driver.getDrvLicNo())) {
                driver.setDrvLicNo(drvLicNo);
            }

            if (overallDrvRating != null && !Objects.equals(overallDrvRating, driver.getOverallDrvRating())) {
                driver.setOverallDrvRating(overallDrvRating);
            }
            res.put("status","SUCCESS");
            res.put("message","Driver upadated!");

        }
        return res;
    }


    public JSONObject findDrivers(String firstName, String lastName, Long pesel, String drvLicNo, Integer overallDrvRating, String LessOrMore, Integer batch) {
        JSONObject response = new JSONObject();
        Pageable pages = PageRequest.of(batch - 1,10, Sort.by("drvId"));
        if (overallDrvRating != null && LessOrMore != null) {
            switch (LessOrMore) {
                case "More" -> {
                    Page<Driver> data = driverRepository.findDriversMore(pesel, firstName, lastName, drvLicNo, overallDrvRating, pages);
                    response.put("status","SUCCESS");
                    response.put("data", data.getContent());
                    response.put("size",data.getTotalPages());
                }
                case "Less" -> {
                    Page<Driver> data = driverRepository.findDriversLess(pesel, firstName, lastName, drvLicNo, overallDrvRating, pages);
                    response.put("status","SUCCESS");
                    response.put("data", data.getContent());
                    response.put("size",data.getTotalPages());

                }
                default -> {
                    response.put("status", "ERROR");
                    response.put("message", "Invalid overallDrvRating operator");
                }
            }
        }else {
            Page<Driver> data = driverRepository.findDriversByAll(pesel, firstName, lastName, drvLicNo, pages);
            response.put("status","SUCCESS");
            response.put("data", data.getContent());
            response.put("size",data.getTotalPages());
        }

        return response;
    }




}
