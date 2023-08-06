package main.Errand;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table
public class Errand {
    @Id
    @SequenceGenerator(
            name = "errand_sequence",
            sequenceName = "errand_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "errand_sequence"
    )
    private Long errandId;
    private Long carId;
    private Long drvId;
    private String plannedRoute;    //1-2-3-4... tak latwiej przechowywac w tablicy sql

    public Errand(Long errandId, Long carId, Long drvId, String plannedRoute) {
        this.errandId = errandId;
        this.carId = carId;
        this.drvId = drvId;
        this.plannedRoute = plannedRoute;
    }

    public Errand() {
    }

    public Long getErrandId() {
        return errandId;
    }

    public void setErrandId(Long errandId) {
        this.errandId = errandId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getDrvId() {
        return drvId;
    }

    public void setDrvId(Long drvId) {
        this.drvId = drvId;
    }

    public List<Long> getPlannedRouteAsList() {
        List<String> routeStringList = new ArrayList<String>(Arrays.asList(this.plannedRoute.split("-")));  //convert String to List<String>
        List<Long> routeList = routeStringList.stream().map(s->Long.parseLong(s)).collect(Collectors.toList());   //convert List<String> to List<Long>
        return routeList;
    }

    public String getPlannedRouteAsString(){
        return this.plannedRoute;
    }

    public void setPlannedRouteAsList(List<Long> plannedRoute) {
        String newRoute = String.join("-", (CharSequence) plannedRoute);
        this.plannedRoute = newRoute;
    }

    public void setPlannedRouteAsString(String plannedRoute){
        this.plannedRoute = plannedRoute;
    }
}
