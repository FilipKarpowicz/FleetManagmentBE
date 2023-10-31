package main.Car;



import main.CarData.CarData;
import main.CarData.CarDataRepository;
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

@Service
public class CarService {

    private final CarRepository carRepository;
    private CarDataRepository carDataRepository;

    @Autowired
    public CarService(CarRepository carRepository, CarDataRepository carDataRepository) {
        this.carRepository = carRepository;
        this.carDataRepository = carDataRepository;
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

    public Optional<Car> getCarById(Long carId){
        return carRepository.findCarByCarId(carId);
    }

    public ResponseEntity<Object> getCarResponse(Long carId){
        Map<String, Object> response = new HashMap<>();
        Optional<Car> maybeCar = carRepository.findCarByCarId(carId);

        if(maybeCar.isEmpty()){
            response.put("status", "record-not-found-0006");
            response.put("message", "Pojazd z id " + carId + " nie istnieje w bazie danych");
            response.put("data", new Car());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            response.put("status", "success");
            response.put("message", "Dane przekazane poprawnie");
            response.put("data", maybeCar.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
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
                default -> throw new IllegalStateException("There is no column named " + column + " in Car table");
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

    public ResponseEntity<Object> addNewCar(Car car) {
        Map<String, Object> response = new HashMap<>();

        if(car.getMake() != null && car.getModel() != null && car.getVin() != null && car.getPlateNo() != null && car.getType() != null && car.getBattNominalCapacity() != null && car.getDevId() != null) {
            Optional<Car> carByVin = carRepository.findCarByVin(car.getVin());
            Optional<Car> carByPlate = carRepository.findCarByPlate(car.getPlateNo());
            if (carByVin.isPresent()) {
                response.put("status", "conflict-0001");
                response.put("message", "Numer VIN " + car.getVin() + " należy już do innego pojazdu w bazie danych");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (carByPlate.isPresent()) {
                response.put("status", "conflict-0002");
                response.put("message", "Numer rejestracyjny " + car.getPlateNo() + " należy już do innego pojazdu w bazie danych");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                carRepository.save(car);
                createNewCarDataRecord(car);
                response.put("status", "success");
                response.put("message", "Pojazd pomyślnie dodany do bazy");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        response.put("status", "conflict-0003");
        response.put("message", "Proszę uzupełnić wszystkie wymagane pola");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void createNewCarDataRecord(Car car){
        CarData carData = new CarData(car);
        car.setCarData(carData);
        carDataRepository.save(carData);
    }

    public ResponseEntity<Object> deleteCar(Long carId) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = carRepository.existsById(carId);
        if (!exists) {
            response.put("status", "record-not-found-0001");
            response.put("message", "Pojazd z id " + carId + " nie istnieje w bazie. Operacja nieudana");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            carRepository.deleteById(carId);
            response.put("status", "success");
            response.put("message", "Pojazd pomyślnie usunięty");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<Object> updateCar(Long carId, String make, String model, String vin, String plateNo,String type,String comment,LocalDate serviceDate, Long serviceMileage, Double battNominalCapacity, String devId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Car> maybeCarById = carRepository.findById(carId);
        if(maybeCarById.isEmpty()){
            response.put("status", "record-not-found-0002");
            response.put("message", "Pojazd z id " + carId + " nie istnieje w bazie. Operacja nieudana");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            Car carById = maybeCarById.get();
            int modifyFlag = 0;
            List<String> validTypes = Arrays.asList("car", "truck");
            if (make != null && !Objects.equals(carById.getMake(), make)) {
                carById.setMake(make);
                modifyFlag = 1;
            }

            if (model != null && !Objects.equals(carById.getModel(), model)) {
                carById.setModel(model);
                modifyFlag = 1;
            }

            if (vin != null && !Objects.equals(carById.getVin(), vin)) {
                carById.setVin(vin);
                modifyFlag = 1;
            }

            if (plateNo != null && !Objects.equals(carById.getPlateNo(), plateNo)) {
                carById.setPlateNo(plateNo);
                modifyFlag = 1;
            }
            if (type != null && !Objects.equals(carById.getType(), type) && validTypes.contains(type)) {
                carById.setType(type);
                modifyFlag = 1;
            }
            if (comment != null && !Objects.equals(carById.getComment(), comment)) {
                carById.setComment(comment);
                modifyFlag = 1;
            }
            if (serviceDate != null && !Objects.equals(carById.getServiceDate(), serviceDate)) {
                carById.setServiceDate(serviceDate);
                modifyFlag = 1;
            }
            if (serviceMileage != null && !Objects.equals(carById.getServiceMileage(), serviceMileage)) {
                carById.setServiceMileage(serviceMileage);
                modifyFlag = 1;
            }
            if (battNominalCapacity != null && !Objects.equals(carById.getBattNominalCapacity(), battNominalCapacity)) {
                carById.setBattNominalCapacity(battNominalCapacity);
                modifyFlag = 1;
            }
            if (devId != null && !Objects.equals(carById.getDevId(), devId)) {
                carById.setDevId(devId);
                modifyFlag = 1;
            }

            if (modifyFlag == 0) {
                response.put("status", "conflict-0004");
                response.put("message", "Żadna z wartości nie została zmieniona");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else{
                response.put("status", "success");
                response.put("message", "Pomyślnie zaktualizowano pojazd");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

    }

    public ResponseEntity<Object> searchCars(String makePart, String modelPart, String vinPart, String plateNumberPart, String typePart, Long serviceMileageLowerThreshold, Long serviceMileageUpperThreshold, LocalDate serviceDateLowerThreshold, LocalDate serviceDateUpperThreshold, Integer batchNumber){
        List<Car> allCars = carRepository.findAll();
        List<Car> matchedCars = new ArrayList<Car>();
        List<Car> responseCarList = new ArrayList<Car>();
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<>();

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

        if(matchedCars.isEmpty()){
            response.put("status", "success");
            response.put("message", "No results found");
            response.put("data", null);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        else {
            Collections.sort(matchedCars, new Comparator<Car>() {
                public int compare(Car o1, Car o2) {
                    // compare two instance of `Car` and return `int` as result.
                    return o1.getCarId().compareTo(o2.getCarId());
                }
            });
            Integer startIndex = batchNumber * 9 - 9;
            Integer endIndex = batchNumber * 9;
            if (matchedCars.size() > startIndex + endIndex && matchedCars.size() > startIndex) {
                responseCarList = matchedCars.subList(startIndex, endIndex);
            } else if (matchedCars.size() > startIndex) {
                responseCarList = matchedCars.subList(startIndex, matchedCars.size());
            } else {
                response.put("status", "empty-0002");
                response.put("message", "Pakiet danych pusty");
                response.put("data", null);
                return new ResponseEntity<Object>(response, HttpStatus.OK);
            }
        }

        Integer numberOfBatches = (Integer) (matchedCars.size()/9) + 1;

        try{
            data.put("cars", responseCarList);
            data.put("size", numberOfBatches);
            response.put("status", "success");
            response.put("message", "Search successful");
            response.put("data", data);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.put("status", "unknown-0002");
            response.put("message", "Unknown error");
            response.put("data", null);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
    }
}
