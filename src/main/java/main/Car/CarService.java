package main.Car;


import com.fasterxml.jackson.databind.ObjectMapper;
import main.CarData.CarData;
import main.CarData.CarDataRepository;
import main.CarData.CarDataService;
import main.Driver.Driver;
import main.Driver.DriverService;
import main.Errand.Errand;
import main.Errand.ErrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private ErrandRepository errandRepository;

    @Autowired
    public CarService(CarRepository carRepository, CarDataRepository carDataRepository, ErrandRepository errandRepository) {
        this.carRepository = carRepository;
        this.carDataRepository = carDataRepository;
        this.errandRepository = errandRepository;
    }

    private List<Car> calculateBatch(Integer batchNumber, List<Car> table) {
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


    public List<Car> getCarsBatch(Integer batchNumber) {
        List<Car> table = carRepository.findAll();
        return calculateBatch(batchNumber, table);
    }

    public Optional<Car> getCarById(Long carId) {
        return carRepository.findCarByCarId(carId);
    }

    public ResponseEntity<Object> getCarResponse(Long carId) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Optional<Car> maybeCar = carRepository.findCarByCarId(carId);
        Optional<CarData> maybeCarData = carDataRepository.findById(carId);
        data.put("carId", null);
        data.put("make", null);
        data.put("model", null);
        data.put("vin", null);
        data.put("plateNo", null);
        data.put("type", null);
        data.put("comment", null);
        data.put("serviceDate", null);
        data.put("serviceMileage", null);
        data.put("battNominalCapacity", null);
        data.put("devId", null);
        data.put("mileageToService", null);


        if (maybeCar.isEmpty() || maybeCarData.isEmpty()) {
            response.put("status", "data-not-found-0008");
            response.put("message", "Pojazd z id " + carId + " nie istnieje w bazie danych");
            response.put("data", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            Car car = maybeCar.get();
            CarData carData = maybeCarData.get();
            data.put("carId", car.getCarId());
            data.put("make", car.getMake());
            data.put("model", car.getModel());
            data.put("vin", car.getVin());
            data.put("plateNo", car.getPlateNo());
            data.put("type", car.getType());
            data.put("comment", car.getComment());
            data.put("serviceDate", car.getServiceDate());
            data.put("serviceMileage", car.getServiceMileage());
            data.put("battNominalCapacity", car.getBattNominalCapacity());
            data.put("devId", car.getDevId());
            data.put("mileageToService", (carData.getOverallMileage() != null && car.getServiceMileage() != null) ? car.getServiceMileage() - carData.getOverallMileage() : null);
            response.put("status", "success");
            response.put("message", "Dane przekazane poprawnie");
            response.put("data", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    public List<Car> getBatchCarsSorted(String sortingType, String column, Integer batchNumber) {
        List<Car> table = carRepository.findAll();
        if (sortingType.equals("ascending")) {
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
        } else if (sortingType.equals("descending")) {
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
        } else {
            throw new IllegalStateException("Sorting type named '" + sortingType + "' is invalid");
        }
        return table;
    }

    public List<Car> findCarByServiceMileage(Long value, Integer batchNumber) {
        List<Car> table = carRepository.findCarsByServiceMileage(value);
        return calculateBatch(batchNumber, table);

    }

    public List<Car> findCarsByPattern(String column, String pattern, Integer batchNumber) {
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
        return calculateBatch(batchNumber, table);
    }

    public List<Car> findCarsByServiceDate(Integer batchNumber, Integer day, Integer month, Integer year) {
        List<Car> table;
        if (year != null) {
            if (month != null) {
                if (day != null) {
                    LocalDate date = LocalDate.of(year, month, day);
                    table = carRepository.findCarsByServiceDate(date);
                } else {
                    table = carRepository.findCarsByMonthYear(month, year);
                }
            } else {
                if (day != null) {
                    table = carRepository.findCarsByDayYear(day, year);
                } else {
                    table = carRepository.findCarsByYear(year);
                }
            }
        } else {
            if (month != null) {
                if (day != null) {
                    table = carRepository.findCarsByMonthDay(day, month);
                } else {
                    table = carRepository.findCarsByMonth(month);
                }
            } else {
                if (day != null) {
                    table = carRepository.findCarsByDay(day);
                } else {
                    table = carRepository.findAll();
                }
            }
        }

        return calculateBatch(batchNumber, table);
    }


    public List<Car> getCars() {
        return carRepository.findAll();
    }

    public ResponseEntity<Object> addNewCar(Car car) {
        Map<String, Object> response = new HashMap<>();

        if (car.getMake() != null && car.getModel() != null && car.getVin() != null && car.getPlateNo() != null && car.getType() != null && car.getBattNominalCapacity() != null && car.getDevId() != null) {
            Optional<Car> carByVin = carRepository.findCarByVin(car.getVin());
            Optional<Car> carByPlate = carRepository.findCarByPlate(car.getPlateNo());
            Optional<Car> carByDevId = carRepository.findCarByDevId(car.getDevId());
            if (carByVin.isPresent()) {
                response.put("status", "conflict-0001");
                response.put("message", "Numer VIN " + car.getVin() + " należy już do innego pojazdu w bazie danych");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (carByPlate.isPresent()) {
                response.put("status", "conflict-0002");
                response.put("message", "Numer rejestracyjny " + car.getPlateNo() + " należy już do innego pojazdu w bazie danych");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (carByDevId.isPresent()) {
                response.put("status", "conflict-0019");
                response.put("message", "ID urządzenia " + car.getDevId() + " należy już do innego pojazdu w bazie danych");
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

    private void createNewCarDataRecord(Car car) {
        CarData carData = new CarData(car);
        car.setCarData(carData);
        carDataRepository.save(carData);
    }

    private String carIsAssignedToErrand(Long carId) {
        String errandId = null;
        List<Errand> listOfErrands = errandRepository.findAll();
        for (Errand errand : listOfErrands) {
            if (errand.getCarId().equals(carId)) errandId = errand.getErrandId();
        }
        return errandId;
    }

    public ResponseEntity<Object> deleteCar(Long carId) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = carRepository.existsById(carId);
        String carErrandId = carIsAssignedToErrand(carId);
        if (!exists) {
            response.put("status", "data-not-found-0009");
            response.put("message", "Pojazd z id " + carId + " nie istnieje w bazie. Operacja nieudana");
        } else if (carErrandId != null) {
            response.put("status", "conflict-0004");
            response.put("message", "Pojazd z id " + carId + " jest przypisany do zlecenia " + carErrandId + ". Nie można usunąć pojazdu");
        } else {
            carRepository.deleteById(carId);
            response.put("status", "success");
            response.put("message", "Pojazd pomyślnie usunięty");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> updateCar(Long carId, String make, String model, String vin, String plateNo, String type, String comment, LocalDate serviceDate, Long serviceMileage, Double battNominalCapacity, String devId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Car> carByPlateNo = carRepository.findCarByPlate(plateNo);
        Optional<Car> carByVin = carRepository.findCarByVin(vin);
        Optional<Car> carByDevId = carRepository.findCarByDevId(devId);

        Optional<Car> maybeCarById = carRepository.findById(carId);
        if (maybeCarById.isEmpty()) {
            response.put("status", "data-not-found-0010");
            response.put("message", "Pojazd z id " + carId + " nie istnieje w bazie. Operacja nieudana");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
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
                if (carByVin.isPresent()) {
                    response.put("status", "conflict-0020");
                    response.put("message", "Numer VIN " + vin + " jest już przypisany do innego pojazdu w bazie");
                } else {
                    carById.setVin(vin);
                    modifyFlag = 1;
                }
            }

            if (plateNo != null && !Objects.equals(carById.getPlateNo(), plateNo)) {
                if (carByPlateNo.isPresent()) {
                    response.put("status", "conflict-0021");
                    response.put("message", "Numer rejestracyjny " + plateNo + " jest już przypisany do innego pojazdu w bazie");
                } else {
                    carById.setPlateNo(plateNo);
                    modifyFlag = 1;
                }
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
                if (carByDevId.isPresent()) {
                    response.put("status", "conflict-0022");
                    response.put("message", "ID urządzenia " + devId + " jest już przypisany do innego pojazdu w bazie");
                } else {
                    carById.setDevId(devId);
                    modifyFlag = 1;
                }
            }

            if (modifyFlag == 0) {
                response.put("status", "conflict-0005");
                response.put("message", "Żadna z wartości nie została zmieniona");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "success");
                response.put("message", "Pomyślnie zaktualizowano pojazd");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

    }

    public ResponseEntity<Object> searchCars(String makePart, String modelPart, String vinPart, String plateNumberPart,
                                             String typePart, Long serviceMileageLowerThreshold, Long serviceMileageUpperThreshold,
                                             LocalDate serviceDateLowerThreshold, LocalDate serviceDateUpperThreshold, Double carMileageThreshold,
                                             String carMileageLessOrMore, Integer battSoc, String battSocLessOrMore, Integer batchNumber) {
        List<Car> allCars = carRepository.findAll();
        List<Car> matchedCars = new ArrayList<Car>();
        List<Car> responseCarList = new ArrayList<Car>();
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<>();

        if (makePart == null) makePart = "";
        if (modelPart == null) modelPart = "";
        if (vinPart == null) vinPart = "";
        if (plateNumberPart == null) plateNumberPart = "";
        if (typePart == null) typePart = "";

        for (Car car : allCars) {
            Boolean carMatchesSearch = false;
            CarData carData = carDataRepository.findById(car.getCarId()).get();

            carMatchesSearch = car.getMake().contains(makePart) && car.getModel().contains(modelPart) && car.getVin().contains(vinPart)
                    && car.getPlateNo().contains(plateNumberPart) && car.getType().contains(typePart);

            if (serviceDateLowerThreshold != null) {
                if (car.getServiceDate() != null)
                    carMatchesSearch = carMatchesSearch && car.getServiceDate().isAfter(serviceDateLowerThreshold);
                else carMatchesSearch = false;
            }
            if (serviceDateUpperThreshold != null) {
                if (car.getServiceDate() != null)
                    carMatchesSearch = carMatchesSearch && car.getServiceDate().isBefore(serviceDateUpperThreshold);
                else carMatchesSearch = false;
            }
            if (serviceMileageLowerThreshold != null) {
                if (car.getServiceMileage() != null)
                    carMatchesSearch = carMatchesSearch && car.getServiceMileage() > serviceMileageLowerThreshold;
                else carMatchesSearch = false;
            }
            if (serviceMileageUpperThreshold != null) {
                if (car.getServiceMileage() != null)
                    carMatchesSearch = carMatchesSearch && car.getServiceMileage() < serviceMileageUpperThreshold;
                else carMatchesSearch = false;
            }
            if (carMileageThreshold != null) {
                if (carData.getOverallMileage() != null) {
                    switch (carMileageLessOrMore) {
                        case "Less" -> {
                            carMatchesSearch = carMatchesSearch && carData.getOverallMileage() < carMileageThreshold;
                        }
                        case "More" -> {
                            carMatchesSearch = carMatchesSearch && carData.getOverallMileage() > carMileageThreshold;
                        }
                        default -> {
                        }
                    }
                } else carMatchesSearch = false;
            }
            if (battSoc != null) {
                if (carData.getBattSoc() != null) {
                    switch (battSocLessOrMore) {
                        case "Less" -> {
                            carMatchesSearch = carMatchesSearch && carData.getBattSoc() < battSoc;
                        }
                        case "More" -> {
                            carMatchesSearch = carMatchesSearch && carData.getBattSoc() > battSoc;
                        }
                        default -> {
                        }
                    }
                } else carMatchesSearch = false;
            }

            if (carMatchesSearch) matchedCars.add(car);
        }

        if (matchedCars.isEmpty()) {
            response.put("status", "success");
            response.put("message", "No results found");
            response.put("data", responseCarList);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        } else {
            Collections.sort(matchedCars, new Comparator<Car>() {
                public int compare(Car o1, Car o2) {
                    // compare two instance of `Car` and return `int` as result.
                    return o1.getCarId().compareTo(o2.getCarId());
                }
            });
            Integer startIndex = batchNumber * 9 - 9;
            Integer endIndex = batchNumber * 9;
            if (matchedCars.size() > endIndex) {
                responseCarList = matchedCars.subList(startIndex, endIndex);
            } else if (matchedCars.size() > startIndex) {
                responseCarList = matchedCars.subList(startIndex, matchedCars.size());
            } else {
                response.put("status", "empty-0002");
                response.put("message", "Pakiet danych pusty");
                response.put("data", responseCarList);
                return new ResponseEntity<Object>(response, HttpStatus.OK);
            }
        }

        Integer numberOfBatches = (Integer) ((matchedCars.size() - 1) / 9) + 1;

        try {
            data.put("cars", responseCarList);
            data.put("size", numberOfBatches);
            response.put("status", "success");
            response.put("message", "Search successful");
            response.put("data", data);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("status", "unknown-0002");
            response.put("message", "Unknown error");
            response.put("data", responseCarList);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> carsByName(String name) {
        List<Car> cars;
        if (name != null) {
            cars = carRepository.findCarByName(name);
        } else {
            Pageable pages = PageRequest.of(0, 8, Sort.by("carId"));
            Page<Car> carsPage = carRepository.get8Cars(pages);
            cars = carsPage.getContent();
        }
        if (cars.size() > 8) {
            cars.subList(0, 8);
        }
        Map<String, Object> response = new HashMap<>();
        List<Object> data = new ArrayList<>();
        for (Car car : cars) {
            Map<String, Object> carObject = new HashMap<>();
            carObject.put("id", car.getCarId());
            carObject.put("make", car.getMake());
            carObject.put("model", car.getModel());
            data.add(carObject);
        }
        response.put("data", data);
        response.put("status", "success");
        response.put("message", "Search successful");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
