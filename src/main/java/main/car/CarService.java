package main.car;



import main.Driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class CarService {

    public static CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    private List<Car> calculateBatch(Integer batchNumber,List<Car> table){
        int from = 15 * (batchNumber - 1);
        int to = 15 * batchNumber;
        if(table.size() > from + to && table.size() > from){
            table = table.subList(from, to);
        } else if (table.size() > from) {
            table = table.subList(from, table.size());
        } else {
            throw new IllegalStateException("batch is empty");
        }
        return table;
    }


    public List<Car> getCarsBatch(Integer batchNumber){
        List<Car> table = carRepository.findAll();
        return calculateBatch(batchNumber, table);
    }

    public List<Car> getBatchCarsSorted(String sortingType,String column,Integer batchNumber){
        List<Car> table = carRepository.findAll();
        if (sortingType.equals("ascending")){
            switch (column) {
                case "carId" -> table.sort(Comparator.comparing(Car::getCarId));
                case "make" -> table.sort(Comparator.comparing(Car::getMake));
                case "model" -> table.sort(Comparator.comparing(Car::getModel));
                case "vin" -> table.sort(Comparator.comparing(Car::getVin));
                case "plateNo" -> table.sort(Comparator.comparing(Car::getPlateNo));
                case "type" -> table.sort(Comparator.comparing(Car::getType));
                case "comment" -> table.sort(Comparator.comparing(Car::getComment));
                case "serviceDate" -> table.sort(Comparator.comparing(Car::getServiceDate));
                case "serviceMileage" -> table.sort(Comparator.comparing(Car::getServiceMileage));
                default -> throw new IllegalStateException("There is no column named '" + column + "' in Car table");
            }
        }else if (sortingType.equals("descending")){
            switch (column) {
                case "carId" -> table.sort(Comparator.comparing(Car::getCarId).reversed());
                case "make" -> table.sort(Comparator.comparing(Car::getMake).reversed());
                case "model" -> table.sort(Comparator.comparing(Car::getModel).reversed());
                case "vin" -> table.sort(Comparator.comparing(Car::getVin).reversed());
                case "plateNo" -> table.sort(Comparator.comparing(Car::getPlateNo).reversed());
                case "type" -> table.sort(Comparator.comparing(Car::getType).reversed());
                case "comment" -> table.sort(Comparator.comparing(Car::getComment).reversed());
                case "serviceDate" -> table.sort(Comparator.comparing(Car::getServiceDate).reversed());
                case "serviceMileage" -> table.sort(Comparator.comparing(Car::getServiceMileage).reversed());
                default -> throw new IllegalStateException("There is no column named '" + column + "' in Car table");
            }
        }else {
            throw new IllegalStateException("Sorting type named '" + sortingType + "' is invalid");
        }
        return table;
    }

    public List<Car> findCarByServiceMileage(Long value, Integer batchNumber) {
        List<Car> table = carRepository.findCarsByServiceMileage(value);
        return calculateBatch(batchNumber,table);

    }
    public List<Car> findCarsByPattern(String column, String pattern,Integer batchNumber) {
        List<Car> table;
        switch (column) {
            case "make" -> {
                table = carRepository.findCarsByMake(pattern);
            }
            case "model" -> {
                table = carRepository.findCarsByModel(pattern);
            }
            case "vin" -> {
                table = carRepository.findCarsByVin(pattern);
            }
            case "plateNo" -> {
                table = carRepository.findCarsByPlateNo(pattern);
            }
            case "type" -> {
                table = carRepository.findCarsByType(pattern);
            }
            case "comment" -> {
                table = carRepository.findCarsByComment(pattern);
            }
            default -> throw new IllegalStateException("Column '" + column + "'is not valid for searching by pattern");
        }
        return calculateBatch(batchNumber,table);
    }

    public List<Car> findCarsByServiceDate(Integer batchNumber, Integer day, Integer month, Integer year) {
        List<Car> table;
        if(year != null){
            if(month != null){
                if(day != null){
                    LocalDate date = LocalDate.of(year,month,day);
                    table = carRepository.findCarsByServiceDate(date);
                }else{
                    table = carRepository.findCarsByMonthYear(month, year);
                }
            }else{
                if(day != null){
                    table = carRepository.findCarsByDayYear(day,year);
                }else{
                    table = carRepository.findCarsByYear(year);
                }
            }
        }else{
            if(month != null){
                if(day != null){
                    table = carRepository.findCarsByMonthDay(day,month);
                }else{
                    table = carRepository.findCarsByMonth(month);
                }
            }else{
                if(day != null){
                    table = carRepository.findCarsByDay(day);
                }else{
                    table = carRepository.findAll();
                }
            }
        }

        return calculateBatch(batchNumber,table);
    }


    public List<Car> getCars() {
        return carRepository.findAll();
    }

    public void addNewCar(Car car) {
        Optional<Car> carByVin = carRepository.findCarByVin(car.getVin());
        Optional<Car> carByPlate = carRepository.findCarByPlate(car.getPlateNo());
        if(carByVin.isPresent()){
            throw new IllegalStateException("Car with vin " + car.getVin() + " already exist");
        } else if (carByPlate.isPresent()) {
            throw new IllegalStateException("Car with plate number " + car.getPlateNo() + " already exist");
        }else {
            carRepository.save(car);
        }
    }

    public void deleteCar(Long carId) {
        boolean exists = carRepository.existsById(carId);
        if (!exists) {
            throw new IllegalStateException("Car with id" + carId + "does not exists");
        }
        carRepository.deleteById(carId);
    }

    @Transactional
    public void updateCar(Long carId, String make, String model, String vin, String plateNo,String type,String comment,LocalDate serviceDate, Long serviceMileage) {
        Car carById = carRepository.findById(carId).orElseThrow(
                () -> new IllegalStateException("Car with id " + carId + " does not exist")
        );
        int modifyFlag = 0;
        List<String> validTypes = Arrays.asList("car","truck");
        if(make != null && !Objects.equals(carById.getMake(),make)){
            carById.setMake(make);
            modifyFlag = 1;
        }

        if(model != null && !Objects.equals(carById.getModel(),model)){
            carById.setModel(model);
            modifyFlag = 1;
        }

        if(vin != null && !Objects.equals(carById.getVin(),vin)){
            carById.setVin(vin);
            modifyFlag = 1;
        }

        if(plateNo != null && !Objects.equals(carById.getPlateNo(),plateNo)){
            carById.setPlateNo(plateNo);
            modifyFlag = 1;
        }
        if(type != null && !Objects.equals(carById.getType(),type) && validTypes.contains(type)){
            carById.setType(type);
            modifyFlag = 1;
        }
        if(comment != null && !Objects.equals(carById.getComment(),comment)){
            carById.setComment(comment);
            modifyFlag = 1;
        }
        if(serviceDate != null && !Objects.equals(carById.getServiceDate(),serviceDate)){
            carById.setServiceDate(serviceDate);
            modifyFlag = 1;
        }
        if(serviceMileage != null && !Objects.equals(carById.getServiceMileage(),serviceMileage)){
            carById.setServiceMileage(serviceMileage);
            modifyFlag = 1;
        }

        if(modifyFlag == 0){
            throw new IllegalStateException("None of values was changed");
        }

    }
}
