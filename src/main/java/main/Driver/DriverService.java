package main.Driver;

import org.apache.catalina.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

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

    public Optional<Driver> getDriverById(Long drvId) {
        return driverRepository.findById(drvId);
    }

    public ResponseEntity<Object> getById(Long drvId){
        Map<String, Object> response = new HashMap<String, Object>();
        Optional<Driver> driver = driverRepository.findById(drvId);

        if(driver.isEmpty()){
            response.put("status", "record-not-found-0007");
            response.put("message", "Kierowca z ID " + drvId + " nie istnieje w bazie");
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            response.put("status", "success");
            response.put("data", driver.get());
            response.put("message", "Dane przekazane poprawnie");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    public ResponseEntity<Object> deleteDriverById(Long drvId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Driver> driverById = driverRepository.findById(drvId);
        if(driverById.isPresent()){
            driverRepository.deleteById(drvId);
            response.put("status","success");
            response.put("message","Kierowca został usunięty z bazy");
        }else{
            response.put("status","record-not-found-0010");
            response.put("message","Kierowca z id " + drvId + " nie istnieje w bazie");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Object> addNewDriver(Driver driver) {
        Map<String, Object> response = new HashMap<>();

        Optional<Driver> driverByPeselOptional = driverRepository.findDriverByPesel(driver.getPesel());
        if (driverByPeselOptional.isPresent()) {
            response.put("status","conflict-0009");
            response.put("message","Kierowca z numerem PESEL " + driver.getPesel() + " już istnieje w bazie danych");
        } else if(driver.getFirstName() != null && driver.getLastName() != null && driver.getPesel() != null && driver.getDrvLicNo() != null && driver.getBirthdate() != null){
            driverRepository.save(driver);
            response.put("status","success");
            response.put("message","Kierowca " + driver.getFirstName() + " " + driver.getLastName() + " został dodany do bazy danych");
        }
        else{
            response.put("status", "conflict-0010");
            response.put("message", "Proszę uzupełnić wszystkie wymagane pola");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> updateDriver(Long drvId, String firstName, String lastName, LocalDate birthdate, Long pesel, String drvLicNo, Integer overallDrvRating) {
        Map<String, Object> response = new HashMap<>();

        Optional<Driver> driverById = driverRepository.findById(drvId);
        if(driverById.isEmpty()){
            response.put("status","record-not-found-0015");
            response.put("message","Kierowca z numerem ID " + drvId + " nie istnieje w bazie danych");
        }else{
            Driver driver = driverById.get();
            boolean modifyFlag = false;

            if (firstName != null && !Objects.equals(firstName, driver.getFirstName())) {
                driver.setFirstName(firstName);
                modifyFlag = true;
            }

            if (lastName != null && !Objects.equals(lastName, driver.getLastName())) {
                driver.setLastName(lastName);
                modifyFlag = true;
            }

            if (birthdate != null && !Objects.equals(birthdate, driver.getBirthdate())) {
                driver.setBirthdate(birthdate);
                modifyFlag = true;
            }

            if (pesel != null && !Objects.equals(pesel, driver.getPesel())) {
                driver.setPesel(pesel);
                modifyFlag = true;
            }

            if (drvLicNo != null && !Objects.equals(drvLicNo, driver.getDrvLicNo())) {
                driver.setDrvLicNo(drvLicNo);
                modifyFlag = true;
            }

            if (overallDrvRating != null && !Objects.equals(overallDrvRating, driver.getOverallDrvRating())) {
                driver.setOverallDrvRating(overallDrvRating);
                modifyFlag = true;
            }

            if(modifyFlag == false){
                response.put("status", "conflict-0010");
                response.put("message", "Żadna z wartości nie została zmieniona");
            }
            else {
                response.put("status", "success");
                response.put("message", "Dane kierowcy " + driver.getFirstName() + " " + driver.getLastName() + " zostały zmienione");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<Object> findDrivers(String firstName, String lastName, Long pesel, String drvLicNo, Integer overallDrvRating, String LessOrMore, Integer batch) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Pageable pages = PageRequest.of(batch - 1,10, Sort.by("drvId"));

        if (overallDrvRating != null && LessOrMore != null) {
            switch (LessOrMore) {
                case "More" -> {
                    Page<Driver> drivers = driverRepository.findDriversMore(pesel, firstName, lastName, drvLicNo, overallDrvRating, pages);
                    response.put("status","success");
                    data.put("drivers", drivers.getContent());
                    data.put("size",drivers.getTotalPages());
                    response.put("message", "Dane przekazane poprawnie");
                    response.put("data", data);
                }
                case "Less" -> {
                    Page<Driver> drivers = driverRepository.findDriversLess(pesel, firstName, lastName, drvLicNo, overallDrvRating, pages);
                    response.put("status","success");
                    data.put("drivers", drivers.getContent());
                    data.put("size",drivers.getTotalPages());
                    response.put("message", "Dane przekazane poprawnie");
                    response.put("data", data);
                }
                default -> {
                    response.put("status", "unknown-0002");
                    response.put("message", "Invalid overallDrvRating operator");
                }
            }
        }else {
            Page<Driver> drivers = driverRepository.findDriversByAll(pesel, firstName, lastName, drvLicNo, pages);
            response.put("status","success");
            data.put("drivers", drivers.getContent());
            data.put("size",drivers.getTotalPages());
            response.put("message", "Dane przekazane poprawnie");
            response.put("data", data);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




}
