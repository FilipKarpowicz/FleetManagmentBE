package main.Driver;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public List<Driver> calculateBatch(Integer batchNumber){
        List<Driver> table = driverRepository.findAll();
        int from = 15 * (batchNumber - 1);
        int to = 15 * batchNumber;
        if(table.size() > from + to && table.size() > from){
            table = table.subList(from, to);
        } else if (table.size() > from) {
            table = table.subList(from, table.size());
        } else {
            throw new IllegalStateException("batch is empty");
        }

        System.out.println(table);
        return table;
    }



    @Autowired
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public List<Driver> get15DriversSorted(String sortingType,String column,Integer batchNumber){
        List<Driver> batch = calculateBatch(batchNumber);
        if (sortingType.equals("ascending")){
            switch (column) {
                case "drvId" -> batch.sort(Comparator.comparing(Driver::getDrvId));
                case "firstName" -> batch.sort(Comparator.comparing(Driver::getFirstName));
                case "lastName" -> batch.sort(Comparator.comparing(Driver::getLastName));
                case "pesel" -> batch.sort(Comparator.comparing(Driver::getPesel));
                case "birthdate" -> batch.sort(Comparator.comparing(Driver::getBirthdate));
                case "drvLicNo" -> batch.sort(Comparator.comparing(Driver::getDrvLicNo));
                case "carId" -> batch.sort(Comparator.comparing(Driver::getCarId));
                case "overallDrvRating" -> batch.sort(Comparator.comparing(Driver::getOverallDrvRating));
                default -> throw new IllegalStateException("There is no column named '" + column + "' in Driver table");
            }
        }else if (sortingType.equals("descending")){
            switch (column) {
                case "drvId" -> batch.sort(Comparator.comparing(Driver::getDrvId).reversed());
                case "firstName" -> batch.sort(Comparator.comparing(Driver::getFirstName).reversed());
                case "lastName" -> batch.sort(Comparator.comparing(Driver::getLastName).reversed());
                case "pesel" -> batch.sort(Comparator.comparing(Driver::getPesel).reversed());
                case "birthdate" -> batch.sort(Comparator.comparing(Driver::getBirthdate).reversed());
                case "drvLicNo" -> batch.sort(Comparator.comparing(Driver::getDrvLicNo).reversed());
                case "carId" -> batch.sort(Comparator.comparing(Driver::getCarId).reversed());
                case "overallDrvRating" -> batch.sort(Comparator.comparing(Driver::getOverallDrvRating).reversed());
                default -> throw new IllegalStateException("There is no column named '" + column + "' in Driver table");
            }
        }else {
            throw new IllegalStateException("Sorting type named '" + sortingType + "' is invalid");
        }
        return batch;
    }

    public List<Driver> findDriverByValue(String column, Long value, Integer batchNumber) {
        List<Driver> table;

        switch (column){
            case "drvId" -> {
                table = driverRepository.findDriversByDrvId(Long.toString(value));
            }
            case "pesel" -> {
                table = driverRepository.findDriversByPesel(Long.toString(value));
            }
            case "carId" -> {
                table = driverRepository.findDriversByCarId(Long.toString(value));
            }
            case "overallDrvRating" -> {
                table = driverRepository.findDriversByOverallDrvRating(Long.toString(value));
            }
            default -> throw new IllegalStateException("Column '" + column + "'is not valid for searching by value");
        }
        int from = 15 * (batchNumber - 1);
        int to = 15 * batchNumber;
        return table.subList(from, to);

    }
    public List<Driver> findDriverByPattern(String column, String pattern,Integer batchNumber) {
        List<Driver> table;
        switch (column) {
            case "firstName" -> {
                table = driverRepository.driversLikeByFirstName(pattern);
            }
            case "lastName" -> {
                table = driverRepository.driversLikeByLastName(pattern);
            }
            case "drvLicNo" -> {
                table = driverRepository.driversLikeByDrvLicNo(pattern);
            }
            default -> throw new IllegalStateException("Column '" + column + "'is not valid for searching by pattern");
        }
        int from = 15 * (batchNumber - 1);
        int to = 15 * batchNumber;
        return table.subList(from, to);
    }

    public List<Driver> findDriversByBirthdate(Integer batchNumber, Integer day, Integer month, Integer year) {
        List<Driver> table;
        if(year != null){
            if(month != null){
                if(day != null){
                    LocalDate date = LocalDate.of(year,month,day);
                    table = driverRepository.findDriverByBirthDate(date);
                }else{
                    table = driverRepository.findDriverByMonthYear(month, year);
                }
            }else{
                if(day != null){
                    table = driverRepository.findDriverByDayYear(day,year);
                }else{
                    table = driverRepository.findDriverByYear(year);
                }
            }
        }else{
            if(month != null){
                if(day != null){
                    table = driverRepository.findDriverByMonthDay(day,month);
                }else{
                    table = driverRepository.findDriverByMonth(month);
                }
            }else{
                if(day != null){
                    table = driverRepository.findDriverByDay(day);
                }else{
                    table = driverRepository.findAll();
                }
            }
        }
        int from = 15 * (batchNumber - 1);
        int to = 15 * batchNumber;
        return table.subList(from, to);
    }

    public Optional<Driver> getDriverById(Long drvId){
        return driverRepository.findById(drvId);
    }


    public void deleteDriverById(Long drvId){
        driverRepository.deleteById(drvId);
    }

    public void addNewDriver(Driver driver){
        Optional <Driver> driverByPeselOptional = driverRepository.findDriverByPesel(driver.getPesel());
        Optional <Driver> driverByCarIdOptional = driverRepository.findDriverByCarId(driver.getCarId());
        if (driverByPeselOptional.isPresent()){
            throw new IllegalStateException("Driver with this pesel already exist");
        } else if (driverByCarIdOptional.isPresent()) {
            throw new IllegalStateException("Driver with this car ID already exist");
        } else {
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
