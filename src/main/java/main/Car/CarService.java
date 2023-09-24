package main.Car;



import main.CarData.CarDataService;
import main.Driver.Driver;
import main.Driver.DriverService;
import main.Errand.Errand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static main.ErrandData.ErrandDataService.getCompletedPointsByErrandId;
import static main.Location.LocationService.getListOfRealAddresses;

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

    public static Optional<Car> getCarById(Long carId){
        return carRepository.findCarByCarId(carId);
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
        if(car.getMake() != null && car.getModel() != null && car.getVin() != null && car.getPlateNo() != null && car.getType() != null) {
            Optional<Car> carByVin = carRepository.findCarByVin(car.getVin());
            Optional<Car> carByPlate = carRepository.findCarByPlate(car.getPlateNo());
            if (carByVin.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Car with vin " + car.getVin() + " already exist");
            } else if (carByPlate.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Car with plate number " + car.getPlateNo() + " already exist");
            } else {
                carRepository.save(car);
                CarDataService.createNewCarDataRecord(car);
            }
        }
            else throw new ResponseStatusException(HttpStatus.CONFLICT, "Please fill in all required fields");
    }

    public void deleteCar(Long carId) {
        boolean exists = carRepository.existsById(carId);
        if (!exists) {
            throw new IllegalStateException("Car with id" + carId + "does not exists");
        }
        else {
            carRepository.deleteById(carId);
            //CarDataService.deleteCarDataRecord(carId);
        }
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

    public ResponseEntity<Object> searchCars(String makePart, String modelPart, String vinPart, String plateNumberPart, String typePart, Long serviceMileageLowerThreshold, Long serviceMileageUpperThreshold, LocalDate serviceDateLowerThreshold, LocalDate serviceDateUpperThreshold, Integer batchNumber){
        List<Car> allCars = carRepository.findAll();
        List<Car> matchedCars = new ArrayList<Car>();
        List<Car> responseCarList = new ArrayList<Car>();

        if(makePart == null)    makePart = "";
        if(modelPart == null)   modelPart = "";
        if(vinPart == null) vinPart = "";
        if(plateNumberPart == null) plateNumberPart = "";
        if(typePart == null)    typePart = "";

        for(Car car : allCars){
            Boolean carMatchesSearch = false;

            carMatchesSearch = car.getMake().contains(makePart) && car.getModel().contains(modelPart) && car.getVin().contains(vinPart)
                            && car.getPlateNo().contains(plateNumberPart) && car.getType().contains(typePart);

            if(car.getServiceDate() != null) {
                if (serviceDateLowerThreshold != null) {
                    carMatchesSearch = carMatchesSearch && car.getServiceDate().isAfter(serviceDateLowerThreshold);
                }
                if (serviceDateUpperThreshold != null) {
                    carMatchesSearch = carMatchesSearch && car.getServiceDate().isBefore(serviceDateUpperThreshold);
                }
            }
            if(car.getServiceMileage() != null) {
                if (serviceMileageLowerThreshold != null) {
                    carMatchesSearch = carMatchesSearch && car.getServiceMileage() > serviceMileageLowerThreshold;
                }
                if (serviceMileageUpperThreshold != null) {
                    carMatchesSearch = carMatchesSearch && car.getServiceMileage() < serviceMileageUpperThreshold;
                }
            }

            if (carMatchesSearch) matchedCars.add(car);
        }

        if(matchedCars.isEmpty())    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found");
        else {
            Integer startIndex = batchNumber * 10 - 10;
            Integer endIndex = batchNumber * 10;
            if (matchedCars.size() > startIndex + endIndex && matchedCars.size() > startIndex) {
                responseCarList = matchedCars.subList(startIndex, endIndex);
            } else if (matchedCars.size() > startIndex) {
                responseCarList = matchedCars.subList(startIndex, matchedCars.size());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch is empty");
            }
        }

        Integer numberOfBatches = (Integer) (matchedCars.size()/10) + 1;

        Map<String, Object> response = new HashMap<String, Object>();
        List<Object> listOfCars = new ArrayList<Object>();
        try{
            for(Car car : responseCarList){
                Map<String, Object> singleCarFields = new HashMap<String, Object>();
                singleCarFields.put("car", car);
                listOfCars.add(singleCarFields);
            }
            response.put("errands", listOfCars);
            response.put("size", numberOfBatches);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error");
        }
    }
}
